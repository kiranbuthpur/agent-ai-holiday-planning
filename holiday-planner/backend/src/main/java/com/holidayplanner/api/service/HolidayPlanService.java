package com.holidayplanner.api.service;

import com.holidayplanner.api.dto.*;
import com.holidayplanner.api.model.*;
import com.holidayplanner.api.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class HolidayPlanService {
    
    private final HolidayPlanRepository holidayPlanRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final ActivityRepository activityRepository;
    private final WeatherService weatherService;
    private final HolidayPlannerAIService aiService;
    private final EmailService emailService;
    
    public HolidayPlanService(HolidayPlanRepository holidayPlanRepository,
                             DailyPlanRepository dailyPlanRepository,
                             ActivityRepository activityRepository,
                             WeatherService weatherService,
                             HolidayPlannerAIService aiService,
                             EmailService emailService) {
        this.holidayPlanRepository = holidayPlanRepository;
        this.dailyPlanRepository = dailyPlanRepository;
        this.activityRepository = activityRepository;
        this.weatherService = weatherService;
        this.aiService = aiService;
        this.emailService = emailService;
    }
    
    /**
     * Create a new holiday plan with AI-generated activities
     */
    public Mono<HolidayPlanResponse> createHolidayPlan(HolidayPlanRequest request) {
        // Validate if similar plan already exists
        if (holidayPlanRepository.existsByUserEmailAndDestinationAndStartDate(
                request.getUserEmail(), request.getDestination(), request.getStartDate())) {
            return Mono.error(new IllegalArgumentException("A similar holiday plan already exists for this destination and date"));
        }
        
        // Create holiday plan entity
        HolidayPlan holidayPlan = new HolidayPlan(
            request.getDestination(),
            request.getStartDate(),
            request.getNumberOfDays(),
            request.getActivityCategory(),
            request.getUserEmail()
        );
        
        // Save the initial plan
        HolidayPlan savedPlan = holidayPlanRepository.save(holidayPlan);
        
        // Generate daily plans with weather integration
        return generateDailyPlansWithWeather(savedPlan)
                .map(this::convertToResponse);
    }
    
    /**
     * Generate daily plans with weather data and AI optimization
     */
    private Mono<HolidayPlan> generateDailyPlansWithWeather(HolidayPlan holidayPlan) {
        LocalDate startDate = holidayPlan.getStartDate();
        LocalDate endDate = holidayPlan.getEndDate();
        
        return weatherService.getWeatherForDateRange(holidayPlan.getDestination(), startDate, endDate)
                .map(weatherDataList -> {
                    // Generate daily plans for each day
                    for (int i = 0; i < holidayPlan.getNumberOfDays(); i++) {
                        LocalDate currentDate = startDate.plusDays(i);
                        WeatherData weatherForDay = weatherDataList.get(i);
                        
                        // Generate AI-optimized daily plan
                        DailyPlan dailyPlan = aiService.generateDailyPlan(
                            currentDate,
                            holidayPlan.getDestination(),
                            holidayPlan.getActivityCategory(),
                            weatherForDay
                        );
                        
                        dailyPlan.setHolidayPlan(holidayPlan);
                        holidayPlan.getDailyPlans().add(dailyPlan);
                    }
                    
                    // Update status to active
                    holidayPlan.setStatus(PlanStatus.ACTIVE);
                    
                    // Save the complete plan with daily plans and activities
                    HolidayPlan savedPlan = holidayPlanRepository.save(holidayPlan);
                    
                    // Send welcome email
                    emailService.sendHolidayPlanCreatedEmail(savedPlan);
                    
                    return savedPlan;
                });
    }
    
    /**
     * Get holiday plan by ID for a specific user
     */
    public Optional<HolidayPlanResponse> getHolidayPlan(Long id, String userEmail) {
        return holidayPlanRepository.findByIdAndUserEmail(id, userEmail)
                .map(this::convertToResponse);
    }
    
    /**
     * Get all holiday plans for a user
     */
    public List<HolidayPlanResponse> getUserHolidayPlans(String userEmail) {
        return holidayPlanRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Update holiday plan with new weather data and optimized activities
     */
    public Mono<HolidayPlanResponse> updateHolidayPlanWithWeather(Long planId, String userEmail) {
        Optional<HolidayPlan> planOpt = holidayPlanRepository.findByIdAndUserEmail(planId, userEmail);
        
        if (planOpt.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Holiday plan not found"));
        }
        
        HolidayPlan plan = planOpt.get();
        
        return weatherService.getWeatherForDateRange(plan.getDestination(), plan.getStartDate(), plan.getEndDate())
                .map(weatherDataList -> {
                    // Update each daily plan with new weather data
                    List<DailyPlan> dailyPlans = plan.getDailyPlans();
                    
                    for (int i = 0; i < dailyPlans.size() && i < weatherDataList.size(); i++) {
                        DailyPlan dailyPlan = dailyPlans.get(i);
                        WeatherData newWeather = weatherDataList.get(i);
                        
                        // Update weather information
                        updateDailyPlanWeather(dailyPlan, newWeather);
                        
                        // Optimize activities for new weather
                        List<Activity> optimizedActivities = aiService.optimizeActivitiesForWeather(
                            dailyPlan.getActivities(), newWeather);
                        
                        // Update activities
                        dailyPlan.getActivities().clear();
                        optimizedActivities.forEach(activity -> {
                            activity.setDailyPlan(dailyPlan);
                            dailyPlan.getActivities().add(activity);
                        });
                        
                        // Update recommendations and precautions
                        dailyPlan.setGeneralRecommendations(
                            generateGeneralRecommendations(newWeather, plan.getActivityCategory()));
                        dailyPlan.setPrecautions(
                            generatePrecautions(newWeather));
                    }
                    
                    HolidayPlan updatedPlan = holidayPlanRepository.save(plan);
                    
                    // Send update notification email
                    emailService.sendWeatherUpdateEmail(updatedPlan);
                    
                    return convertToResponse(updatedPlan);
                });
    }
    
    /**
     * Update daily plan weather information
     */
    private void updateDailyPlanWeather(DailyPlan dailyPlan, WeatherData weather) {
        dailyPlan.setWeatherSummary(weather.getDescription());
        dailyPlan.setTemperature(weather.getTemperature());
        dailyPlan.setWeatherCondition(weather.getCondition());
        dailyPlan.setHumidity(weather.getHumidity());
        dailyPlan.setWindSpeed(weather.getWindSpeed());
        dailyPlan.preUpdate(); // Update the last updated timestamp
    }
    
    /**
     * Cancel holiday plan
     */
    public boolean cancelHolidayPlan(Long planId, String userEmail) {
        Optional<HolidayPlan> planOpt = holidayPlanRepository.findByIdAndUserEmail(planId, userEmail);
        
        if (planOpt.isEmpty()) {
            return false;
        }
        
        HolidayPlan plan = planOpt.get();
        plan.setStatus(PlanStatus.CANCELLED);
        holidayPlanRepository.save(plan);
        
        // Send cancellation email
        emailService.sendHolidayPlanCancelledEmail(plan);
        
        return true;
    }
    
    /**
     * Get active holiday plans for today (for scheduled weather monitoring)
     */
    public List<HolidayPlan> getActivePlansForToday() {
        return holidayPlanRepository.findActivePlansForDate(LocalDate.now(), PlanStatus.ACTIVE);
    }
    
    /**
     * Process daily weather updates for all active plans
     */
    public void processDailyWeatherUpdates() {
        List<HolidayPlan> activePlans = getActivePlansForToday();
        
        for (HolidayPlan plan : activePlans) {
            try {
                updateHolidayPlanWithWeather(plan.getId(), plan.getUserEmail())
                        .subscribe(); // Async processing
            } catch (Exception e) {
                // Log error but continue processing other plans
                System.err.println("Failed to update plan " + plan.getId() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Get daily plan for specific date
     */
    public Optional<DailyPlanResponse> getDailyPlan(Long planId, LocalDate date, String userEmail) {
        Optional<HolidayPlan> planOpt = holidayPlanRepository.findByIdAndUserEmail(planId, userEmail);
        
        if (planOpt.isEmpty()) {
            return Optional.empty();
        }
        
        return dailyPlanRepository.findByHolidayPlanIdAndDate(planId, date)
                .map(this::convertDailyPlanToResponse);
    }
    
    /**
     * Get activity categories available for selection
     */
    public List<String> getAvailableActivityCategories() {
        return List.of(
            "Adventure",
            "Cultural",
            "Relaxation",
            "Sports",
            "Entertainment",
            "Dining",
            "Shopping",
            "Sightseeing"
        );
    }
    
    /**
     * Convert entity to response DTO
     */
    private HolidayPlanResponse convertToResponse(HolidayPlan holidayPlan) {
        HolidayPlanResponse response = new HolidayPlanResponse();
        BeanUtils.copyProperties(holidayPlan, response);
        
        // Convert daily plans
        List<DailyPlanResponse> dailyPlanResponses = holidayPlan.getDailyPlans()
                .stream()
                .map(this::convertDailyPlanToResponse)
                .collect(Collectors.toList());
        
        response.setDailyPlans(dailyPlanResponses);
        return response;
    }
    
    /**
     * Convert daily plan entity to response DTO
     */
    private DailyPlanResponse convertDailyPlanToResponse(DailyPlan dailyPlan) {
        DailyPlanResponse response = new DailyPlanResponse();
        BeanUtils.copyProperties(dailyPlan, response);
        
        // Convert activities
        List<ActivityResponse> activityResponses = dailyPlan.getActivities()
                .stream()
                .map(this::convertActivityToResponse)
                .collect(Collectors.toList());
        
        response.setActivities(activityResponses);
        return response;
    }
    
    /**
     * Convert activity entity to response DTO
     */
    private ActivityResponse convertActivityToResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        BeanUtils.copyProperties(activity, response);
        return response;
    }
    
    /**
     * Generate precautions based on weather - delegated to AI service
     */
    private String generatePrecautions(WeatherData weather) {
        return aiService.generatePrecautions(weather);
    }
    
    /**
     * Generate general recommendations - delegated to AI service
     */
    private String generateGeneralRecommendations(WeatherData weather, String activityCategory) {
        return aiService.generateGeneralRecommendations(weather, activityCategory);
    }
}
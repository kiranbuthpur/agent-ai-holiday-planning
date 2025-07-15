package com.holidayplanner.api.service;

import com.holidayplanner.api.model.HolidayPlan;
import com.holidayplanner.api.model.DailyPlan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledTaskService {
    
    private final HolidayPlanService holidayPlanService;
    private final EmailService emailService;
    
    public ScheduledTaskService(HolidayPlanService holidayPlanService, EmailService emailService) {
        this.holidayPlanService = holidayPlanService;
        this.emailService = emailService;
    }
    
    /**
     * Daily weather update task - runs every day at 6 AM
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void dailyWeatherUpdate() {
        try {
            System.out.println("Starting daily weather update task...");
            holidayPlanService.processDailyWeatherUpdates();
            System.out.println("Daily weather update task completed successfully");
        } catch (Exception e) {
            System.err.println("Daily weather update task failed: " + e.getMessage());
        }
    }
    
    /**
     * Morning reminder emails - runs every day at 7 AM
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void sendMorningReminders() {
        try {
            System.out.println("Sending morning reminder emails...");
            
            List<HolidayPlan> activePlans = holidayPlanService.getActivePlansForToday();
            LocalDate today = LocalDate.now();
            
            for (HolidayPlan plan : activePlans) {
                // Find today's daily plan
                DailyPlan todaysPlan = plan.getDailyPlans().stream()
                        .filter(dp -> dp.getDate().equals(today))
                        .findFirst()
                        .orElse(null);
                
                if (todaysPlan != null) {
                    emailService.sendDailyReminderEmail(plan, todaysPlan);
                }
            }
            
            System.out.println("Morning reminder emails sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send morning reminders: " + e.getMessage());
        }
    }
    
    /**
     * Weather monitoring task - runs every 3 hours to check for weather changes
     */
    @Scheduled(fixedRate = 3 * 60 * 60 * 1000) // 3 hours in milliseconds
    public void weatherMonitoringTask() {
        try {
            System.out.println("Running weather monitoring check...");
            
            List<HolidayPlan> activePlans = holidayPlanService.getActivePlansForToday();
            
            // For each active plan, check if weather has significantly changed
            for (HolidayPlan plan : activePlans) {
                // This could be enhanced to only update if weather has changed significantly
                // For now, we'll do a light update check
                checkAndUpdateWeatherIfNeeded(plan);
            }
            
            System.out.println("Weather monitoring check completed");
        } catch (Exception e) {
            System.err.println("Weather monitoring task failed: " + e.getMessage());
        }
    }
    
    /**
     * Check and update weather if needed (lightweight check)
     */
    private void checkAndUpdateWeatherIfNeeded(HolidayPlan plan) {
        try {
            LocalDate today = LocalDate.now();
            
            // Find today's plan
            DailyPlan todaysPlan = plan.getDailyPlans().stream()
                    .filter(dp -> dp.getDate().equals(today))
                    .findFirst()
                    .orElse(null);
            
            if (todaysPlan != null) {
                // Check if last update was more than 3 hours ago
                if (todaysPlan.getLastUpdated().isBefore(java.time.LocalDateTime.now().minusHours(3))) {
                    // Trigger update - this will also send email if weather changed significantly
                    holidayPlanService.updateHolidayPlanWithWeather(plan.getId(), plan.getUserEmail())
                            .subscribe(); // Async processing
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to check weather for plan " + plan.getId() + ": " + e.getMessage());
        }
    }
    
    /**
     * Cleanup task - runs daily at midnight to clean up completed plans
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupCompletedPlans() {
        try {
            System.out.println("Running cleanup task for completed plans...");
            
            // This could be enhanced to mark plans as completed when the end date has passed
            // For now, just log that the task ran
            
            System.out.println("Cleanup task completed");
        } catch (Exception e) {
            System.err.println("Cleanup task failed: " + e.getMessage());
        }
    }
}
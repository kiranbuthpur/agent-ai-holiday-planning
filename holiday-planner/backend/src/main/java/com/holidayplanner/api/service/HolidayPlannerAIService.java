package com.holidayplanner.api.service;

import com.holidayplanner.api.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HolidayPlannerAIService {
    
    private final WeatherService weatherService;
    
    public HolidayPlannerAIService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }
    
    /**
     * Generate intelligent daily plan based on weather and user preferences
     */
    public DailyPlan generateDailyPlan(LocalDate date, String destination, String activityCategory, WeatherData weather) {
        DailyPlan dailyPlan = new DailyPlan();
        dailyPlan.setDate(date);
        dailyPlan.setWeatherSummary(weather.getDescription());
        dailyPlan.setTemperature(weather.getTemperature());
        dailyPlan.setWeatherCondition(weather.getCondition());
        dailyPlan.setHumidity(weather.getHumidity());
        dailyPlan.setWindSpeed(weather.getWindSpeed());
        
        // Generate AI recommendations
        dailyPlan.setGeneralRecommendations(generateGeneralRecommendations(weather, activityCategory));
        dailyPlan.setPrecautions(generatePrecautions(weather));
        
        // Generate activities based on weather conditions
        List<Activity> activities = generateWeatherOptimizedActivities(weather, activityCategory, destination);
        
        // Assign activities to the daily plan
        for (Activity activity : activities) {
            dailyPlan.addActivity(activity);
        }
        
        return dailyPlan;
    }
    
    /**
     * Generate weather-optimized activities
     */
    private List<Activity> generateWeatherOptimizedActivities(WeatherData weather, String activityCategory, String destination) {
        List<Activity> activities = new ArrayList<>();
        
        // Morning activities (6 AM - 12 PM)
        Activity morningActivity = generateMorningActivity(weather, activityCategory, destination);
        if (morningActivity != null) {
            activities.add(morningActivity);
        }
        
        // Afternoon activities (12 PM - 6 PM)
        Activity afternoonActivity = generateAfternoonActivity(weather, activityCategory, destination);
        if (afternoonActivity != null) {
            activities.add(afternoonActivity);
        }
        
        // Evening activities (6 PM - 11 PM)
        Activity eveningActivity = generateEveningActivity(weather, activityCategory, destination);
        if (eveningActivity != null) {
            activities.add(eveningActivity);
        }
        
        return activities;
    }
    
    private Activity generateMorningActivity(WeatherData weather, String activityCategory, String destination) {
        Activity activity = new Activity();
        activity.setTimeOfDay(TimeOfDay.MORNING);
        activity.setStartTime(LocalTime.of(8, 0));
        activity.setEndTime(LocalTime.of(11, 30));
        
        if (weather.isGoodForOutdoorActivities()) {
            // Perfect weather for outdoor morning activities
            switch (activityCategory.toLowerCase()) {
                case "adventure":
                    activity.setName("Morning Hiking Adventure");
                    activity.setDescription("Explore scenic hiking trails and enjoy the fresh morning air");
                    activity.setType(ActivityType.ADVENTURE);
                    activity.setLocation("Local hiking trails near " + destination);
                    activity.setEquipmentNeeded("Hiking boots, water bottle, backpack");
                    break;
                case "cultural":
                    activity.setName("Historic District Walking Tour");
                    activity.setDescription("Guided walking tour of historic sites and cultural landmarks");
                    activity.setType(ActivityType.CULTURAL);
                    activity.setLocation("Historic district of " + destination);
                    activity.setEquipmentNeeded("Comfortable walking shoes, camera");
                    break;
                case "sports":
                    activity.setName("Morning Cycling Tour");
                    activity.setDescription("Scenic cycling route through the best spots in the city");
                    activity.setType(ActivityType.SPORTS);
                    activity.setLocation("City cycling paths in " + destination);
                    activity.setEquipmentNeeded("Bicycle rental, helmet, water");
                    break;
                default:
                    activity.setName("Morning Sightseeing");
                    activity.setDescription("Visit top attractions and photo spots");
                    activity.setType(ActivityType.SIGHTSEEING);
                    activity.setLocation("Main attractions in " + destination);
                    activity.setEquipmentNeeded("Camera, map");
            }
            activity.setPriority(ActivityPriority.HIGH);
        } else if (weather.isRainy() || weather.isHot()) {
            // Indoor activities for bad weather
            activity.setName("Museum and Gallery Visit");
            activity.setDescription("Explore local museums and art galleries");
            activity.setType(ActivityType.CULTURAL);
            activity.setLocation("Museums in " + destination);
            activity.setEquipmentNeeded("None");
            activity.setPriority(ActivityPriority.MEDIUM);
            
            if (weather.isRainy()) {
                activity.setWeatherRequirements("Indoor activity - perfect for rainy weather");
            } else {
                activity.setWeatherRequirements("Air-conditioned indoor space - escape the heat");
            }
        } else {
            // Mild outdoor activities for moderate weather
            activity.setName("Leisurely Morning Stroll");
            activity.setDescription("Gentle walk through parks and gardens");
            activity.setType(ActivityType.RELAXATION);
            activity.setLocation("Parks and gardens in " + destination);
            activity.setEquipmentNeeded("Comfortable shoes");
            activity.setPriority(ActivityPriority.MEDIUM);
        }
        
        activity.setEstimatedCost(calculateEstimatedCost(activity.getType()));
        return activity;
    }
    
    private Activity generateAfternoonActivity(WeatherData weather, String activityCategory, String destination) {
        Activity activity = new Activity();
        activity.setTimeOfDay(TimeOfDay.AFTERNOON);
        activity.setStartTime(LocalTime.of(13, 0));
        activity.setEndTime(LocalTime.of(17, 0));
        
        if (weather.isHot()) {
            // Hot weather - indoor or water activities
            if (activityCategory.toLowerCase().contains("adventure")) {
                activity.setName("Indoor Rock Climbing");
                activity.setDescription("Challenging indoor climbing experience");
                activity.setType(ActivityType.ADVENTURE);
                activity.setLocation("Indoor climbing center in " + destination);
                activity.setEquipmentNeeded("Climbing gear (provided), athletic wear");
            } else {
                activity.setName("Shopping and Lunch");
                activity.setDescription("Explore local markets and enjoy air-conditioned shopping");
                activity.setType(ActivityType.SHOPPING);
                activity.setLocation("Shopping centers in " + destination);
                activity.setEquipmentNeeded("Comfortable walking shoes");
            }
            activity.setWeatherRequirements("Indoor activity - escape the afternoon heat");
            activity.setPriority(ActivityPriority.HIGH);
        } else if (weather.isRainy()) {
            // Rainy weather - indoor activities
            activity.setName("Food Tasting Experience");
            activity.setDescription("Culinary tour and food tasting at local restaurants");
            activity.setType(ActivityType.DINING);
            activity.setLocation("Local restaurants in " + destination);
            activity.setEquipmentNeeded("Appetite and curiosity");
            activity.setWeatherRequirements("Indoor dining - perfect for rainy afternoons");
            activity.setPriority(ActivityPriority.HIGH);
        } else {
            // Good weather - outdoor activities
            switch (activityCategory.toLowerCase()) {
                case "adventure":
                    activity.setName("Outdoor Adventure Sports");
                    activity.setDescription("Kayaking, zip-lining, or other adventure sports");
                    activity.setType(ActivityType.ADVENTURE);
                    activity.setLocation("Adventure sports center near " + destination);
                    activity.setEquipmentNeeded("Provided by operator, bring change of clothes");
                    break;
                case "cultural":
                    activity.setName("Local Market and Artisan Tour");
                    activity.setDescription("Visit local markets and meet traditional artisans");
                    activity.setType(ActivityType.CULTURAL);
                    activity.setLocation("Local markets in " + destination);
                    activity.setEquipmentNeeded("Cash for purchases, camera");
                    break;
                default:
                    activity.setName("Scenic Viewpoint Tour");
                    activity.setDescription("Visit the best viewpoints and photo locations");
                    activity.setType(ActivityType.SIGHTSEEING);
                    activity.setLocation("Scenic viewpoints around " + destination);
                    activity.setEquipmentNeeded("Camera, comfortable shoes");
            }
            activity.setPriority(ActivityPriority.HIGH);
        }
        
        activity.setEstimatedCost(calculateEstimatedCost(activity.getType()));
        return activity;
    }
    
    private Activity generateEveningActivity(WeatherData weather, String activityCategory, String destination) {
        Activity activity = new Activity();
        activity.setTimeOfDay(TimeOfDay.EVENING);
        activity.setStartTime(LocalTime.of(18, 30));
        activity.setEndTime(LocalTime.of(22, 0));
        
        // Evening activities are typically less weather-dependent
        switch (activityCategory.toLowerCase()) {
            case "cultural":
                activity.setName("Traditional Performance Show");
                activity.setDescription("Enjoy local traditional music and dance performances");
                activity.setType(ActivityType.ENTERTAINMENT);
                activity.setLocation("Cultural center in " + destination);
                activity.setEquipmentNeeded("Camera (if allowed)");
                break;
            case "adventure":
                if (weather.isGoodForOutdoorActivities()) {
                    activity.setName("Sunset Adventure Tour");
                    activity.setDescription("Sunset viewing with light adventure activities");
                    activity.setType(ActivityType.ADVENTURE);
                    activity.setLocation("Scenic sunset location near " + destination);
                    activity.setEquipmentNeeded("Light jacket, camera");
                } else {
                    activity.setName("Evening Entertainment Complex");
                    activity.setDescription("Indoor entertainment and gaming complex");
                    activity.setType(ActivityType.ENTERTAINMENT);
                    activity.setLocation("Entertainment district in " + destination);
                    activity.setEquipmentNeeded("None");
                }
                break;
            default:
                activity.setName("Dinner and Local Nightlife");
                activity.setDescription("Experience local cuisine and evening atmosphere");
                activity.setType(ActivityType.DINING);
                activity.setLocation("Restaurant district in " + destination);
                activity.setEquipmentNeeded("Smart casual attire");
        }
        
        activity.setPriority(ActivityPriority.MEDIUM);
        activity.setEstimatedCost(calculateEstimatedCost(activity.getType()));
        
        if (weather.isCold()) {
            activity.setWeatherRequirements("Bring warm clothing for evening activities");
        }
        
        return activity;
    }
    
    public String generateGeneralRecommendations(WeatherData weather, String activityCategory) {
        StringBuilder recommendations = new StringBuilder();
        
        recommendations.append("Based on today's weather conditions: ");
        recommendations.append(weatherService.getWeatherRecommendations(weather));
        recommendations.append(" ");
        
        if (weather.isGoodForOutdoorActivities()) {
            recommendations.append("Perfect day for ").append(activityCategory).append(" activities outdoors! ");
            recommendations.append("Take advantage of the pleasant weather for sightseeing and adventure. ");
        } else if (weather.isRainy()) {
            recommendations.append("Indoor activities are recommended today. ");
            recommendations.append("Great opportunity to explore museums, galleries, and local cuisine. ");
        } else if (weather.isHot()) {
            recommendations.append("Schedule outdoor activities early morning or late evening. ");
            recommendations.append("Midday is perfect for indoor attractions and air-conditioned venues. ");
        }
        
        // Activity-specific recommendations
        switch (activityCategory.toLowerCase()) {
            case "adventure":
                if (weather.isGoodForOutdoorActivities()) {
                    recommendations.append("Ideal conditions for hiking, cycling, and outdoor sports. ");
                } else {
                    recommendations.append("Consider indoor adventure activities like rock climbing or adventure parks. ");
                }
                break;
            case "cultural":
                recommendations.append("Perfect day to explore local history, museums, and cultural sites. ");
                break;
            case "relaxation":
                recommendations.append("Great weather for spa activities and peaceful sightseeing. ");
                break;
        }
        
        return recommendations.toString().trim();
    }
    
    public String generatePrecautions(WeatherData weather) {
        StringBuilder precautions = new StringBuilder();
        
        if (weather.isRainy()) {
            precautions.append("• Carry waterproof clothing and umbrella\n");
            precautions.append("• Check if outdoor venues are open\n");
            precautions.append("• Allow extra travel time due to weather conditions\n");
        }
        
        if (weather.isHot()) {
            precautions.append("• Stay hydrated - carry water bottle\n");
            precautions.append("• Use sunscreen and wear hat\n");
            precautions.append("• Take breaks in shaded or air-conditioned areas\n");
            precautions.append("• Avoid strenuous outdoor activities during 11 AM - 3 PM\n");
        }
        
        if (weather.isCold()) {
            precautions.append("• Dress in warm layers\n");
            precautions.append("• Wear appropriate footwear for cold weather\n");
            precautions.append("• Check heating availability at outdoor venues\n");
        }
        
        if (weather.getWindSpeed() != null && weather.getWindSpeed() > 20) {
            precautions.append("• Secure loose items and clothing\n");
            precautions.append("• Be cautious near water bodies\n");
            precautions.append("• Check if outdoor activities are still operating\n");
        }
        
        if (weather.getUvIndex() != null && weather.getUvIndex() > 6) {
            precautions.append("• High UV index - use SPF 30+ sunscreen\n");
            precautions.append("• Wear UV-protective sunglasses\n");
            precautions.append("• Seek shade during peak sun hours\n");
        }
        
        if (precautions.length() == 0) {
            precautions.append("• No special precautions needed - enjoy your day!");
        }
        
        return precautions.toString().trim();
    }
    
    private Double calculateEstimatedCost(ActivityType type) {
        return switch (type) {
            case ADVENTURE -> 45.0 + (Math.random() * 30); // $45-75
            case CULTURAL -> 15.0 + (Math.random() * 20); // $15-35
            case DINING -> 25.0 + (Math.random() * 25); // $25-50
            case ENTERTAINMENT -> 20.0 + (Math.random() * 30); // $20-50
            case SHOPPING -> 10.0 + (Math.random() * 40); // $10-50 (variable)
            case SPORTS -> 30.0 + (Math.random() * 20); // $30-50
            case SIGHTSEEING -> 5.0 + (Math.random() * 15); // $5-20
            case RELAXATION -> 35.0 + (Math.random() * 40); // $35-75
            default -> 20.0 + (Math.random() * 20); // $20-40
        };
    }
    
    /**
     * Optimize activities based on weather changes
     */
    public List<Activity> optimizeActivitiesForWeather(List<Activity> originalActivities, WeatherData newWeather) {
        return originalActivities.stream()
                .map(activity -> optimizeActivityForWeather(activity, newWeather))
                .collect(Collectors.toList());
    }
    
    private Activity optimizeActivityForWeather(Activity originalActivity, WeatherData weather) {
        Activity optimizedActivity = new Activity();
        
        // Copy basic information
        optimizedActivity.setName(originalActivity.getName());
        optimizedActivity.setDescription(originalActivity.getDescription());
        optimizedActivity.setType(originalActivity.getType());
        optimizedActivity.setTimeOfDay(originalActivity.getTimeOfDay());
        optimizedActivity.setStartTime(originalActivity.getStartTime());
        optimizedActivity.setEndTime(originalActivity.getEndTime());
        optimizedActivity.setLocation(originalActivity.getLocation());
        optimizedActivity.setPriority(originalActivity.getPriority());
        optimizedActivity.setEstimatedCost(originalActivity.getEstimatedCost());
        
        // Adjust for weather conditions
        if (originalActivity.getType().isWeatherDependent()) {
            if (!weather.isGoodForOutdoorActivities()) {
                // Switch to indoor alternative
                optimizedActivity = createIndoorAlternative(originalActivity);
            }
        }
        
        // Update weather requirements and equipment
        optimizedActivity.setWeatherRequirements(weatherService.getWeatherRecommendations(weather));
        optimizedActivity.setEquipmentNeeded(updateEquipmentForWeather(originalActivity.getEquipmentNeeded(), weather));
        
        return optimizedActivity;
    }
    
    private Activity createIndoorAlternative(Activity outdoorActivity) {
        Activity indoor = new Activity();
        indoor.setTimeOfDay(outdoorActivity.getTimeOfDay());
        indoor.setStartTime(outdoorActivity.getStartTime());
        indoor.setEndTime(outdoorActivity.getEndTime());
        indoor.setPriority(outdoorActivity.getPriority());
        indoor.setEstimatedCost(outdoorActivity.getEstimatedCost());
        
        switch (outdoorActivity.getType()) {
            case ADVENTURE:
                indoor.setName("Indoor Adventure Experience");
                indoor.setDescription("Indoor rock climbing, escape rooms, or adventure simulators");
                indoor.setType(ActivityType.ADVENTURE);
                break;
            case SPORTS:
                indoor.setName("Indoor Sports Complex");
                indoor.setDescription("Bowling, indoor courts, or fitness activities");
                indoor.setType(ActivityType.SPORTS);
                break;
            case SIGHTSEEING:
                indoor.setName("Museum and Gallery Tour");
                indoor.setDescription("Explore local museums, galleries, and cultural centers");
                indoor.setType(ActivityType.CULTURAL);
                break;
            default:
                indoor.setName("Indoor Cultural Experience");
                indoor.setDescription("Museums, galleries, or cultural centers");
                indoor.setType(ActivityType.CULTURAL);
        }
        
        return indoor;
    }
    
    private String updateEquipmentForWeather(String originalEquipment, WeatherData weather) {
        Set<String> equipment = new HashSet<>();
        
        if (originalEquipment != null && !originalEquipment.isEmpty()) {
            equipment.add(originalEquipment);
        }
        
        if (weather.isRainy()) {
            equipment.add("Umbrella, waterproof jacket");
        }
        
        if (weather.isHot()) {
            equipment.add("Sunscreen, hat, water bottle");
        }
        
        if (weather.isCold()) {
            equipment.add("Warm clothing, layers");
        }
        
        if (weather.getUvIndex() != null && weather.getUvIndex() > 6) {
            equipment.add("Sunglasses, SPF 30+ sunscreen");
        }
        
        return String.join(", ", equipment);
    }
}
package com.holidayplanner.api.service;

import com.holidayplanner.api.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class WeatherService {
    
    private final WebClient webClient;
    private final Random random = new Random();
    
    @Value("${weather.api.key:demo-key}")
    private String weatherApiKey;
    
    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5}")
    private String weatherApiUrl;
    
    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    /**
     * Get current weather for a destination
     */
    public Mono<WeatherData> getCurrentWeather(String destination) {
        // For demo purposes, return mock data
        // In production, replace with actual API call
        return Mono.just(generateMockWeatherData(LocalDate.now(), destination));
        
        // Uncomment and configure for real OpenWeatherMap API:
        /*
        String url = String.format("%s/weather?q=%s&appid=%s&units=metric", 
                                   weatherApiUrl, destination, weatherApiKey);
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseCurrentWeatherResponse);
        */
    }
    
    /**
     * Get weather forecast for multiple days
     */
    public Mono<List<WeatherData>> getWeatherForecast(String destination, int days) {
        // For demo purposes, return mock data
        // In production, replace with actual API call
        List<WeatherData> forecast = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            forecast.add(generateMockWeatherData(date, destination));
        }
        
        return Mono.just(forecast);
        
        // Uncomment and configure for real OpenWeatherMap API:
        /*
        String url = String.format("%s/forecast?q=%s&appid=%s&units=metric&cnt=%d", 
                                   weatherApiUrl, destination, weatherApiKey, days * 8); // 8 forecasts per day (3-hour intervals)
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> parseForecastResponse(response, days));
        */
    }
    
    /**
     * Get weather for specific date range
     */
    public Mono<List<WeatherData>> getWeatherForDateRange(String destination, LocalDate startDate, LocalDate endDate) {
        List<WeatherData> weatherList = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            weatherList.add(generateMockWeatherData(current, destination));
            current = current.plusDays(1);
        }
        
        return Mono.just(weatherList);
    }
    
    /**
     * Generate mock weather data for demo purposes
     */
    private WeatherData generateMockWeatherData(LocalDate date, String destination) {
        WeatherData weather = new WeatherData();
        weather.setDate(date);
        weather.setLastUpdated(LocalDateTime.now());
        
        // Generate realistic weather based on season and destination
        int dayOfYear = date.getDayOfYear();
        boolean isSummer = dayOfYear > 150 && dayOfYear < 250;
        boolean isWinter = dayOfYear < 60 || dayOfYear > 300;
        
        // Base temperature on season
        double baseTemp = isSummer ? 25.0 : (isWinter ? 5.0 : 15.0);
        double variation = random.nextGaussian() * 5; // Random variation
        
        double temperature = baseTemp + variation;
        weather.setTemperature(temperature);
        weather.setTemperatureMin(temperature - 3);
        weather.setTemperatureMax(temperature + 5);
        
        // Generate weather conditions
        String[] conditions = {"Clear", "Partly Cloudy", "Cloudy", "Light Rain", "Rain", "Sunny"};
        String[] rainConditions = {"Light Rain", "Rain", "Heavy Rain", "Drizzle"};
        
        boolean isRainy = random.nextDouble() < 0.3; // 30% chance of rain
        String condition = isRainy ? 
            rainConditions[random.nextInt(rainConditions.length)] :
            conditions[random.nextInt(conditions.length)];
        
        weather.setCondition(condition);
        weather.setDescription(generateWeatherDescription(condition, temperature));
        
        // Other weather parameters
        weather.setHumidity(30 + random.nextInt(60)); // 30-90%
        weather.setWindSpeed(5.0 + random.nextDouble() * 15); // 5-20 km/h
        weather.setWindDirection(getRandomWindDirection());
        weather.setPrecipitation(isRainy ? random.nextDouble() * 10 : 0.0);
        weather.setVisibility(8 + random.nextInt(12)); // 8-20 km
        weather.setUvIndex(isSummer ? 6.0 + random.nextDouble() * 4 : random.nextDouble() * 5);
        
        // Set sunrise/sunset (simplified)
        weather.setSunrise(date.atTime(6, 0).plusMinutes(random.nextInt(120)));
        weather.setSunset(date.atTime(18, 0).plusMinutes(random.nextInt(120)));
        
        return weather;
    }
    
    private String generateWeatherDescription(String condition, double temperature) {
        StringBuilder desc = new StringBuilder();
        
        if (temperature > 30) {
            desc.append("Hot and ");
        } else if (temperature < 10) {
            desc.append("Cold and ");
        } else {
            desc.append("Pleasant and ");
        }
        
        desc.append(condition.toLowerCase());
        
        if (condition.contains("Rain")) {
            desc.append(". Bring an umbrella!");
        } else if (condition.equals("Sunny")) {
            desc.append(". Perfect for outdoor activities!");
        }
        
        return desc.toString();
    }
    
    private String getRandomWindDirection() {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[random.nextInt(directions.length)];
    }
    
    /**
     * Check if weather is suitable for outdoor activities
     */
    public boolean isGoodWeatherForOutdoorActivities(WeatherData weather) {
        return weather.isGoodForOutdoorActivities();
    }
    
    /**
     * Get weather recommendations based on conditions
     */
    public String getWeatherRecommendations(WeatherData weather) {
        StringBuilder recommendations = new StringBuilder();
        
        if (weather.isHot()) {
            recommendations.append("Hot weather - stay hydrated, seek shade during midday. ");
        }
        
        if (weather.isCold()) {
            recommendations.append("Cold weather - dress warmly, layer clothing. ");
        }
        
        if (weather.isRainy()) {
            recommendations.append("Rainy conditions - carry umbrella, plan indoor activities. ");
        }
        
        if (weather.getWindSpeed() != null && weather.getWindSpeed() > 20) {
            recommendations.append("Windy conditions - secure loose items, be careful near water. ");
        }
        
        if (weather.getUvIndex() != null && weather.getUvIndex() > 6) {
            recommendations.append("High UV index - use sunscreen, wear hat and sunglasses. ");
        }
        
        if (recommendations.length() == 0) {
            recommendations.append("Perfect weather for any outdoor activities!");
        }
        
        return recommendations.toString().trim();
    }
}
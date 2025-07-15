package com.holidayplanner.api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WeatherData {
    
    private LocalDate date;
    private Double temperature;
    private Double temperatureMin;
    private Double temperatureMax;
    private String condition;
    private String description;
    private Integer humidity;
    private Double windSpeed;
    private String windDirection;
    private Double precipitation;
    private Integer visibility;
    private Double uvIndex;
    private LocalDateTime sunrise;
    private LocalDateTime sunset;
    private LocalDateTime lastUpdated;
    
    // Constructors
    public WeatherData() {}
    
    public WeatherData(LocalDate date, Double temperature, String condition) {
        this.date = date;
        this.temperature = temperature;
        this.condition = condition;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getTemperatureMin() { return temperatureMin; }
    public void setTemperatureMin(Double temperatureMin) { this.temperatureMin = temperatureMin; }
    
    public Double getTemperatureMax() { return temperatureMax; }
    public void setTemperatureMax(Double temperatureMax) { this.temperatureMax = temperatureMax; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }
    
    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }
    
    public String getWindDirection() { return windDirection; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }
    
    public Double getPrecipitation() { return precipitation; }
    public void setPrecipitation(Double precipitation) { this.precipitation = precipitation; }
    
    public Integer getVisibility() { return visibility; }
    public void setVisibility(Integer visibility) { this.visibility = visibility; }
    
    public Double getUvIndex() { return uvIndex; }
    public void setUvIndex(Double uvIndex) { this.uvIndex = uvIndex; }
    
    public LocalDateTime getSunrise() { return sunrise; }
    public void setSunrise(LocalDateTime sunrise) { this.sunrise = sunrise; }
    
    public LocalDateTime getSunset() { return sunset; }
    public void setSunset(LocalDateTime sunset) { this.sunset = sunset; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    // Utility methods
    public boolean isGoodForOutdoorActivities() {
        return temperature != null && temperature > 15 && temperature < 35 &&
               (condition == null || !condition.toLowerCase().contains("rain")) &&
               (windSpeed == null || windSpeed < 20);
    }
    
    public boolean isRainy() {
        return condition != null && 
               (condition.toLowerCase().contains("rain") || 
                condition.toLowerCase().contains("drizzle") ||
                condition.toLowerCase().contains("shower"));
    }
    
    public boolean isHot() {
        return temperature != null && temperature > 30;
    }
    
    public boolean isCold() {
        return temperature != null && temperature < 10;
    }
}
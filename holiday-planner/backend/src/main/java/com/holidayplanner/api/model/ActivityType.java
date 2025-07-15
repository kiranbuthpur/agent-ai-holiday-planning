package com.holidayplanner.api.model;

public enum ActivityType {
    OUTDOOR("Outdoor activity"),
    INDOOR("Indoor activity"),
    CULTURAL("Cultural activity"),
    ADVENTURE("Adventure activity"),
    RELAXATION("Relaxation activity"),
    DINING("Dining activity"),
    SHOPPING("Shopping activity"),
    ENTERTAINMENT("Entertainment activity"),
    SPORTS("Sports activity"),
    SIGHTSEEING("Sightseeing activity");
    
    private final String description;
    
    ActivityType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isWeatherDependent() {
        return this == OUTDOOR || this == ADVENTURE || this == SPORTS || this == SIGHTSEEING;
    }
}
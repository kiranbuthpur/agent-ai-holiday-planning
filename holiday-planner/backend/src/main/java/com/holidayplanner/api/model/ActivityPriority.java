package com.holidayplanner.api.model;

public enum ActivityPriority {
    LOW(1, "Low priority - optional activity"),
    MEDIUM(2, "Medium priority - recommended activity"),
    HIGH(3, "High priority - must-do activity"),
    CRITICAL(4, "Critical priority - essential for the trip");
    
    private final int level;
    private final String description;
    
    ActivityPriority(int level, String description) {
        this.level = level;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isHigherThan(ActivityPriority other) {
        return this.level > other.level;
    }
}
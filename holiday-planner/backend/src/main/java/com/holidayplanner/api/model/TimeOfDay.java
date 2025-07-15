package com.holidayplanner.api.model;

import java.time.LocalTime;

public enum TimeOfDay {
    MORNING("Morning", LocalTime.of(6, 0), LocalTime.of(12, 0)),
    AFTERNOON("Afternoon", LocalTime.of(12, 0), LocalTime.of(18, 0)),
    EVENING("Evening", LocalTime.of(18, 0), LocalTime.of(23, 0)),
    NIGHT("Night", LocalTime.of(23, 0), LocalTime.of(6, 0));
    
    private final String description;
    private final LocalTime startTime;
    private final LocalTime endTime;
    
    TimeOfDay(String description, LocalTime startTime, LocalTime endTime) {
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public boolean isInTimeRange(LocalTime time) {
        if (this == NIGHT) {
            // Night spans midnight
            return time.isAfter(startTime) || time.isBefore(endTime);
        }
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }
}
package com.holidayplanner.api.dto;

import com.holidayplanner.api.model.ActivityType;
import com.holidayplanner.api.model.TimeOfDay;
import com.holidayplanner.api.model.ActivityPriority;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class ActivityResponse {
    
    private Long id;
    private String name;
    private String description;
    private ActivityType type;
    private TimeOfDay timeOfDay;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    
    private String location;
    private String weatherRequirements;
    private String equipmentNeeded;
    private Double estimatedCost;
    private ActivityPriority priority;
    
    // Constructors
    public ActivityResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }
    
    public TimeOfDay getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(TimeOfDay timeOfDay) { this.timeOfDay = timeOfDay; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getWeatherRequirements() { return weatherRequirements; }
    public void setWeatherRequirements(String weatherRequirements) { this.weatherRequirements = weatherRequirements; }
    
    public String getEquipmentNeeded() { return equipmentNeeded; }
    public void setEquipmentNeeded(String equipmentNeeded) { this.equipmentNeeded = equipmentNeeded; }
    
    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }
    
    public ActivityPriority getPriority() { return priority; }
    public void setPriority(ActivityPriority priority) { this.priority = priority; }
}
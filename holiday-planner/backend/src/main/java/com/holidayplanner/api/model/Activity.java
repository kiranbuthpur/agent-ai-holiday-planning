package com.holidayplanner.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalTime;

@Entity
@Table(name = "activities")
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Activity name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @NotNull(message = "Activity type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;
    
    @NotNull(message = "Time of day is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeOfDay timeOfDay;
    
    @Column
    private LocalTime startTime;
    
    @Column
    private LocalTime endTime;
    
    @Column
    private String location;
    
    @Column(length = 500)
    private String weatherRequirements;
    
    @Column(length = 500)
    private String equipmentNeeded;
    
    @Column
    private Double estimatedCost;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityPriority priority = ActivityPriority.MEDIUM;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_plan_id", nullable = false)
    @JsonBackReference
    private DailyPlan dailyPlan;
    
    // Constructors
    public Activity() {}
    
    public Activity(String name, ActivityType type, TimeOfDay timeOfDay, DailyPlan dailyPlan) {
        this.name = name;
        this.type = type;
        this.timeOfDay = timeOfDay;
        this.dailyPlan = dailyPlan;
    }
    
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
    
    public DailyPlan getDailyPlan() { return dailyPlan; }
    public void setDailyPlan(DailyPlan dailyPlan) { this.dailyPlan = dailyPlan; }
}
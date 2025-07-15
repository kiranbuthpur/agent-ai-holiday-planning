package com.holidayplanner.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_plans")
public class DailyPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(length = 1000)
    private String weatherSummary;
    
    @Column
    private Double temperature;
    
    @Column
    private String weatherCondition;
    
    @Column
    private Integer humidity;
    
    @Column
    private Double windSpeed;
    
    @Column(length = 2000)
    private String generalRecommendations;
    
    @Column(length = 1000)
    private String precautions;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_plan_id", nullable = false)
    @JsonBackReference
    private HolidayPlan holidayPlan;
    
    @OneToMany(mappedBy = "dailyPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Activity> activities = new ArrayList<>();
    
    // Constructors
    public DailyPlan() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public DailyPlan(LocalDate date, HolidayPlan holidayPlan) {
        this();
        this.date = date;
        this.holidayPlan = holidayPlan;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getWeatherSummary() { return weatherSummary; }
    public void setWeatherSummary(String weatherSummary) { this.weatherSummary = weatherSummary; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }
    
    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }
    
    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }
    
    public String getGeneralRecommendations() { return generalRecommendations; }
    public void setGeneralRecommendations(String generalRecommendations) { this.generalRecommendations = generalRecommendations; }
    
    public String getPrecautions() { return precautions; }
    public void setPrecautions(String precautions) { this.precautions = precautions; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public HolidayPlan getHolidayPlan() { return holidayPlan; }
    public void setHolidayPlan(HolidayPlan holidayPlan) { this.holidayPlan = holidayPlan; }
    
    public List<Activity> getActivities() { return activities; }
    public void setActivities(List<Activity> activities) { this.activities = activities; }
    
    // Utility methods
    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void addActivity(Activity activity) {
        activities.add(activity);
        activity.setDailyPlan(this);
    }
    
    public void removeActivity(Activity activity) {
        activities.remove(activity);
        activity.setDailyPlan(null);
    }
}
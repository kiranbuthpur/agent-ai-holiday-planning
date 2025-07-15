package com.holidayplanner.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Email;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class HolidayPlanRequest {
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @NotNull(message = "Number of days is required")
    @Positive(message = "Number of days must be positive")
    private Integer numberOfDays;
    
    @NotBlank(message = "Activity category is required")
    private String activityCategory;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String userEmail;
    
    // Constructors
    public HolidayPlanRequest() {}
    
    public HolidayPlanRequest(String destination, LocalDate startDate, Integer numberOfDays, 
                             String activityCategory, String userEmail) {
        this.destination = destination;
        this.startDate = startDate;
        this.numberOfDays = numberOfDays;
        this.activityCategory = activityCategory;
        this.userEmail = userEmail;
    }
    
    // Getters and Setters
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public Integer getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(Integer numberOfDays) { this.numberOfDays = numberOfDays; }
    
    public String getActivityCategory() { return activityCategory; }
    public void setActivityCategory(String activityCategory) { this.activityCategory = activityCategory; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
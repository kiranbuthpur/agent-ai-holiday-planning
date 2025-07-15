package com.holidayplanner.api.model;

public enum PlanStatus {
    PENDING("Plan is being created"),
    ACTIVE("Plan is active and monitoring weather"),
    COMPLETED("Holiday completed"),
    CANCELLED("Plan cancelled");
    
    private final String description;
    
    PlanStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
package com.holidayplanner.api.controller;

import com.holidayplanner.api.dto.*;
import com.holidayplanner.api.service.HolidayPlanService;
import com.holidayplanner.api.service.WeatherService;
import com.holidayplanner.api.model.WeatherData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/holiday-plans")
@CrossOrigin(origins = "*") // Configure for your frontend URL in production
public class HolidayPlanController {
    
    private final HolidayPlanService holidayPlanService;
    private final WeatherService weatherService;
    
    public HolidayPlanController(HolidayPlanService holidayPlanService, WeatherService weatherService) {
        this.holidayPlanService = holidayPlanService;
        this.weatherService = weatherService;
    }
    
    /**
     * Create a new holiday plan
     */
    @PostMapping
    public Mono<ResponseEntity<HolidayPlanResponse>> createHolidayPlan(@Valid @RequestBody HolidayPlanRequest request) {
        return holidayPlanService.createHolidayPlan(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorReturn(
                    IllegalArgumentException.class,
                    ResponseEntity.badRequest().build()
                );
    }
    
    /**
     * Get holiday plan by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HolidayPlanResponse> getHolidayPlan(
            @PathVariable Long id,
            @RequestParam String userEmail) {
        
        Optional<HolidayPlanResponse> plan = holidayPlanService.getHolidayPlan(id, userEmail);
        
        return plan.map(response -> ResponseEntity.ok(response))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all holiday plans for a user
     */
    @GetMapping
    public ResponseEntity<List<HolidayPlanResponse>> getUserHolidayPlans(@RequestParam String userEmail) {
        List<HolidayPlanResponse> plans = holidayPlanService.getUserHolidayPlans(userEmail);
        return ResponseEntity.ok(plans);
    }
    
    /**
     * Update holiday plan with latest weather data
     */
    @PutMapping("/{id}/weather-update")
    public Mono<ResponseEntity<HolidayPlanResponse>> updateHolidayPlanWeather(
            @PathVariable Long id,
            @RequestParam String userEmail) {
        
        return holidayPlanService.updateHolidayPlanWithWeather(id, userEmail)
                .map(response -> ResponseEntity.ok(response))
                .onErrorReturn(
                    IllegalArgumentException.class,
                    ResponseEntity.notFound().build()
                );
    }
    
    /**
     * Cancel holiday plan
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelHolidayPlan(
            @PathVariable Long id,
            @RequestParam String userEmail) {
        
        boolean cancelled = holidayPlanService.cancelHolidayPlan(id, userEmail);
        
        return cancelled ? ResponseEntity.noContent().build() 
                        : ResponseEntity.notFound().build();
    }
    
    /**
     * Get daily plan for specific date
     */
    @GetMapping("/{id}/daily-plan")
    public ResponseEntity<DailyPlanResponse> getDailyPlan(
            @PathVariable Long id,
            @RequestParam LocalDate date,
            @RequestParam String userEmail) {
        
        Optional<DailyPlanResponse> dailyPlan = holidayPlanService.getDailyPlan(id, date, userEmail);
        
        return dailyPlan.map(response -> ResponseEntity.ok(response))
                       .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get available activity categories
     */
    @GetMapping("/activity-categories")
    public ResponseEntity<List<String>> getActivityCategories() {
        List<String> categories = holidayPlanService.getAvailableActivityCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get current weather for a destination
     */
    @GetMapping("/weather/current")
    public Mono<ResponseEntity<WeatherData>> getCurrentWeather(@RequestParam String destination) {
        return weatherService.getCurrentWeather(destination)
                .map(weather -> ResponseEntity.ok(weather))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
    
    /**
     * Get weather forecast for multiple days
     */
    @GetMapping("/weather/forecast")
    public Mono<ResponseEntity<List<WeatherData>>> getWeatherForecast(
            @RequestParam String destination,
            @RequestParam(defaultValue = "7") int days) {
        
        return weatherService.getWeatherForecast(destination, days)
                .map(forecast -> ResponseEntity.ok(forecast))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
}

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
class AdminController {
    
    private final HolidayPlanService holidayPlanService;
    
    public AdminController(HolidayPlanService holidayPlanService) {
        this.holidayPlanService = holidayPlanService;
    }
    
    /**
     * Trigger daily weather updates for all active plans (for testing/manual trigger)
     */
    @PostMapping("/weather-updates")
    public ResponseEntity<String> triggerDailyWeatherUpdates() {
        try {
            holidayPlanService.processDailyWeatherUpdates();
            return ResponseEntity.ok("Daily weather updates triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to trigger updates: " + e.getMessage());
        }
    }
}

// Global exception handler for better error responses
@ControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorResponse error = new ErrorResponse("INVALID_REQUEST", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// Error response DTO
class ErrorResponse {
    private String code;
    private String message;
    
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
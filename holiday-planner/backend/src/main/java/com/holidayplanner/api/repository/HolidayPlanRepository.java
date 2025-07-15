package com.holidayplanner.api.repository;

import com.holidayplanner.api.model.HolidayPlan;
import com.holidayplanner.api.model.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayPlanRepository extends JpaRepository<HolidayPlan, Long> {
    
    List<HolidayPlan> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    
    List<HolidayPlan> findByStatus(PlanStatus status);
    
    List<HolidayPlan> findByDestinationContainingIgnoreCase(String destination);
    
    @Query("SELECT hp FROM HolidayPlan hp WHERE hp.startDate <= :currentDate AND hp.startDate + hp.numberOfDays > :currentDate AND hp.status = :status")
    List<HolidayPlan> findActivePlansForDate(@Param("currentDate") LocalDate currentDate, @Param("status") PlanStatus status);
    
    @Query("SELECT hp FROM HolidayPlan hp WHERE hp.startDate >= :startDate AND hp.startDate <= :endDate")
    List<HolidayPlan> findPlansInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    Optional<HolidayPlan> findByIdAndUserEmail(Long id, String userEmail);
    
    boolean existsByUserEmailAndDestinationAndStartDate(String userEmail, String destination, LocalDate startDate);
}
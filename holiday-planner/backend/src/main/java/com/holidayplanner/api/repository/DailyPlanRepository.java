package com.holidayplanner.api.repository;

import com.holidayplanner.api.model.DailyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {
    
    List<DailyPlan> findByHolidayPlanIdOrderByDateAsc(Long holidayPlanId);
    
    Optional<DailyPlan> findByHolidayPlanIdAndDate(Long holidayPlanId, LocalDate date);
    
    List<DailyPlan> findByDateAndHolidayPlanStatusOrderByLastUpdatedAsc(LocalDate date, 
                                                                        com.holidayplanner.api.model.PlanStatus status);
    
    @Query("SELECT dp FROM DailyPlan dp WHERE dp.date = :date AND dp.holidayPlan.status = :status")
    List<DailyPlan> findByDateAndStatus(@Param("date") LocalDate date, 
                                       @Param("status") com.holidayplanner.api.model.PlanStatus status);
    
    @Query("SELECT dp FROM DailyPlan dp WHERE dp.date >= :startDate AND dp.date <= :endDate AND dp.holidayPlan.id = :holidayPlanId ORDER BY dp.date ASC")
    List<DailyPlan> findByDateRangeAndHolidayPlan(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate, 
                                                  @Param("holidayPlanId") Long holidayPlanId);
    
    void deleteByHolidayPlanId(Long holidayPlanId);
}
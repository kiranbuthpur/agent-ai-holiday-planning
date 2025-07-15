package com.holidayplanner.api.repository;

import com.holidayplanner.api.model.Activity;
import com.holidayplanner.api.model.ActivityType;
import com.holidayplanner.api.model.TimeOfDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    List<Activity> findByDailyPlanIdOrderByTimeOfDayAscStartTimeAsc(Long dailyPlanId);
    
    List<Activity> findByDailyPlanIdAndTimeOfDay(Long dailyPlanId, TimeOfDay timeOfDay);
    
    List<Activity> findByDailyPlanIdAndType(Long dailyPlanId, ActivityType type);
    
    @Query("SELECT a FROM Activity a WHERE a.dailyPlan.id = :dailyPlanId AND a.type IN :types ORDER BY a.priority DESC, a.timeOfDay ASC")
    List<Activity> findByDailyPlanIdAndTypesOrderByPriority(@Param("dailyPlanId") Long dailyPlanId, 
                                                           @Param("types") List<ActivityType> types);
    
    @Query("SELECT a FROM Activity a WHERE a.dailyPlan.holidayPlan.id = :holidayPlanId ORDER BY a.dailyPlan.date ASC, a.timeOfDay ASC")
    List<Activity> findByHolidayPlanIdOrderByDateAndTime(@Param("holidayPlanId") Long holidayPlanId);
    
    void deleteByDailyPlanId(Long dailyPlanId);
}
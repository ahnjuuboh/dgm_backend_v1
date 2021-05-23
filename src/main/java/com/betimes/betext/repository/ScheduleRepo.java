package com.betimes.betext.repository;

import com.betimes.betext.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, String> {

    @Override
    @Query(value = "SELECT sc FROM Schedule sc WHERE record_status = 'A' and schedule_id = :id")
    Optional<Schedule> findById(@Param("id") String scheduleId);
}

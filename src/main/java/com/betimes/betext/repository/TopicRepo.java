package com.betimes.betext.repository;

import com.betimes.betext.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepo extends JpaRepository<Topic, Long> {
    @Override
    @Query(value = "SELECT t FROM Topic t WHERE record_status = 'A'")
    List<Topic> findAll();

    @Query(value = "SELECT t FROM Topic t WHERE record_status = 'A' and created_by = :username")
    List<Topic> findByUsername(@Param("username") String username);

    @Override
    @Query(value = "SELECT t FROM Topic t WHERE record_status = 'A' and topic_id = :id")
    Optional<Topic> findById(@Param("id") Long id);
}

package com.betimes.betext.repository;

import com.betimes.betext.model.TopicCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TopicConditionRepo extends JpaRepository<TopicCondition, Long> {

    @Query(value = "SELECT tc FROM TopicCondition tc WHERE topic_id = :topicId")
    List<TopicCondition> findByTopicId(@Param("topicId") Long topicId);

    @Modifying
    @Query(value = "DELETE FROM TopicCondition tc WHERE topic_id = :topicId")
    int deleteByTopicId(@Param("topicId") Long topicId);
}

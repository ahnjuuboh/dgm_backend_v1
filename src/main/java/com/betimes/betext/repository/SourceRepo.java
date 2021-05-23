package com.betimes.betext.repository;

import com.betimes.betext.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourceRepo extends JpaRepository<Source, String> {
    @Override
    @Query(value = "SELECT s FROM Source s WHERE record_status = 'A'")
    List<Source> findAll();

    @Query(value = "SELECT s FROM Source s WHERE record_status = 'A' and created_by = :username")
    List<Source> findByUsername(@Param("username") String username);

    @Override
    @Query(value = "SELECT s FROM Source s WHERE record_status = 'A' and source_id = :id")
    Optional<Source> findById(@Param("id") String id);
}

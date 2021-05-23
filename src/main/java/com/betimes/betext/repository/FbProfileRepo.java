package com.betimes.betext.repository;

import com.betimes.betext.model.FbProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FbProfileRepo extends JpaRepository<FbProfile, String> {
}

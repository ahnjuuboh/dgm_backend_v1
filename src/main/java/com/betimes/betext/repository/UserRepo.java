package com.betimes.betext.repository;

import com.betimes.betext.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    @Query(value = "SELECT u FROM User u WHERE username = :username AND password = :password")
    Optional<User> login(@Param("username") String username, @Param("password") String password);
}

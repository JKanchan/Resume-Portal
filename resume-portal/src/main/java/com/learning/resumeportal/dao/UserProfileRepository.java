package com.learning.resumeportal.dao;



import org.springframework.data.jpa.repository.JpaRepository;

import com.learning.resumeportal.model.UserProfile;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    Optional<UserProfile> findByUserName(String userName);
}
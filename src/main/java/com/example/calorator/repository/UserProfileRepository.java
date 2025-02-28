package com.example.calorator.repository;

import com.example.calorator.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> { // Use JPARepository for CRUD operations{
}

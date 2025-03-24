package com.example.calorator.repository;

import com.example.calorator.model.entity.Goal;
import com.example.calorator.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    Optional<Goal> findByUser(User user);

    void deleteByUser(User user);
}
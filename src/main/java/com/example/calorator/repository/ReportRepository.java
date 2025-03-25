package com.example.calorator.repository;

import com.example.calorator.model.entity.Report;
import com.example.calorator.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUser(User user);
}

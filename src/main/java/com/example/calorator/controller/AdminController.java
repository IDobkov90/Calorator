package com.example.calorator.controller;

import com.example.calorator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping
    public String adminDashboard(Model model) {
        // Add some basic stats for the admin dashboard
        model.addAttribute("totalUsers", userRepository.count());
        return "admin/dashboard";
    }
}

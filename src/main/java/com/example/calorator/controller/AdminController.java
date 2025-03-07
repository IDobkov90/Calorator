package com.example.calorator.controller;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.FoodItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final FoodItemService foodItemService;


    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {

        model.addAttribute("foodCategories", FoodCategory.values());

        model.addAttribute("totalFoodItems", foodItemService.getAllFoodItems().size());
        

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<FoodItemDTO> recentFoodItems = foodItemService.getAllFoodItems(pageable);
        model.addAttribute("recentFoodItems", recentFoodItems.getContent());
        
        return "admin/dashboard";
    }
}

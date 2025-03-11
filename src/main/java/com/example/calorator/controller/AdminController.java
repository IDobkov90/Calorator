package com.example.calorator.controller;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserProfileDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.FoodItemService;
import com.example.calorator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final FoodItemService foodItemService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {

        model.addAttribute("foodCategories", FoodCategory.values());
        model.addAttribute("totalFoodItems", foodItemService.getAllFoodItems().size());

        model.addAttribute("totalUsers", userRepository.count());

        Pageable userPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> recentUsersPage = userRepository.findAll(userPageable);
        model.addAttribute("recentUsers", recentUsersPage.getContent());

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<FoodItemDTO> recentFoodItems = foodItemService.getAllFoodItems(pageable);
        model.addAttribute("recentFoodItems", recentFoodItems.getContent());
        
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<UserDTO> userPage = userService.getAllUsers(pageable);
        
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        
        return "admin/users";
    }
    
    @GetMapping("/users/{username}")
    public String viewUserProfile(@PathVariable String username, Model model) {
        UserProfileDTO userProfile = userService.getUserProfile(username);
        if (userProfile == null) {
            return "redirect:/admin/users?error=User+not+found";
        }
        model.addAttribute("userProfile", userProfile);
        return "admin/user-profile";
    }
}

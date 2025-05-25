package com.example.calorator.controller;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserProfileDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.UserRole;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String adminDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        model.addAttribute("foodCategories", FoodCategory.values());

        model.addAttribute("totalFoodItems", foodItemService.countFoodItems());

        model.addAttribute("totalUsers", userRepository.count());

        Pageable userPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> recentUsersPage = userRepository.findAll(userPageable);
        model.addAttribute("recentUsers", recentUsersPage.getContent());
        
        Pageable foodPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<FoodItemDTO> recentFoodItemsPage = foodItemService.getAllFoodItems(foodPageable);
        
        model.addAttribute("size", size);
        model.addAttribute("recentFoodItems", recentFoodItemsPage.getContent());
        model.addAttribute("currentPage", recentFoodItemsPage.getNumber());
        model.addAttribute("totalPages", recentFoodItemsPage.getTotalPages());
        model.addAttribute("totalItems", recentFoodItemsPage.getTotalElements());

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
        model.addAttribute("pageSize", size);

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

    @GetMapping("/users/{username}/role")
    public String editUserRoleForm(@PathVariable String username, Model model) {
        UserDTO user = userService.getUserByUsername(username);
        UserRole[] allRoles = userService.getAllAvailableRoles();

        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("currentRole", user.getRole());

        return "admin/edit-user-role";
    }

    @PostMapping("/users/{username}/role")
    public String updateUserRole(
            @PathVariable String username,
            @RequestParam UserRole role,
            RedirectAttributes redirectAttributes) {

        try {
            userService.updateUserRole(username, role);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User role for " + username + " updated successfully to " + role);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error updating user role: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }
}
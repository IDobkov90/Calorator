package com.example.calorator.controller;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.dto.PasswordChangeDTO;
import com.example.calorator.model.dto.UserProfileDTO;
import com.example.calorator.model.dto.UserRegisterDTO;
import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.Gender;
import com.example.calorator.service.FoodItemService;
import com.example.calorator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FoodItemService foodItemService;
    private static final String USER_REGISTER_DTO = "userRegisterDTO";
    private static final String PASSWORD_CHANGE_DTO = "passwordChangeDTO";

    @ModelAttribute(USER_REGISTER_DTO)
    public UserRegisterDTO userRegisterDTO() {
        return new UserRegisterDTO();
    }
    @ModelAttribute(PASSWORD_CHANGE_DTO)
    public PasswordChangeDTO passwordChangeDTO() {
        return new PasswordChangeDTO();
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute(USER_REGISTER_DTO)) {
            model.addAttribute(USER_REGISTER_DTO, new UserRegisterDTO());
        }
        model.addAttribute("genders", Gender.values());
        model.addAttribute("activityLevels", ActivityLevel.values());
        return "user/register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid UserRegisterDTO userRegisterDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userRegisterDTO", "Passwords do not match");
        }
        if (userService.existsByUsernameOrEmail(userRegisterDTO.getUsername(), userRegisterDTO.getEmail())) {
            bindingResult.rejectValue("username", "error.userRegisterDTO",
                    "Username or email already exists");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + USER_REGISTER_DTO, bindingResult);
            redirectAttributes.addFlashAttribute(USER_REGISTER_DTO, userRegisterDTO);
            return "redirect:/register";
        }
        
        try {
            userService.register(userRegisterDTO);
            return "redirect:/login?registered";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute(USER_REGISTER_DTO, userRegisterDTO);
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "user/login";
    }

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String dashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        model.addAttribute("foodCategories", FoodCategory.values());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FoodItemDTO> recentFoodItemsPage = foodItemService.getRecentFoodItems(pageable);
        model.addAttribute("recentFoodItems", recentFoodItemsPage.getContent());
        model.addAttribute("currentPage", recentFoodItemsPage.getNumber());
        model.addAttribute("totalPages", recentFoodItemsPage.getTotalPages());
        model.addAttribute("totalItems", recentFoodItemsPage.getTotalElements());
        
        return "user/dashboard";
    }

    @GetMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public String showSettingsPage() {
        return "user/settings";
    }
    
    @GetMapping("/settings/change-password")
    @PreAuthorize("isAuthenticated()")
    public String showChangePasswordForm(Model model) {
        model.addAttribute(PASSWORD_CHANGE_DTO, passwordChangeDTO());
        return "user/change-password";
    }
    
    @PostMapping("/settings/change-password")
    @PreAuthorize("isAuthenticated()")
    public String changePassword(
            @Valid PasswordChangeDTO passwordChangeDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Principal principal) {
        
        if (bindingResult.hasErrors()) {
            return "user/change-password";
        }
        
        try {
            userService.changePassword(
                    principal.getName(),
                    passwordChangeDTO.getCurrentPassword(),
                    passwordChangeDTO.getNewPassword()
            );
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password: " + e.getMessage());
            return "redirect:/settings/change-password";
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String showProfilePage(Model model, Principal principal) {
        UserProfileDTO userProfile = userService.getUserProfile(principal.getName());
        model.addAttribute("userProfile", userProfile);
        return "user/profile";
    }
}

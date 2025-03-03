package com.example.calorator.controller;

import com.example.calorator.model.dto.UserRegisterDTO;
import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.Gender;
import com.example.calorator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final String USER_REGISTER_DTO = "userRegisterDTO";

    @ModelAttribute(USER_REGISTER_DTO)
    public UserRegisterDTO userRegisterDTO() {
        return new UserRegisterDTO();
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
    public String dashboard() {
        return "user/dashboard";
    }
}

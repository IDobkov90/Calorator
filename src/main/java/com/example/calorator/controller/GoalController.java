package com.example.calorator.controller;

import com.example.calorator.model.dto.GoalDTO;
import com.example.calorator.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public String viewGoal(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        GoalDTO goal = goalService.getGoalByUsername(userDetails.getUsername());
        model.addAttribute("goal", goal);
        return "goals/view";
    }

    @GetMapping("/edit")
    public String editGoalForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        GoalDTO goal = goalService.getGoalByUsername(userDetails.getUsername());
        model.addAttribute("goal", goal != null ? goal : new GoalDTO());
        return "goals/edit";
    }

    @PostMapping("/edit")
    public String updateGoal(@AuthenticationPrincipal UserDetails userDetails,
                             @Valid @ModelAttribute("goal") GoalDTO goalDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "goals/edit";
        }
        goalService.updateGoal(userDetails.getUsername(), goalDTO);
        redirectAttributes.addFlashAttribute("message", "Goal updated successfully");
        return "redirect:/goals";
    }

    @PostMapping("/delete")
    public String deleteGoal(@AuthenticationPrincipal UserDetails userDetails) {
        goalService.deleteGoal(userDetails.getUsername());
        return "redirect:/goals";
    }
}

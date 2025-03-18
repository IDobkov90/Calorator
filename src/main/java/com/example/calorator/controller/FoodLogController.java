package com.example.calorator.controller;

import com.example.calorator.model.dto.FoodLogDTO;
import com.example.calorator.model.dto.NutritionSummaryDTO;
import com.example.calorator.model.enums.MealType;
import com.example.calorator.service.FoodItemService;
import com.example.calorator.service.FoodLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/food-logs")
@RequiredArgsConstructor
public class FoodLogController {

    private final FoodLogService foodLogService;
    private final FoodItemService foodItemService;

    @ModelAttribute("mealTypes")
    public MealType[] getMealTypes() {
        return MealType.values();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String getAllFoodLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            Model model,
            Authentication authentication) {

        if (date == null) {
            date = LocalDate.now();
        }

        String username = authentication.getName();
        List<FoodLogDTO> foodLogs = foodLogService.getFoodLogsByDate(date, username);
        NutritionSummaryDTO nutritionSummary = foodLogService.getNutritionSummaryForDate(date, username);

        model.addAttribute("foodLogs", foodLogs);
        model.addAttribute("nutritionSummary", nutritionSummary);
        model.addAttribute("selectedDate", date);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "date", "id"));
        Page<FoodLogDTO> recentLogs = foodLogService.getRecentFoodLogs(username, pageable);
        model.addAttribute("recentLogs", recentLogs.getContent());

        return "food-logs/list";
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String showCreateForm(Model model) {
        model.addAttribute("foodLog", new FoodLogDTO());
        model.addAttribute("foodItems", foodItemService.getAllFoodItems());
        model.addAttribute("today", LocalDate.now());
        return "food-logs/create";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createFoodLog(@Valid @ModelAttribute("foodLog") FoodLogDTO foodLogDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("foodItems", foodItemService.getAllFoodItems());
            model.addAttribute("today", LocalDate.now());
            return "food-logs/create";
        }
        String username = authentication.getName();
        FoodLogDTO createdFoodLog = foodLogService.createFoodLog(foodLogDTO, username);
        redirectAttributes.addFlashAttribute("successMessage", "Food log created successfully!");
        return "redirect:/food-logs?date=" + foodLogDTO.getDate();
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(@PathVariable Long id, Authentication authentication, Model model) {
        String username = authentication.getName();
        FoodLogDTO foodLog = foodLogService.getFoodLogById(id, username);
        model.addAttribute("foodLog", foodLog);
        model.addAttribute("foodItems", foodItemService.getAllFoodItems());
        return "food-logs/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateFoodLog(@PathVariable Long id,
                                @Valid @ModelAttribute("foodLog") FoodLogDTO foodLogDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("foodItems", foodItemService.getAllFoodItems());
            return "food-logs/edit";
        }
        String username = authentication.getName();
        FoodLogDTO updatedFoodLog = foodLogService.updateFoodLog(id, foodLogDTO, username);
        redirectAttributes.addFlashAttribute("successMessage", "Food log updated successfully!");
        return "redirect:/food-logs?date=" + foodLogDTO.getDate();
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteFoodLog(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        FoodLogDTO foodLog = foodLogService.getFoodLogById(id, username);
        LocalDate date = foodLog.getDate();
        foodLogService.deleteFoodLog(id, username);
        redirectAttributes.addFlashAttribute("successMessage", "Food log deleted successfully!");
        return "redirect:/food-logs?date=" + date;
    }

    @GetMapping("/meal/{mealType}")
    @PreAuthorize("isAuthenticated()")
    public String getFoodLogsByMealType(
            @PathVariable MealType mealType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model,
            Authentication authentication) {
        
        if (date == null) {
            date = LocalDate.now();
        }

        String username = authentication.getName();
        List<FoodLogDTO> foodLogs = foodLogService.getFoodLogsByDateAndMealType(date, mealType, username);

        double totalCalories = foodLogs.stream().mapToDouble(FoodLogDTO::getTotalCalories).sum();
        double totalProtein = foodLogs.stream().mapToDouble(FoodLogDTO::getTotalProtein).sum();
        double totalCarbs = foodLogs.stream().mapToDouble(FoodLogDTO::getTotalCarbs).sum();
        double totalFat = foodLogs.stream().mapToDouble(FoodLogDTO::getTotalFat).sum();
        
        NutritionSummaryDTO nutritionSummary = new NutritionSummaryDTO();
        nutritionSummary.setTotalCalories(totalCalories);
        nutritionSummary.setTotalProtein(totalProtein);
        nutritionSummary.setTotalCarbs(totalCarbs);
        nutritionSummary.setTotalFat(totalFat);
        
        model.addAttribute("foodLogs", foodLogs);
        model.addAttribute("nutritionSummary", nutritionSummary);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedMealType", mealType);
        
        return "food-logs/meal-view";
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public String getFoodLogHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            Authentication authentication) {

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        Page<FoodLogDTO> foodLogs = foodLogService.getFoodLogsByDateRange(startDate, endDate, username, pageable);
        Map<LocalDate, NutritionSummaryDTO> nutritionSummaries =
                foodLogService.getNutritionSummaryForDateRange(startDate, endDate, username);
        model.addAttribute("foodLogs", foodLogs);
        model.addAttribute("nutritionSummaries", nutritionSummaries);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", foodLogs.getTotalPages());
        return "food-logs/history";
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public String searchFoodLogs(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            Authentication authentication) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<FoodLogDTO> searchResults = foodLogService.searchFoodLogs(query, username, pageable);
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());
        return "food-logs/search-results";
    }

    @GetMapping("/frequent")
    @PreAuthorize("isAuthenticated()")
    public String getFrequentFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            Authentication authentication) {
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<FoodLogDTO> frequentFoods = foodLogService.getFrequentlyLoggedFoods(username, pageable);
        model.addAttribute("frequentFoods", frequentFoods);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", frequentFoods.getTotalPages());
        return "food-logs/frequent";
    }
}

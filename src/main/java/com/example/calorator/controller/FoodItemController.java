package com.example.calorator.controller;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import com.example.calorator.service.FoodItemService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/food-items")
@RequiredArgsConstructor
public class FoodItemController {

    private final FoodItemService foodItemService;

    @ModelAttribute("categories")
    public List<Map<String, String>> getCategories() {
        return Arrays.stream(FoodCategory.values())
                .map(category -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", category.name());
                    map.put("displayName", category.getDisplayName());
                    return map;
                })
                .toList();
    }

    @ModelAttribute("servingUnits")
    public ServingUnit[] getServingUnits() {
        return ServingUnit.values();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String getAllFoodItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String category,
            Model model) {

        FoodCategory foodCategory = null;
        if (category != null && !category.isEmpty()) {
            try {
                foodCategory = FoodCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                model.addAttribute("errorMessage", "Invalid category: " + category);
            }
        }
    
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                                      Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, sortDirection, sort);
    
        Page<FoodItemDTO> foodItems;
        if (foodCategory != null) {
            foodItems = foodItemService.getFoodItemsByCategory(foodCategory, pageable);
        } else {
            foodItems = foodItemService.getAllFoodItems(pageable);
        }
        
        model.addAttribute("foodItems", foodItems);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", foodItems.getTotalPages());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        
        return "food-items/list";
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("isAuthenticated()")
    public String getFoodItemsByCategory(@PathVariable String category, Model model) {
        try {
            FoodCategory foodCategory = FoodCategory.valueOf(category.toUpperCase());
            List<FoodItemDTO> foodItems = foodItemService.getFoodItemsByCategory(foodCategory);
            model.addAttribute("foodItems", foodItems);
            model.addAttribute("selectedCategory", foodCategory);
            return "food-items/list";
        } catch (IllegalArgumentException e) {
            return "redirect:/food-items";
        }
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("foodItem")) {
            model.addAttribute("foodItem", new FoodItemDTO());
        }
        model.addAttribute("categories", FoodCategory.values());
        model.addAttribute("servingUnits", ServingUnit.values());
        return "food-items/create";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createFoodItem(@Valid @ModelAttribute("foodItem") FoodItemDTO foodItemDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", FoodCategory.values());
            model.addAttribute("servingUnits", ServingUnit.values());
            return "food-items/create";
        }

        try {
            foodItemService.createFoodItem(foodItemDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Food item created successfully");
            return "redirect:/food-items";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating food item: " + e.getMessage());
            redirectAttributes.addFlashAttribute("foodItem", foodItemDTO);
            return "redirect:/food-items/create";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            FoodItemDTO foodItem = foodItemService.getFoodItemById(id);
            model.addAttribute("foodItem", foodItem);
            model.addAttribute("categories", FoodCategory.values());
            model.addAttribute("servingUnits", ServingUnit.values());
            return "food-items/edit";
        } catch (Exception e) {
            return "redirect:/food-items";
        }
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateFoodItem(@PathVariable Long id,
                                 @Valid @ModelAttribute("foodItem") FoodItemDTO foodItemDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", FoodCategory.values());
            model.addAttribute("servingUnits", ServingUnit.values());
            return "food-items/edit";
        }

        try {
            foodItemDTO.setId(id);
            foodItemService.updateFoodItem(foodItemDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Food item updated successfully");
            return "redirect:/food-items";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating food item: " + e.getMessage());
            return "redirect:/food-items/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFoodItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            foodItemService.deleteFoodItem(id);
            redirectAttributes.addFlashAttribute("successMessage", "Food item deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting food item: " + e.getMessage());
        }
        return "redirect:/food-items";
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String viewFoodItem(@PathVariable Long id, Model model) {
        try {
            FoodItemDTO foodItem = foodItemService.getFoodItemById(id);
            model.addAttribute("foodItem", foodItem);
            return "food-items/view";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Food item not found");
            return "redirect:/food-items";
        }
    }
}

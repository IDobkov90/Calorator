package com.example.calorator.service.scheduled;

import com.example.calorator.model.dto.NutritionSummaryDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.FoodLogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyStatisticsScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DailyStatisticsScheduler.class);
    
    private final UserRepository userRepository;
    private final FoodLogService foodLogService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void generateDailyStatistics() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        logger.info("Generating daily statistics for {}", yesterday);
        
        List<User> users = userRepository.findAll();
        double totalCaloriesAllUsers = 0;
        int userCount = 0;
        
        for (User user : users) {
            try {
                NutritionSummaryDTO summary = foodLogService.getNutritionSummaryForDate(yesterday, user.getUsername());
                if (summary != null) {
                    totalCaloriesAllUsers += summary.getTotalCalories();
                    userCount++;
                    logger.info("User {} consumed {} calories on {}", 
                            user.getUsername(), summary.getTotalCalories(), yesterday);
                }
            } catch (Exception e) {
                logger.warn("Could not calculate statistics for user {}: {}", 
                        user.getUsername(), e.getMessage());
            }
        }
        
        if (userCount > 0) {
            double averageCalories = totalCaloriesAllUsers / userCount;
            logger.info("Daily Statistics for {}: Total users: {}, Average calories per user: {}, Total calories: {}", 
                    yesterday, userCount, averageCalories, totalCaloriesAllUsers);
        } else {
            logger.info("No nutrition data found for {}", yesterday);
        }
    }
}
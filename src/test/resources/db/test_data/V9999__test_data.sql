-- Test Users
INSERT INTO USERS (id, username, email, password, role, created_at, updated_at)
VALUES
(1, 'testuser', 'test@example.com', '$2a$10$eDhncK/4cNH2KE.Y51AWpeL8K4mtNVxMvhDTSqtLCrWHvNQSu8/2m', 'USER', NOW(), NOW()),
(2, 'testadmin', 'admin@example.com', '$2a$10$eDhncK/4cNH2KE.Y51AWpeL8K4mtNVxMvhDTSqtLCrWHvNQSu8/2m', 'ADMIN', NOW(), NOW());

-- Test User Profiles
INSERT INTO USER_PROFILES (id, user_id, first_name, last_name, gender, age, height, weight, activity_level, created_at, updated_at)
VALUES
(1, 1, 'Test', 'User', 'MALE', 30, 180.0, 80.0, 'MODERATELY_ACTIVE', NOW(), NOW()),
(2, 2, 'Admin', 'User', 'FEMALE', 35, 165.0, 65.0, 'LIGHTLY_ACTIVE', NOW(), NOW());

-- Test Food Items
INSERT INTO FOOD_ITEMS (id, name, calories, protein, carbs, fat, serving_size, serving_unit, category, created_at, updated_at)
VALUES
(1, 'Test Apple', 52.0, 0.3, 14.0, 0.2, 100.0, 'GRAM', 'FRUIT', NOW(), NOW()),
(2, 'Test Chicken Breast', 165.0, 31.0, 0.0, 3.6, 100.0, 'GRAM', 'PROTEIN', NOW(), NOW()),
(3, 'Test Brown Rice', 112.0, 2.6, 23.5, 0.9, 100.0, 'GRAM', 'GRAIN', NOW(), NOW());

-- Test Goals
INSERT INTO GOALS (id, user_id, type, target_weight, daily_calorie_goal, created_at, updated_at)
VALUES
(1, 1, 'LOSE_WEIGHT', 75.0, 2000.0, NOW(), NOW());

-- Test Food Logs (for yesterday and today)
INSERT INTO FOOD_LOGS (id, user_id, food_item_id, quantity, meal_type, log_date, created_at, updated_at)
VALUES
(1, 1, 1, 1.0, 'BREAKFAST', CURRENT_DATE() - 1, NOW(), NOW()),
(2, 1, 2, 1.5, 'LUNCH', CURRENT_DATE() - 1, NOW(), NOW()),
(3, 1, 3, 1.0, 'DINNER', CURRENT_DATE() - 1, NOW(), NOW()),
(4, 1, 1, 2.0, 'BREAKFAST', CURRENT_DATE(), NOW(), NOW());

-- Test Reports
INSERT INTO REPORTS (id, user_id, report_type, start_date, end_date, created_at, updated_at)
VALUES
(1, 1, 'DAILY', CURRENT_DATE() - 7, CURRENT_DATE(), NOW(), NOW()),
(2, 1, 'WEEKLY', CURRENT_DATE() - 30, CURRENT_DATE(), NOW(), NOW());
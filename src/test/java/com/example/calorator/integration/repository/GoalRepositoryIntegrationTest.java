package com.example.calorator.integration.repository;

import com.example.calorator.model.entity.Goal;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.GoalType;
import com.example.calorator.model.enums.UserRole;
import com.example.calorator.repository.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class GoalRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GoalRepository goalRepository;

    private User testUser1;
    private User testUser2;
    private Goal testGoal1;
    private Goal testGoal2;

    @BeforeEach
    void setUp() {

        goalRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        testUser1 = new User();
        testUser1.setUsername("testuser1");
        testUser1.setEmail("test1@example.com");
        testUser1.setPassword("password123");
        testUser1.setRole(UserRole.USER);
        entityManager.persist(testUser1);

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setEmail("test2@example.com");
        testUser2.setPassword("password456");
        testUser2.setRole(UserRole.USER);
        entityManager.persist(testUser2);

        testGoal1 = new Goal();
        testGoal1.setUser(testUser1);
        testGoal1.setType(GoalType.LOSE_WEIGHT);
        testGoal1.setTargetWeight(70.0);
        testGoal1.setDailyCalorieGoal(2000.0);
        entityManager.persist(testGoal1);

        testGoal2 = new Goal();
        testGoal2.setUser(testUser2);
        testGoal2.setType(GoalType.GAIN_WEIGHT);
        testGoal2.setTargetWeight(80.0);
        testGoal2.setDailyCalorieGoal(3000.0);
        entityManager.persist(testGoal2);

        entityManager.flush();
    }

    @Test
    void findById_ShouldReturnGoal() {

        Optional<Goal> result = goalRepository.findById(testGoal1.getId());

        assertTrue(result.isPresent());
        assertEquals(testGoal1.getType(), result.get().getType());
        assertEquals(testGoal1.getTargetWeight(), result.get().getTargetWeight());
        assertEquals(testGoal1.getDailyCalorieGoal(), result.get().getDailyCalorieGoal());
        assertEquals(testUser1.getId(), result.get().getUser().getId());
    }

    @Test
    void findByUser_ShouldReturnUserGoal() {

        Optional<Goal> result = goalRepository.findByUser(testUser1);

        assertTrue(result.isPresent());
        assertEquals(testGoal1.getId(), result.get().getId());
        assertEquals(testGoal1.getType(), result.get().getType());
        assertEquals(testGoal1.getTargetWeight(), result.get().getTargetWeight());
    }

    @Test
    void findByUser_WithNonExistentUser_ShouldReturnEmpty() {

        User nonExistentUser = new User();
        nonExistentUser.setId(999L);
        nonExistentUser.setUsername("nonexistent");

        Optional<Goal> result = goalRepository.findByUser(nonExistentUser);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUser_WithUserHavingNoGoal_ShouldReturnEmpty() {
        // Given
        User userWithNoGoal = new User();
        userWithNoGoal.setUsername("usernogoal");
        userWithNoGoal.setEmail("nogoal@example.com");
        userWithNoGoal.setPassword("password");
        userWithNoGoal.setRole(UserRole.USER);
        entityManager.persist(userWithNoGoal);
        entityManager.flush();

        // When
        Optional<Goal> result = goalRepository.findByUser(userWithNoGoal);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void save_ShouldPersistNewGoal() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("newpassword");
        newUser.setRole(UserRole.USER);
        entityManager.persist(newUser);

        Goal newGoal = new Goal();
        newGoal.setUser(newUser);
        newGoal.setType(GoalType.MAINTAIN_WEIGHT);
        newGoal.setTargetWeight(75.0);
        newGoal.setDailyCalorieGoal(2500.0);

        // When
        Goal savedGoal = goalRepository.save(newGoal);
        entityManager.flush();

        // Then
        assertNotNull(savedGoal.getId());

        // Verify it's in the database
        Optional<Goal> retrievedGoal = goalRepository.findById(savedGoal.getId());
        assertTrue(retrievedGoal.isPresent());
        assertEquals(GoalType.MAINTAIN_WEIGHT, retrievedGoal.get().getType());
        assertEquals(75.0, retrievedGoal.get().getTargetWeight());
        assertEquals(2500.0, retrievedGoal.get().getDailyCalorieGoal());
        assertEquals(newUser.getId(), retrievedGoal.get().getUser().getId());
    }

    @Test
    void update_ShouldUpdateExistingGoal() {
        // Given
        testGoal1.setType(GoalType.MAINTAIN_WEIGHT);
        testGoal1.setTargetWeight(72.5);
        testGoal1.setDailyCalorieGoal(2200.0);

        // When
        Goal updatedGoal = goalRepository.save(testGoal1);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Goal> result = goalRepository.findById(testGoal1.getId());
        assertTrue(result.isPresent());
        assertEquals(GoalType.MAINTAIN_WEIGHT, result.get().getType());
        assertEquals(72.5, result.get().getTargetWeight());
        assertEquals(2200.0, result.get().getDailyCalorieGoal());
    }

    @Test
    void findAll_ShouldReturnAllGoals() {
        // When
        List<Goal> goals = goalRepository.findAll();

        // Then
        assertThat(goals).hasSize(2);
        assertThat(goals).extracting(Goal::getId).containsExactlyInAnyOrder(testGoal1.getId(), testGoal2.getId());
    }

    @Test
    void deleteById_ShouldRemoveGoal() {
        // Given
        Long idToDelete = testGoal1.getId();

        // When
        goalRepository.deleteById(idToDelete);
        entityManager.flush();

        // Then
        Optional<Goal> result = goalRepository.findById(idToDelete);
        assertFalse(result.isPresent());

        // Verify other goal still exists
        assertEquals(1, goalRepository.count());
    }

    @Test
    void deleteByUser_ShouldRemoveUserGoal() {
        // When
        goalRepository.deleteByUser(testUser1);
        entityManager.flush();

        // Then
        Optional<Goal> result = goalRepository.findByUser(testUser1);
        assertFalse(result.isPresent());

        // Verify other goal still exists
        assertEquals(1, goalRepository.count());
        assertTrue(goalRepository.findByUser(testUser2).isPresent());
    }
}
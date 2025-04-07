package com.example.calorator.integration.repository;

import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.UserRole;
import com.example.calorator.repository.UserRepository;
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
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();
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
        testUser2.setRole(UserRole.ADMIN);
        entityManager.persist(testUser2);

        entityManager.flush();
    }

    @Test
    void findById_ShouldReturnUser() {

        Optional<User> result = userRepository.findById(testUser1.getId());

        assertTrue(result.isPresent());
        assertEquals(testUser1.getUsername(), result.get().getUsername());
        assertEquals(testUser1.getEmail(), result.get().getEmail());
    }

    @Test
    void findByUsername_ShouldReturnUser() {

        Optional<User> result = userRepository.findByUsername("testuser1");

        assertTrue(result.isPresent());
        assertEquals(testUser1.getId(), result.get().getId());
        assertEquals(testUser1.getEmail(), result.get().getEmail());
    }

    @Test
    void findByUsername_WithNonExistentUsername_ShouldReturnEmpty() {

        Optional<User> result = userRepository.findByUsername("nonexistentuser");

        assertFalse(result.isPresent());
    }

    @Test
    void existsByUsernameOrEmail_WithExistingUsername_ShouldReturnTrue() {

        boolean exists = userRepository.existsByUsernameOrEmail("testuser1", "nonexistent@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByUsernameOrEmail_WithExistingEmail_ShouldReturnTrue() {

        boolean exists = userRepository.existsByUsernameOrEmail("nonexistentuser", "test2@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByUsernameOrEmail_WithNonExistentCredentials_ShouldReturnFalse() {

        boolean exists = userRepository.existsByUsernameOrEmail("nonexistentuser", "nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    void save_ShouldPersistNewUser() {

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("newpassword");
        newUser.setRole(UserRole.USER);

        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId());

        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals("newuser", retrievedUser.get().getUsername());
        assertEquals("new@example.com", retrievedUser.get().getEmail());
    }

    @Test
    void findAll_ShouldReturnAllUsers() {

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername).containsExactlyInAnyOrder("testuser1", "testuser2");
    }

    @Test
    void delete_ShouldRemoveUser() {

        Long idToDelete = testUser1.getId();

        userRepository.deleteById(idToDelete);
        entityManager.flush();

        Optional<User> result = userRepository.findById(idToDelete);
        assertFalse(result.isPresent());

        assertEquals(1, userRepository.count());
    }

    @Test
    void update_ShouldUpdateExistingUser() {

        testUser1.setEmail("updated@example.com");

        User updatedUser = userRepository.save(testUser1);
        entityManager.flush();
        entityManager.clear();

        Optional<User> result = userRepository.findById(testUser1.getId());
        assertTrue(result.isPresent());
        assertEquals("updated@example.com", result.get().getEmail());
        assertEquals("testuser1", result.get().getUsername());
    }
}
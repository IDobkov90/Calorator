package com.example.calorator.service.impl;

import com.example.calorator.mapper.UserMapper;
import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserProfileDTO;
import com.example.calorator.model.dto.UserRegisterDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.entity.UserProfile;
import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.Gender;
import com.example.calorator.model.enums.UserRole;
import com.example.calorator.repository.UserProfileRepository;
import com.example.calorator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserProfile testUserProfile;
    private UserDTO testUserDTO;
    private UserRegisterDTO testUserRegisterDTO;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.USER);

        testUserProfile = new UserProfile();
        testUserProfile.setId(1L);
        testUserProfile.setFirstName("Test");
        testUserProfile.setLastName("User");
        testUserProfile.setGender(Gender.MALE);
        testUserProfile.setAge(30);
        testUserProfile.setHeight(180.0);
        testUserProfile.setWeight(80.0);
        testUserProfile.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        testUserProfile.setUser(testUser);
    
        testUser.setUserProfile(testUserProfile);

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setUsername("testuser");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setRole(UserRole.USER);
    
        testUserRegisterDTO = new UserRegisterDTO();
        testUserRegisterDTO.setUsername("testuser");
        testUserRegisterDTO.setEmail("test@example.com");
        testUserRegisterDTO.setPassword("password");
        testUserRegisterDTO.setConfirmPassword("password");
        testUserRegisterDTO.setFirstName("Test");
        testUserRegisterDTO.setLastName("User");
        testUserRegisterDTO.setGender(Gender.MALE);
        testUserRegisterDTO.setAge(30);
        testUserRegisterDTO.setHeight(180.0);
        testUserRegisterDTO.setWeight(80.0);
        testUserRegisterDTO.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
    }

    @Test
    void register_FirstUser_ShouldCreateAdminUser() {

        when(userRepository.count()).thenReturn(0L);
        when(userMapper.toUser(testUserRegisterDTO)).thenReturn(testUser);
        when(passwordEncoder.encode(testUserRegisterDTO.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toUserProfile(testUserRegisterDTO, testUser)).thenReturn(testUserProfile);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userProfileRepository.save(testUserProfile)).thenReturn(testUserProfile);
        when(userMapper.toUserDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.register(testUserRegisterDTO);

        assertNotNull(result);
        assertEquals(testUserDTO.getUsername(), result.getUsername());
        assertEquals(testUserDTO.getEmail(), result.getEmail());

        verify(userRepository).count();
        verify(userMapper).toUser(testUserRegisterDTO);
        verify(passwordEncoder).encode(testUserRegisterDTO.getPassword());
        verify(userMapper).toUserProfile(testUserRegisterDTO, testUser);
        verify(userRepository, times(2)).save(testUser);
        verify(userProfileRepository).save(testUserProfile);
        verify(userMapper).toUserDTO(testUser);
    }

    @Test
    void register_SubsequentUser_ShouldCreateRegularUser() {

        when(userRepository.count()).thenReturn(1L);
        when(userMapper.toUser(testUserRegisterDTO)).thenReturn(testUser);
        when(passwordEncoder.encode(testUserRegisterDTO.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toUserProfile(testUserRegisterDTO, testUser)).thenReturn(testUserProfile);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userProfileRepository.save(testUserProfile)).thenReturn(testUserProfile);
        when(userMapper.toUserDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.register(testUserRegisterDTO);

        assertNotNull(result);
        assertEquals(testUserDTO.getUsername(), result.getUsername());
        assertEquals(testUserDTO.getEmail(), result.getEmail());

        verify(userRepository).count();
        verify(userMapper).toUser(testUserRegisterDTO);
        verify(passwordEncoder).encode(testUserRegisterDTO.getPassword());
        verify(userMapper).toUserProfile(testUserRegisterDTO, testUser);
        verify(userRepository, times(2)).save(testUser);
        verify(userProfileRepository).save(testUserProfile);
        verify(userMapper).toUserDTO(testUser);
    }

    @Test
    void existsByUsernameOrEmail_WhenExists_ShouldReturnTrue() {

        when(userRepository.existsByUsernameOrEmail("testuser", "test@example.com")).thenReturn(true);

        boolean result = userService.existsByUsernameOrEmail("testuser", "test@example.com");

        assertTrue(result);
        verify(userRepository).existsByUsernameOrEmail("testuser", "test@example.com");
    }

    @Test
    void existsByUsernameOrEmail_WhenNotExists_ShouldReturnFalse() {

        when(userRepository.existsByUsernameOrEmail("testuser", "test@example.com")).thenReturn(false);

        boolean result = userService.existsByUsernameOrEmail("testuser", "test@example.com");

        assertFalse(result);
        verify(userRepository).existsByUsernameOrEmail("testuser", "test@example.com");
    }

    @Test
    void changePassword_WithCorrectCurrentPassword_ShouldUpdatePassword() {

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        userService.changePassword("testuser", "currentPassword", "newPassword");

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("currentPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(testUser);
        assertEquals("newEncodedPassword", testUser.getPassword());
    }

    @Test
    void changePassword_WithIncorrectCurrentPassword_ShouldThrowException() {

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.changePassword("testuser", "wrongPassword", "newPassword")
        );
        assertEquals("Current password is incorrect", exception.getMessage());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_WithNonExistingUser_ShouldThrowException() {

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userService.changePassword("nonexistent", "currentPassword", "newPassword")
        );

        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserProfile_WithExistingUser_ShouldReturnUserProfile() {

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserProfileDTO result = userService.getUserProfile("testuser");

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUserProfile.getFirstName(), result.getFirstName());
        assertEquals(testUserProfile.getLastName(), result.getLastName());
        assertEquals(testUserProfile.getGender(), result.getGender());
        assertEquals(testUserProfile.getAge(), result.getAge());
        assertEquals(testUserProfile.getHeight(), result.getHeight());
        assertEquals(testUserProfile.getWeight(), result.getWeight());
        assertEquals(testUserProfile.getActivityLevel(), result.getActivityLevel());
        assertNotNull(result.getDailyCalorieGoal());
    }
}
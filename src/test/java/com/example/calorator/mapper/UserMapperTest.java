package com.example.calorator.mapper;

import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserRegisterDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.entity.UserProfile;
import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.Gender;
import com.example.calorator.model.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUser_shouldMapUserRegisterDTOToUser() {

        UserRegisterDTO userRegisterDTO = getUserRegisterDTO();

        User user = userMapper.toUser(userRegisterDTO);

        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());

        assertNull(user.getPassword());

        assertNull(user.getId());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
        assertNull(user.getRole());
        assertNull(user.getUserProfile());
    }

    private static UserRegisterDTO getUserRegisterDTO() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUsername("testUser");
        userRegisterDTO.setEmail("test@example.com");
        userRegisterDTO.setPassword("password123");
        userRegisterDTO.setFirstName("John");
        userRegisterDTO.setLastName("Doe");
        userRegisterDTO.setAge(30);
        userRegisterDTO.setHeight(180.0);
        userRegisterDTO.setWeight(75.0);
        userRegisterDTO.setGender(Gender.MALE);
        userRegisterDTO.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        return userRegisterDTO;
    }

    @Test
    void toUserProfile_shouldMapUserRegisterDTOToUserProfile() {

        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("John");
        userRegisterDTO.setLastName("Doe");
        userRegisterDTO.setAge(30);
        userRegisterDTO.setHeight(180.0);
        userRegisterDTO.setWeight(75.0);
        userRegisterDTO.setGender(Gender.MALE);
        userRegisterDTO.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        UserProfile userProfile = userMapper.toUserProfile(userRegisterDTO, user);

        assertNotNull(userProfile);
        assertEquals("John", userProfile.getFirstName());
        assertEquals("Doe", userProfile.getLastName());
        assertEquals(30, userProfile.getAge());
        assertEquals(180.0, userProfile.getHeight());
        assertEquals(75.0, userProfile.getWeight());
        assertEquals(Gender.MALE, userProfile.getGender());
        assertEquals(ActivityLevel.MODERATELY_ACTIVE, userProfile.getActivityLevel());

        assertNull(userProfile.getId());
        assertNull(userProfile.getCreatedAt());
        assertNull(userProfile.getUpdatedAt());

        assertSame(user, userProfile.getUser());
    }

    @Test
    void toUserDTO_shouldMapUserToUserDTO() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setRole(UserRole.USER);  // Set the role explicitly

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setAge(30);
        userProfile.setHeight(180.0);
        userProfile.setWeight(75.0);
        userProfile.setGender(Gender.MALE);
        userProfile.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);

        user.setUserProfile(userProfile);
        userProfile.setUser(user);

        UserDTO userDTO = userMapper.toUserDTO(user);

        assertNotNull(userDTO);
        assertEquals(1L, userDTO.getId());
        assertEquals("testUser", userDTO.getUsername());
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals(UserRole.USER, userDTO.getRole());
    }
}
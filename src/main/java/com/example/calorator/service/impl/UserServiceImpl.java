package com.example.calorator.service.impl;

import com.example.calorator.mapper.UserMapper;
import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserProfileDTO;
import com.example.calorator.model.dto.UserRegisterDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.entity.UserProfile;
import com.example.calorator.model.enums.Gender;
import com.example.calorator.model.enums.UserRole;
import com.example.calorator.repository.UserProfileRepository;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO register(UserRegisterDTO userRegisterDTO) {
        User user = userMapper.toUser(userRegisterDTO);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));

        if (userRepository.count() == 0) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);

        UserProfile userProfile = userMapper.toUserProfile(userRegisterDTO, user);
        userProfileRepository.save(userProfile);

        user.setUserProfile(userProfile);
        userRepository.save(user);
        return userMapper.toUserDTO(user);
    }

    @Override
    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }

    @Override
    public UserProfileDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    
        UserProfile profile = user.getUserProfile();
    
        return UserProfileDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .gender(profile.getGender())
                .age(profile.getAge())
                .height(profile.getHeight())
                .weight(profile.getWeight())
                .activityLevel(profile.getActivityLevel())
                .dailyCalorieGoal(calculateDailyCalorieGoal(profile))
                .build();
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserDTO);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO updateUserRole(String username, UserRole role) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName().equals(username)
                && user.getRole() == UserRole.ADMIN) {
            throw new IllegalStateException("Administrators cannot change their own role");
        }

        user.setRole(role);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    @Override
    public UserRole[] getAllAvailableRoles() {
        return UserRole.values();
    }

    private Integer calculateDailyCalorieGoal(UserProfile profile) {
        double bmr;
        if (profile.getGender() == Gender.MALE) {
            bmr = 88.362 + (13.397 * profile.getWeight()) + (4.799 * profile.getHeight()) - (5.677 * profile.getAge());
        } else {
            bmr = 447.593 + (9.247 * profile.getWeight()) + (3.098 * profile.getHeight()) - (4.330 * profile.getAge());
        }

        double activityMultiplier = profile.getActivityLevel().getFactor();
        
        return (int) Math.round(bmr * activityMultiplier);
    }
}

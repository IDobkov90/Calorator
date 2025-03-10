package com.example.calorator.service.impl;

import com.example.calorator.mapper.UserMapper;
import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserRegisterDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.entity.UserProfile;
import com.example.calorator.model.enums.UserRole;
import com.example.calorator.repository.UserProfileRepository;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Context;
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
}

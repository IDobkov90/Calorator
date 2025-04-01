package com.example.calorator.service;

import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserProfileDTO;
import com.example.calorator.model.dto.UserRegisterDTO;
import com.example.calorator.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO register(UserRegisterDTO userRegisterDTO);

    boolean existsByUsernameOrEmail(String username, String email);

    void changePassword(String username, String currentPassword, String newPassword);

    UserProfileDTO getUserProfile(String username);
    
    Page<UserDTO> getAllUsers(Pageable pageable);

    UserDTO getUserByUsername(String username);

    UserDTO updateUserRole(String username, UserRole role);

    UserRole[] getAllAvailableRoles();
}

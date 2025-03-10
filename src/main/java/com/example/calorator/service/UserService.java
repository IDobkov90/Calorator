package com.example.calorator.service;

import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserRegisterDTO;

public interface UserService {
    UserDTO register(UserRegisterDTO userRegisterDTO);
    boolean existsByUsernameOrEmail(String username, String email);
    void changePassword(String username, String currentPassword, String newPassword);
}

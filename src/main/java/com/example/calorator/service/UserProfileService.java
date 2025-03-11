package com.example.calorator.service;

import com.example.calorator.model.dto.UserProfileDTO;

public interface UserProfileService {
    UserProfileDTO getProfileByUsername(String username);
}

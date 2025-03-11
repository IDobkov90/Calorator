package com.example.calorator.service.impl;

import com.example.calorator.model.dto.UserProfileDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.entity.UserProfile;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;

    @Override
    public UserProfileDTO getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);
        if (user == null || user.getUserProfile() == null) {
            return null;
        }
        UserProfile profile = user.getUserProfile();
        UserProfileDTO dto = new UserProfileDTO();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setAge(profile.getAge());
        dto.setGender(profile.getGender());
        dto.setWeight(profile.getWeight());
        dto.setHeight(profile.getHeight());
        dto.setActivityLevel(profile.getActivityLevel());
        return dto;
    }
}

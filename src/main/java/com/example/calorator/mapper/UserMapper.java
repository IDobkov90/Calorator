package com.example.calorator.mapper;

import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserRegisterDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    User toUser(UserRegisterDTO userRegisterDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", expression = "java(user)")
    UserProfile toUserProfile(UserRegisterDTO userRegisterDTO, User user);

    UserDTO toUserDTO(User user);
}
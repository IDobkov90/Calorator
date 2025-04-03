package com.example.calorator.security;

import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.UserRole;
import com.example.calorator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;
    private final String testUsername = "testUser";
    private final String testPassword = "encodedPassword";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(testUsername);
        testUser.setPassword(testPassword);
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.USER);
    }

    @Test
    void loadUserByUsername_WithValidUsername_ShouldReturnUserDetails() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(testUsername);

        assertNotNull(userDetails);
        assertEquals(testUsername, userDetails.getUsername());
        assertEquals(testPassword, userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    void loadUserByUsername_WithAdminRole_ShouldReturnAdminAuthority() {

        testUser.setRole(UserRole.ADMIN);
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(testUsername);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    void loadUserByUsername_WithInvalidUsername_ShouldThrowException() {

        String nonExistentUsername = "nonexistent";
        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(nonExistentUsername)
        );

        assertEquals("User not found with username: " + nonExistentUsername, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(nonExistentUsername);
    }

    @Test
    void loadUserByUsername_ShouldReturnEnabledUser() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(testUsername);

        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());

        verify(userRepository, times(1)).findByUsername(testUsername);
    }
}
package com.felix.healthcare.api_core.service;

import com.felix.healthcare.api_core.dto.UsersDto;
import com.felix.healthcare.api_core.entity.Roles;
import com.felix.healthcare.api_core.entity.Users;
import com.felix.healthcare.api_core.repository.RoleRepository;
import com.felix.healthcare.api_core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private UsersDto.SaveRequest validSaveRequest;
    private UsersDto.SaveRequest existingUserRequest;

    @BeforeEach
    void setUp() {
        // Set up a valid save request
        validSaveRequest = new UsersDto.SaveRequest();
        validSaveRequest.setUsername("new-user");
        validSaveRequest.setEmail("newuser@example.com");
        validSaveRequest.setPassword("password123");
        validSaveRequest.setRolesName(Set.of("user"));

        // Set up a request for an existing user
        existingUserRequest = new UsersDto.SaveRequest();
        existingUserRequest.setUsername("existing-user");
        existingUserRequest.setEmail("existinguser@example.com");
        existingUserRequest.setPassword("password123");
        existingUserRequest.setRolesName(Set.of("user"));
    }

    @Test
    void save_shouldSaveUser_whenUserDoesNotExist() throws Exception {
        // Arrange
        when(userRepository.findByUsername(validSaveRequest.getUsername())).thenReturn(Optional.empty());
        Roles role = new Roles();
        role.setName("user");
        when(roleRepository.findByName("user")).thenReturn(role);
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Users savedUser = userService.save(validSaveRequest);

        // Assert
        assertNotNull(savedUser);
        assertEquals("new-user", savedUser.getUsername());
        assertEquals("newuser@example.com", savedUser.getEmail());
        assertNotNull(savedUser.getPassword());
        assertEquals(1, savedUser.getRoles().size());
        assertEquals("user", savedUser.getRoles().iterator().next().getName());

        // Verify interactions
        verify(userRepository).findByUsername(validSaveRequest.getUsername());
        verify(roleRepository).findByName("user");
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void save_shouldThrowException_whenUserAlreadyExists() {
        // Arrange
        when(userRepository.findByUsername(existingUserRequest.getUsername())).thenReturn(Optional.of(new Users()));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> userService.save(existingUserRequest));
        assertEquals("User with this existing-user already exists", exception.getMessage());

        // Verify interactions
        verify(userRepository).findByUsername(existingUserRequest.getUsername());
        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any(Users.class));
    }
}
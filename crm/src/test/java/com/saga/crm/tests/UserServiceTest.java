package com.saga.crm.tests;

import com.saga.crm.dto.UserDto;
import com.saga.crm.model.User;
import com.saga.crm.repositories.UserRepository;
import com.saga.crm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password123");
        userDto.setProfileImage("base64EncodedImageString");

        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.registerUser(userDto);

        assertNotNull(createdUser, "O usuário não deve ser nulo.");
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals(LocalDate.now(), createdUser.getDataCadastro());
        assertTrue(createdUser.isAtivo());
        assertTrue(createdUser.isTagAlterarSenha());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindUserByEmail_UserExists() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        User foundUser = userService.findUserByEmail(email);

        assertNotNull(foundUser, "Usuário encontrado não deve ser nulo.");
        assertEquals(email, foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindUserByEmail_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        User foundUser = userService.findUserByEmail(email);

        assertNull(foundUser, "Usuário não deve ser encontrado para um email inexistente.");
        verify(userRepository, times(1)).findByEmail(email);
    }
}

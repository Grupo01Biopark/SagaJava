package com.saga.crm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saga.crm.dto.UserDto;
import com.saga.crm.model.User;

@Service
public interface UserService {
    void saveUser(User user);
    void saveUser(UserDto userDto);
    User findUserByEmail(String email);
    List<UserDto> findAllUsers();
    void deleteUser(Long id); 
    void updateUserProfile(UserDto userDto);
    void updatePassword(String email, String newPassword);
    boolean isPasswordMatches(User user, String currentPassword);
    User getUserById(Long id);
    List<User> getAllUsers();
    boolean emailJaCadastrado(String email);
    void editUser(User user);
}
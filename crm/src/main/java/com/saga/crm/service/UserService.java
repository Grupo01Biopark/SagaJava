package com.saga.crm.service;

import com.saga.crm.dto.UserDto;
import com.saga.crm.model.User;
import com.saga.crm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserDto userDto) {
        User newUser = new User();
        newUser.setName(userDto.getName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setDataCadastro(LocalDate.now());
        newUser.setTagAlterarSenha(true);
        newUser.setAtivo(true);

        if (userDto.getProfileImage() != null) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(userDto.getProfileImage());
                String directoryPath = "src/main/resources/images";
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String imagePath = directoryPath + "/" + userDto.getEmail() + "_profile.png";
                try (FileOutputStream fos = new FileOutputStream(new File(imagePath))) {
                    fos.write(imageBytes);
                }
                newUser.setProfileImage(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar a imagem de perfil", e);
            }
        }
        return userRepository.save(newUser);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User editUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Atualizar nome e email
        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());


        // Atualizar a imagem de perfil se fornecida
        if (userDto.getProfileImage() != null) {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(userDto.getProfileImage());
                String directoryPath = "src/main/resources/images";
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String imagePath = directoryPath + "/" + userDto.getEmail() + "_profile.png";
                try (FileOutputStream fos = new FileOutputStream(new File(imagePath))) {
                    fos.write(imageBytes);
                }
                existingUser.setProfileImage(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar a imagem de perfil", e);
            }
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deactivateUserById(id);
    }
    public boolean emailJaCadastrado(String email) {
        return userRepository.existsByEmail(email);
    }
}

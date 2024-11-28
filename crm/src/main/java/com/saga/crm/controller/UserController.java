package com.saga.crm.controller;
import com.saga.crm.dto.UserDto;
import com.saga.crm.model.User;
import com.saga.crm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/listar/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Usuário não encontrado"));
        }

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("password", user.getPassword());
        userMap.put("dataCadastro", user.getDataCadastro());
        userMap.put("ativo", user.isAtivo());
        userMap.put("roles", user.getRoles());
        userMap.put("tagAlterarSenha", user.isTagAlterarSenha());

        if (user.getProfileImage() != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(Paths.get(user.getProfileImage()));
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                userMap.put("profileImage", base64Image);
            } catch (IOException e) {
                e.printStackTrace();
                userMap.put("profileImage", null);
            }
        } else {
            userMap.put("profileImage", null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("user", userMap);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> usersWithImages = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("name", user.getName());
            userMap.put("email", user.getEmail());
            userMap.put("password", user.getPassword());
            userMap.put("dataCadastro", user.getDataCadastro());
            userMap.put("ativo", user.isAtivo());
            userMap.put("roles", user.getRoles());
            userMap.put("tagAlterarSenha", user.isTagAlterarSenha());

            if (user.getProfileImage() != null) {
                try {
                    byte[] imageBytes = Files.readAllBytes(Paths.get(user.getProfileImage()));
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    userMap.put("profileImage", base64Image);
                } catch (IOException e) {
                    e.printStackTrace(); // Handle exception as needed
                    userMap.put("profileImage", null);
                }
            } else {
                userMap.put("profileImage", null);
            }

            usersWithImages.add(userMap);
        }

        response.put("users", usersWithImages);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/verificarEmail")
    public ResponseEntity<?> verificarEmail(@RequestParam String email) {
        try {
            boolean emailCadastrado = userService.emailJaCadastrado(email);
            return ResponseEntity.ok(Map.of("emailCadastrado", emailCadastrado));
        } catch (Exception e) {
            return handleErrorResponse("Erro ao verificar email", e);
        }
    }
    @PostMapping("/editar/{id}")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Usuário não encontrado"));
            }
            if(!Objects.equals(user.getEmail(), userDto.getEmail())){
                if (userService.emailJaCadastrado(userDto.getEmail())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("error", false, "message", "Email já cadastrado"));
                }
            }
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            userService.editUser(id, userDto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuário editado com sucesso");
            response.put("data", Map.of("id", user.getId(), "nome", user.getName()));
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return handleErrorResponse("Erro ao editar usuário", e);
        }
    }
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Usuário excluído com sucesso", "data", Map.of("id", id)));
        } catch (Exception e) {
            return handleErrorResponse("Erro ao excluir usuário", e);
        }
    }
    private ResponseEntity<Map<String, Object>> handleErrorResponse(String message, Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
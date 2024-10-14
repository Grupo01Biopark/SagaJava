package com.saga.crm.controller;
import com.saga.crm.dto.UserDto;
import com.saga.crm.model.User;
import com.saga.crm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
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
            if (userService.emailJaCadastrado(userDto.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", false, "message", "Email já cadastrado"));
            }
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            userService.editUser(user);
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
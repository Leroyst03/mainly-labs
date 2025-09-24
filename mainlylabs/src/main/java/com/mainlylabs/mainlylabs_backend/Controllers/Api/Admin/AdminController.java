package com.mainlylabs.mainlylabs_backend.Controllers.Api.Admin;

import com.mainlylabs.mainlylabs_backend.DTOs.UserDTO;
import com.mainlylabs.mainlylabs_backend.DTOs.UserInfo;
import com.mainlylabs.mainlylabs_backend.Exceptions.HttpException;
import com.mainlylabs.mainlylabs_backend.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> showUsers() {
        try {
            List<UserDTO> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        } catch (HttpException err) {
            return ResponseEntity.status(err.getStatus()).body(err.getMessage());
        }
    }

    @GetMapping("/users/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> showUser(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userService.findUser(email));
        } catch (HttpException err) {
            return ResponseEntity.status(err.getStatus()).body(err.getMessage());
        }
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody UserInfo newUser) {
        try {
            // Si no viene rol, por defecto ROLE_USER
            if (newUser.getRole() == null || newUser.getRole().isBlank()) {
                newUser.setRole("ROLE_USER");
            }
            userService.saveUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado en LDAP correctamente");
        } catch (HttpException err) {
            return ResponseEntity.status(err.getStatus()).body(err.getMessage());
        }
    }

    @PutMapping("/users/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody UserInfo userInfo) {
        try {
            userService.updateUser(userInfo);
            return ResponseEntity.ok("Usuario actualizado en LDAP correctamente");
        } catch (HttpException err) {
            return ResponseEntity.status(err.getStatus()).body(err.getMessage());
        }
    }

    @DeleteMapping("/users/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        try {
            userService.deleteUser(email);
            return ResponseEntity.ok("Usuario eliminado de LDAP correctamente");
        } catch (HttpException err) {
            return ResponseEntity.status(err.getStatus()).body(err.getMessage());
        }
    }
}

package com.mainlylabs.mainlylabs_backend.Controllers.Auth;

import com.mainlylabs.mainlylabs_backend.DS.AttemptInfo;
import com.mainlylabs.mainlylabs_backend.DTOs.UserInfo;
import com.mainlylabs.mainlylabs_backend.Exceptions.HttpException;
import com.mainlylabs.mainlylabs_backend.Services.AttemptService;
import com.mainlylabs.mainlylabs_backend.Services.IpBanService;
import com.mainlylabs.mainlylabs_backend.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final UserService userService;
    private final AttemptService attemptService;
    private final IpBanService ipBanService;

    public LoginController(UserService userService, AttemptService attemptService, IpBanService ipBanService) {
        this.userService = userService;
        this.attemptService = attemptService;
        this.ipBanService = ipBanService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        try {
            String ip = request.getRemoteAddr();

            // Si estamos detrás de un proxy o balanceador
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null) {
                ip = forwarded.split(",")[0].trim();
            }

            // Verificar si la IP está temporalmente bloqueada
            if(ipBanService.isIpBanned(ip)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Demasiados intentos fallidos. Intente nuevamente en una hora.");
            }

            // Obtener o inicializar información de intentos
            AttemptInfo info = attemptService.getAttempts(ip);
            int currentAttempts = info.getNumberAttempt();
            LocalDateTime lastAttempt = info.getLastAttempt();

            if(attemptService.checkTime(lastAttempt)) {
                currentAttempts = 0;
            }

            // Verificar si aún tiene intentos disponibles

            String answer = userService.logUser(userInfo.getEmail(), userInfo.getPassword());

            if (answer == null) {
                int newAttempt = currentAttempts + 1;
                lastAttempt = LocalDateTime.now();
                AttemptInfo attemptInfo = new AttemptInfo(newAttempt, lastAttempt);

                if (newAttempt >= 3) {
                    ipBanService.banIp(ip, lastAttempt);
                    attemptService.deleteAttempts(ip);

                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Demasiados intentos fallidos. Intente nuevamente en una hora.");
                    }
                    else {
                        attemptService.setAttempts(ip, attemptInfo);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Contraseña incorrecta. Quedan " + (3 - newAttempt) + " intentos.");
                    }
            }

            // Login exitoso
            attemptService.deleteAttempts(ip);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(answer);
        } catch (HttpException err) {
            return ResponseEntity.status(err.getStatus()).body(err.getMessage());
        }
    }
}

package com.mainlylabs.mainlylabs_backend.Controllers.Auth;


import com.mainlylabs.mainlylabs_backend.DTOs.RecoverInfo;
import com.mainlylabs.mainlylabs_backend.Exceptions.HttpException;
import com.mainlylabs.mainlylabs_backend.Services.EmailService;
import com.mainlylabs.mainlylabs_backend.Services.EnCodeService;
import com.mainlylabs.mainlylabs_backend.Services.IpBanService;
import com.mainlylabs.mainlylabs_backend.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RecoverController {
    private final EnCodeService enCodeService;
    private final UserService userService;
    private final IpBanService ipBanService;
    private final EmailService emailService;

    public RecoverController(EmailService emailService, EnCodeService enCodeService, UserService userService, IpBanService ipBanService) {
        this.enCodeService = enCodeService;
        this.userService = userService;
        this.ipBanService = ipBanService;
        this.emailService = emailService;
    }

    @GetMapping("/recover")
    public ResponseEntity<?> getCode(@RequestBody RecoverInfo recoverInfo) {
        try {
            String code = enCodeService.makeCode();
            emailService.sendEmail(recoverInfo.getEmail(), "Codigo de recuperacion", code);

            return ResponseEntity.status(HttpStatus.CREATED).body("Se ha enviado el codigo de recuperacion a su correo");
        } catch (HttpException err) {
            return ResponseEntity.status(err.getStatus()).body(err.getMessage());
        }
    }

    @PostMapping("/recover")
    public ResponseEntity<?> recover(@RequestBody RecoverInfo recoverInfo, HttpServletRequest request) {
        try {
            String ip = request.getRemoteAddr();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null) {
                ip = forwarded.split(",")[0].trim();
            }

            if(enCodeService.checkCode(recoverInfo.getCode())) {
                userService.updatePassword(recoverInfo.getEmail(), recoverInfo.getPassword());
                ipBanService.disBannIp(ip);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Cambio de contraseña realizado");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El codigo es incorrecto");
        } catch (HttpException err) {
            return  ResponseEntity.status(err.getStatus()).body(err.getMessage());
        }

    }

}

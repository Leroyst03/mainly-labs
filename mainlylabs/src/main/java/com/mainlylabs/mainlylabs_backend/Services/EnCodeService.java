package com.mainlylabs.mainlylabs_backend.Services;

import com.mainlylabs.mainlylabs_backend.Exceptions.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EnCodeService {
    private String code;
    private final ConcurrentHashMap<String, LocalDateTime> currentCodes = new ConcurrentHashMap<>();

    public String makeCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()_-+:?><";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(index));
        }

        String code = codeBuilder.toString();
        currentCodes.put(code, LocalDateTime.now());
        return code;
    }

    public boolean checkCode(String actualCode) {
        if(actualCode == null) {
            return false;
        }
        if (!currentCodes.containsKey(actualCode)) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "El codigo es incorrecto");
        }

        LocalDateTime actual = LocalDateTime.now();
        LocalDateTime generatedTime = currentCodes.get(actualCode);

        Duration difference = Duration.between(generatedTime, actual);

        if(difference.toMinutes() <= 5) {
            currentCodes.remove(actualCode);
            return  true;
        }
        return false;
    }
}

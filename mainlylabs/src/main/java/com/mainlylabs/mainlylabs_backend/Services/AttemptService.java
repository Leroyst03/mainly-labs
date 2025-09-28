package com.mainlylabs.mainlylabs_backend.Services;

import com.mainlylabs.mainlylabs_backend.DS.AttemptInfo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AttemptService {
    private final ConcurrentHashMap<String, AttemptInfo> ipControl = new ConcurrentHashMap<>();

    public void deleteAttempts(String ip) {
        ipControl.remove(ip);
    }

    public AttemptInfo getAttempts(String ip) {
        return ipControl.computeIfAbsent(ip, k -> new AttemptInfo(0, LocalDateTime.now()));
    }

    // Incremento atómico: devuelve el nuevo AttemptInfo
    public AttemptInfo incrementAttempts(String ip) {
        return ipControl.compute(ip, (key, oldInfo) -> {
            if (oldInfo == null) {
                return new AttemptInfo(1, LocalDateTime.now());
            }
            return new AttemptInfo(oldInfo.numberAttempt() + 1, LocalDateTime.now());
        });
    }

    public boolean checkTime(LocalDateTime time) {
        LocalDateTime actualTime = LocalDateTime.now();
        Duration difference = Duration.between(time, actualTime);
        return difference.toHours() > 1;
    }
}

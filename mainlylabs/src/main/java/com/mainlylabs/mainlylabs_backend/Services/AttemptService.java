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

    public void setAttempts(String ip, AttemptInfo attemptInfo) {
        ipControl.put(ip, attemptInfo);
    }

    public boolean checkTime(LocalDateTime time) {
        LocalDateTime acutalTime = LocalDateTime.now();

        Duration difference =   Duration.between(time, acutalTime);

        return difference.toHours() > 1;
    }

}

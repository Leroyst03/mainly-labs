package com.mainlylabs.mainlylabs_backend.Services;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IpBanService {
    private final ConcurrentHashMap<String, LocalDateTime> bannedIp = new ConcurrentHashMap<>();

    public boolean isIpBanned(String ip) {
        if (!bannedIp.containsKey(ip)) return false;

        LocalDateTime actualTime = LocalDateTime.now();
        LocalDateTime bannedTime = bannedIp.get(ip);
        Duration difference = Duration.between(bannedTime, actualTime);

        if (difference.toHours() < 1) {
            return true;
        }

        bannedIp.remove(ip);
        return false;
    }

    public void banIp(String ip, LocalDateTime firstAttempt) {
        bannedIp.put(ip, firstAttempt);
    }

    public void disBannIp(String ip) {
        if(bannedIp.containsKey(ip)) {
            bannedIp.remove(ip);
        }
    }

}

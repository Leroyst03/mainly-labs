package com.mainlylabs.mainlylabs_backend.DS;

import java.time.LocalDateTime;

public class AttemptInfo {
    private int numberAttempt;
    private LocalDateTime lastAttempt;

    public AttemptInfo(int numberAttempt, LocalDateTime firstAttempt) {
        this.numberAttempt = numberAttempt;
        this.lastAttempt = firstAttempt;
    }

    public LocalDateTime getLastAttempt() {
        return lastAttempt;
    }

    public int getNumberAttempt() {
        return numberAttempt;
    }

    public void setLastAttempt(LocalDateTime lastAttempt) {
        this.lastAttempt = lastAttempt;
    }

    public void setNumberAttempt(int numberAttempt) {
        this.numberAttempt = numberAttempt;
    }
}

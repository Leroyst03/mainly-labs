package com.mainlylabs.mainlylabs_backend.DTOs;

public class RecoverInfo {
    private String email;
    private String code;
    private String password;

    public RecoverInfo(String email,String code, String password) {
        this.email = email;
        this.code = code;
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

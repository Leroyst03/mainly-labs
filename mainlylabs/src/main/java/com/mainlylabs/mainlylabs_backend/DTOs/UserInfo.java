package com.mainlylabs.mainlylabs_backend.DTOs;

public class UserInfo {

    private String name;       // cn
    private String lastName;   // sn
    private String email;      // uid
    private String password;   // userPassword
    private String role;       // ROLE_USER o ROLE_ADMIN

    public UserInfo() {}

    public UserInfo(String name, String lastName, String email, String password, String role) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

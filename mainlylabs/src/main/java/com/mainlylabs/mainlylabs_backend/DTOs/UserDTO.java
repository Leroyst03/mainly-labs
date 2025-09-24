package com.mainlylabs.mainlylabs_backend.DTOs;

import com.mainlylabs.mainlylabs_backend.Entity.LdapUser;

public class UserDTO {
    private String name;
    private String sn;
    private String email;
    private String role;
    private String dn;

    public UserDTO(LdapUser user) {
        this.name = user.getName();
        this.sn = user.getSn();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.dn = user.getDn() != null ? user.getDn().toString() : null;
    }

    // Getters y setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }
}

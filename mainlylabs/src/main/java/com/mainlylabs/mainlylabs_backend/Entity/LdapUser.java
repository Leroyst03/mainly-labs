package com.mainlylabs.mainlylabs_backend.Entity;

import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Attribute;

import javax.naming.Name;

@Entry(objectClasses = {"inetOrgPerson", "organizationalPerson", "person", "top"}, base = "ou=people")
public class LdapUser {

    @Id
    private Name dn;

    @Attribute(name = "cn")
    private String name;

    @Attribute(name = "sn")
    private String sn;

    @Attribute(name = "uid")
    private String email;

    @Attribute(name = "userPassword")
    private String password;

    @Attribute(name = "memberOf")
    private String role;

    public Name getDn() { return dn; }
    public void setDn(Name dn) { this.dn = dn; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSn() { return sn; }
    public void setSn(String sn) { this.sn = sn; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

package com.mainlylabs.mainlylabs_backend.Services;

import com.mainlylabs.mainlylabs_backend.DTOs.UserInfo;
import com.mainlylabs.mainlylabs_backend.DTOs.UserDTO;
import com.mainlylabs.mainlylabs_backend.Entity.LdapUser;
import com.mainlylabs.mainlylabs_backend.Exceptions.HttpException;
import com.mainlylabs.mainlylabs_backend.Repository.UserRepository;
import com.mainlylabs.mainlylabs_backend.Security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LdapTemplate ldapTemplate;
    private final JwtUtil jwtUtil;


    public UserService(UserRepository userRepository,
                       LdapTemplate ldapTemplate,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.ldapTemplate = ldapTemplate;
        this.jwtUtil = jwtUtil;
    }

    public void saveUser(UserInfo dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "El email es obligatorio");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }

        Name dn = LdapNameBuilder.newInstance()
                .add("ou", "people")
                .add("uid", dto.getEmail())
                .build();

        try {
            ldapTemplate.lookup(dn);
            throw new HttpException(HttpStatus.CONFLICT, "El usuario ya existe");
        } catch (org.springframework.ldap.NameNotFoundException e) {
            // OK: el usuario no existe
        } catch (CommunicationException e) {
            throw new HttpException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo conectar con el servidor LDAP");
        } catch (NamingException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Error en la estructura LDAP: " + e.getMessage());
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al verificar el usuario");
        }

        try {
            LdapUser user = new LdapUser();
            user.setName(dto.getName());
            user.setSn(dto.getLastName() != null ? dto.getLastName() : dto.getName());
            user.setEmail(dto.getEmail());

            //Guardar contraseña en texto plano para que LDAP la valide directamente
            user.setPassword(dto.getPassword());

            user.setDn(dn);

            if ("ROLE_ADMIN".equalsIgnoreCase(dto.getRole())) {
                user.setRole("cn=ROLE_ADMIN,ou=groups,dc=maxcrc,dc=com");
            } else {
                user.setRole("cn=ROLE_USER,ou=groups,dc=maxcrc,dc=com");
            }

            ldapTemplate.create(user);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el usuario");
        }
    }


    public UserDTO findUser(String email) {
        try {
            LdapUser user = userRepository.findByEmail(email);
            if (user == null) {
                throw new HttpException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }
            return new UserDTO(user); // igual que en findAllUsers
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar el usuario");
        }
    }


    public void updateUser(UserInfo dto) {
        try {
            LdapUser user = userRepository.findByEmail(dto.getEmail());
            if (user == null) {
                throw new HttpException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }

            user.setName(dto.getName());
            user.setSn(dto.getLastName() != null ? dto.getLastName() : dto.getName());

            if ("ROLE_ADMIN".equalsIgnoreCase(dto.getRole())) {
                user.setRole("cn=ROLE_ADMIN,ou=groups,dc=maxcrc,dc=com");
            } else {
                user.setRole("cn=ROLE_USER,ou=groups,dc=maxcrc,dc=com");
            }

            // Guardar contraseña en texto plano si se proporciona
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(dto.getPassword());
            }

            userRepository.save(user);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el usuario");
        }
    }

    public void updatePassword(String email, String newPassword) {
        try {
            LdapUser user = userRepository.findByEmail(email);
            if (user == null) {
                throw new HttpException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }

            // Guardar nueva contraseña en texto plano
            user.setPassword(newPassword);
            userRepository.save(user);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la contraseña");
        }
    }

    public void deleteUser(String email) {
        try {
            LdapUser user = userRepository.findByEmail(email);
            if (user == null) {
                throw new HttpException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }

            userRepository.delete(user);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el usuario");
        }
    }

    public List<UserDTO> findAllUsers() {
        try {
            List<LdapUser> users = (List<LdapUser>) userRepository.findAll();
            if (users.isEmpty()) {
                throw new HttpException(HttpStatus.NO_CONTENT, "No hay usuarios registrados");
            }
            return users.stream().map(UserDTO::new).collect(Collectors.toList());
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar los usuarios");
        }
    }
    public String logUser(String email, String rawPassword) {
        try {
            // Autenticación directa con LDAP
            boolean authenticated = ldapTemplate.authenticate(
                    "ou=people", "(uid=" + email + ")", rawPassword);

            if (!authenticated) {
               return null;
            }

            // Buscar el usuario para obtener rol y generar token
            LdapUser user = userRepository.findByEmail(email);
            if (user == null) {
                throw new HttpException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }

            String roleDn = user.getRole();
            String simpleRole = roleDn != null && roleDn.contains("ROLE_ADMIN") ? "ROLE_ADMIN" : "ROLE_USER";
            List<String> roles = Collections.singletonList(simpleRole);

            return jwtUtil.generateToken(user.getEmail(), roles);
        } catch (Exception e) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al iniciar sesión: " + e.getMessage());
        }
    }
}

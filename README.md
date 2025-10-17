# README

## Instalación

1. Clona el repositorio:
```
git clone https://github.com/your-username/your-project.git
```

2. Navega al directorio del proyecto:
```
cd your-project
```
3. Compila el proyecto usando Maven:
```
mvn clean install
```
4. Ejecuta la aplicación:
```
  mvn spring-boot:run
 ```

## Uso
La aplicación proporciona los siguientes endpoints de la API:

### Autenticación

- `POST /auth/login:` Autenticar a un usuario y generar un token JWT.
- `GET /auth/recover: `Solicitar un código de recuperación de contraseña.
- `POST /auth/recover:` Restablecer la contraseña de un usuario usando el código de recuperación.

### API de administrador

- `GET /api/admin/users`: Obtener la lista de todos los usuarios.
- `GET /api/admin/users/{email}`: Obtener los detalles de un usuario específico.
- `POST /api/admin/users`: Crear un nuevo usuario.
- `PUT /api/admin/users/{email}`: Actualizar un usuario existente.
- `DELETE /api/admin/users/{email}`: Eliminar un usuario.

## API

### Autenticación

#### Inicio de sesión

- **Endpoint**: `POST /auth/login`
- **Cuerpo de la petición**:

```json
{
  "email": "user@example.com",
  "password": "password"
}
```

- **Respuesta**:

- Éxito: `200 OK` con un token JWT
- Error: `401 Unauthorized` con un mensaje de error

#### Recuperación de contraseña

- **Endpoint**: `GET /auth/recover`
- **Cuerpo de la petición**:

```json
{
  "email": "user@example.com"
}
```
- **Respuesta**:

- Éxito: `201 Created` con un mensaje indicando que el código de recuperación ha sido enviado
- Error: `4xx` con un mensaje de error

- **Endpoint**: `POST /auth/recover`
- **Cuerpo de la petición**:

```json
{
  "email": "user@example.com",
  "code": "abcd1234",
  "password": "newpassword"
}
```
- **Respuesta**:

- Éxito: `202 Accepted` con un mensaje indicando que la contraseña ha sido cambiada
- Error: `4xx` con un mensaje de error

### API de administrador

#### Obtener todos los usuarios

- **Endpoint**: `GET /api/admin/users`

- **Respuesta**:

- Éxito: `200 OK` con una lista de objetos UserDTO
- Error: `4xx` con un mensaje de error
  
#### Obtener detalles de un usuario

- **Endpoint**: `GET /api/admin/users/{email}`

- **Respuesta**:

- Éxito: `200 OK` con un objeto UserDTO
- Error: `4xx` con un mensaje de error

#### Crear usuario

- **Endpoint**: `POST /api/admin/users`
- **Cuerpo de la petición**:

```json
{
  "name": "John Doe",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password",
  "role": "ROLE_USER"
}
```

- **Respuesta**:

- Éxito: `201 Created` con un mensaje indicando que el usuario fue creado
- Error: `500` con un mensaje de error

#### Actualizar usuario

- **Endpoint**: `PUT /api/admin/users/{email}`
- **Cuerpo de la petición**:

```json
{
  "name": "John Doe",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "newpassword",
  "role": "ROLE_ADMIN"
}
```

- **Respuesta**:

- Éxito: `200 OK` con un mensaje indicando que el usuario fue actualizado
- **Error**: `4xx` con un mensaje de error
  
#### Eliminar usuario

- **Endpoint**: `DELETE /api/admin/users/{email}`

- **Respuesta**:

- Éxito: `200 OK` con un mensaje indicando que el usuario fue eliminado
- Error: `4xx` con un mensaje de error

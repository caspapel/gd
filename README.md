# Gestor de Documentos

Aplicación Spring Boot para gestionar documentos: subir, listar y descargar. Incluye login con Google OAuth2.

## Requisitos

- Java 21
- Maven (o usar Maven Wrapper)

## Ejecutar

```bash
./mvnw.cmd spring-boot:run
```

## Login con Google

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un proyecto o selecciona uno existente
3. **APIs y servicios** → **Credenciales** → **Crear credenciales** → **ID de cliente de OAuth 2.0**
4. Tipo: **Aplicación web**
5. En **URIs de redirección autorizados** añade: `http://localhost:8080/login/oauth2/code/google`
6. Copia el Client ID y Client Secret
7. Crea `src/main/resources/application-oauth.properties` con:
   ```
   spring.security.oauth2.client.registration.google.client-id=TU_CLIENT_ID
   spring.security.oauth2.client.registration.google.client-secret=TU_CLIENT_SECRET
   ```
   O usa variables de entorno: `GOOGLE_CLIENT_ID` y `GOOGLE_CLIENT_SECRET`

## Rutas

- `/` - Inicio (público)
- `/login` - Iniciar sesión con Google
- `/addDocument` - Subir documento (requiere login)
- `/downloadDocument` - Listar y descargar documentos (requiere login)
- `/hola` - API de prueba (público)

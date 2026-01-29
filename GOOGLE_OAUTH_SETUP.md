# Configuración de Google OAuth 2.0

## 📋 Pasos para configurar Google Sign-In

### 1. Crear proyecto en Google Cloud Console

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. En el menú lateral, ve a **APIs & Services** > **Credentials**

### 2. Configurar pantalla de consentimiento OAuth

1. Click en **OAuth consent screen**
2. Selecciona **External** (para usuarios fuera de tu organización)
3. Completa:
   - **App name**: LobbySync
   - **User support email**: tu-email@gmail.com
   - **Developer contact information**: tu-email@gmail.com
4. Click **Save and Continue**
5. En **Scopes**, no agregues nada adicional (los básicos están bien)
6. Click **Save and Continue**
7. En **Test users**, agrega tu email de prueba
8. Click **Save and Continue**

### 3. Crear credenciales OAuth 2.0

1. Ve a **Credentials** > **Create Credentials** > **OAuth 2.0 Client ID**
2. Tipo de aplicación: **Web application**
3. Nombre: `LobbySync Web Client`
4. **Authorized JavaScript origins**:
   ```
   http://localhost:3000
   http://localhost:5173
   https://tu-dominio.com
   ```
5. **Authorized redirect URIs**:
   ```
   http://localhost:3000
   http://localhost:5173
   https://tu-dominio.com
   ```
6. Click **Create**
7. **¡IMPORTANTE!** Copia el **Client ID** que aparece (formato: `xxxxx.apps.googleusercontent.com`)

### 4. Configurar el Backend

Edita el archivo `application.properties` o usa variables de entorno:

```properties
# application.properties
google.oauth.client-id=TU-CLIENT-ID.apps.googleusercontent.com
```

O en el VPS con variable de entorno:

```bash
export GOOGLE_OAUTH_CLIENT_ID=TU-CLIENT-ID.apps.googleusercontent.com
```

O en docker-compose.yml:

```yaml
services:
  backend:
    environment:
      - GOOGLE_OAUTH_CLIENT_ID=TU-CLIENT-ID.apps.googleusercontent.com
```

### 5. Integración en el Frontend

#### React con @react-oauth/google

```bash
npm install @react-oauth/google
```

```jsx
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';

function App() {
  return (
    <GoogleOAuthProvider clientId="TU-CLIENT-ID.apps.googleusercontent.com">
      <GoogleLogin
        onSuccess={(credentialResponse) => {
          // Enviar el token al backend
          fetch('http://168.197.50.14:8080/api/auth/google', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ idToken: credentialResponse.credential })
          })
          .then(res => res.json())
          .then(data => {
            // Guardar el JWT token
            localStorage.setItem('token', data.token);
            // Redirigir a dashboard
          });
        }}
        onError={() => console.log('Login Failed')}
      />
    </GoogleOAuthProvider>
  );
}
```

#### Angular

```bash
npm install @abacritt/angularx-social-login
```

```typescript
import { GoogleLoginProvider, SocialAuthService } from '@abacritt/angularx-social-login';

constructor(private authService: SocialAuthService, private http: HttpClient) {
  this.authService.authState.subscribe((user) => {
    const idToken = user.idToken;
    
    // Enviar al backend
    this.http.post('http://168.197.50.14:8080/api/auth/google', 
      { idToken: idToken }
    ).subscribe(response => {
      localStorage.setItem('token', response.token);
    });
  });
}

signInWithGoogle(): void {
  this.authService.signIn(GoogleLoginProvider.PROVIDER_ID);
}
```

#### Vue 3

```bash
npm install vue3-google-login
```

```vue
<template>
  <GoogleLogin :callback="handleLogin" />
</template>

<script setup>
import { GoogleLogin } from 'vue3-google-login';

const handleLogin = async (response) => {
  const result = await fetch('http://168.197.50.14:8080/api/auth/google', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ idToken: response.credential })
  });
  
  const data = await result.json();
  localStorage.setItem('token', data.token);
};
</script>
```

### 6. Probar la integración

1. Usuario hace clic en "Sign in with Google"
2. Selecciona su cuenta Google
3. Frontend recibe el `idToken`
4. Frontend envía `POST /api/auth/google` con `{ "idToken": "..." }`
5. Backend valida con Google, crea/actualiza usuario en PostgreSQL
6. Backend retorna JWT token válido por 24 horas
7. Frontend guarda el token y lo usa en todas las requests:
   ```
   Authorization: Bearer <jwt-token>
   ```

## 🔒 Seguridad

- El Google ID Token se valida contra los servidores de Google
- Solo tokens válidos y no expirados son aceptados
- El Client ID debe coincidir exactamente
- Los usuarios se crean automáticamente con rol `RESIDENT` por defecto
- Los administradores pueden cambiar roles después

## 📝 Notas

- El Google ID Token expira en 1 hora
- El JWT del backend expira en 24 horas
- No necesitas Firebase Auth para este flujo
- Firestore puede usarse para caché/sincronización si lo deseas

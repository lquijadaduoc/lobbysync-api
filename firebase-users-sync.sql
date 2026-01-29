-- Script para crear usuarios en Firebase y PostgreSQL
-- Los usuarios ya están en PostgreSQL, este script es para verificar/crear en Firebase

-- IMPORTANTE: Los usuarios deben crearse en Firebase primero manualmente o vía API
-- Este archivo documenta los usuarios que deben existir en Firebase

/*
USUARIOS PARA CREAR EN FIREBASE (si no existen):

ADMINISTRADORES:
1. Email: admin@lobbysync.cl
   Password: Admin123! (cambiar en producción)
   UID esperado: firebase_admin_1
   
2. Email: admin2@lobbysync.cl
   Password: Admin123! (cambiar en producción)
   UID esperado: firebase_admin_2

CONSERJES:
3. Email: conserje1@lobbysync.cl
   Password: Conserje123!
   UID esperado: firebase_conserje_1
   
4. Email: conserje2@lobbysync.cl
   Password: Conserje123!
   UID esperado: firebase_conserje_2

RESIDENTES:
5. Email: residente1@mail.com
   Password: Residente123!
   UID esperado: firebase_res_1
   
6. Email: residente2@mail.com
   Password: Residente123!
   UID esperado: firebase_res_2
   
7. Email: residente3@mail.com
   Password: Residente123!
   UID esperado: firebase_res_3
   
8. Email: residente4@mail.com
   Password: Residente123!
   UID esperado: firebase_res_4
   
9. Email: residente5@mail.com
   Password: Residente123!
   UID esperado: firebase_res_5

PARA CREAR ESTOS USUARIOS EN FIREBASE:
1. Usar la consola de Firebase: https://console.firebase.google.com
2. O usar el endpoint POST /api/v1/firebase/users con:
   {
     "email": "email@ejemplo.com",
     "password": "Password123!",
     "displayName": "Nombre Apellido",
     "role": "ADMIN|CONCIERGE|RESIDENT"
   }
*/

-- Verificar que los usuarios existen en PostgreSQL
SELECT 
    id,
    email,
    firebase_uid,
    first_name || ' ' || last_name as nombre_completo,
    role,
    is_active
FROM users
ORDER BY 
    CASE 
        WHEN role = 'ADMIN' THEN 1
        WHEN role = 'CONCIERGE' THEN 2
        WHEN role = 'RESIDENT' THEN 3
        ELSE 4
    END,
    id;

# Script para sincronizar usuarios con Firebase
# Ejecutar: .\sync-firebase-users.ps1

$VPS_IP = "168.197.50.14"
$API_BASE = "http://${VPS_IP}:8080/api/v1"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Sincronizacion de Usuarios con Firebase" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Lista de usuarios a crear
$users = @(
    @{
        email = "admin@lobbysync.cl"
        password = "Admin123!"
        displayName = "Admin Principal"
        role = "ADMIN"
    },
    @{
        email = "admin2@lobbysync.cl"
        password = "Admin123!"
        displayName = "Admin Secundario"
        role = "ADMIN"
    },
    @{
        email = "conserje1@lobbysync.cl"
        password = "Conserje123!"
        displayName = "Juan Perez"
        role = "CONCIERGE"
    },
    @{
        email = "conserje2@lobbysync.cl"
        password = "Conserje123!"
        displayName = "Maria Gonzalez"
        role = "CONCIERGE"
    },
    @{
        email = "residente1@mail.com"
        password = "Residente123!"
        displayName = "Carlos Rodriguez"
        role = "RESIDENT"
    },
    @{
        email = "residente2@mail.com"
        password = "Residente123!"
        displayName = "Ana Martinez"
        role = "RESIDENT"
    },
    @{
        email = "residente3@mail.com"
        password = "Residente123!"
        displayName = "Pedro Lopez"
        role = "RESIDENT"
    },
    @{
        email = "residente4@mail.com"
        password = "Residente123!"
        displayName = "Laura Silva"
        role = "RESIDENT"
    },
    @{
        email = "residente5@mail.com"
        password = "Residente123!"
        displayName = "Diego Munoz"
        role = "RESIDENT"
    }
)

$created = 0
$errors = 0
$skipped = 0

foreach ($user in $users) {
    Write-Host "Procesando: $($user.email) ($($user.role))..." -ForegroundColor Yellow
    
    $body = @{
        email = $user.email
        password = $user.password
        displayName = $user.displayName
        role = $user.role
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "$API_BASE/firebase/users" -Method Post -Body $body -ContentType "application/json" -ErrorAction Stop
        Write-Host "  OK Usuario creado - UID: $($response.firebaseUid)" -ForegroundColor Green
        $created++
    }
    catch {
        $errorMessage = $_.Exception.Message
        if ($errorMessage -like "*409*" -or $errorMessage -like "*already exists*") {
            Write-Host "  INFO Usuario ya existe en Firebase" -ForegroundColor Gray
            $skipped++
        }
        else {
            Write-Host "  ERROR: $errorMessage" -ForegroundColor Red
            $errors++
        }
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Resumen de Sincronizacion" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Creados: $created" -ForegroundColor Green
Write-Host "Ya existian: $skipped" -ForegroundColor Gray
Write-Host "Errores: $errors" -ForegroundColor Red
Write-Host ""

Write-Host "Verificando usuarios en PostgreSQL..." -ForegroundColor Yellow
ssh root@168.197.50.14 "docker exec postgres_db psql -U postgres -d lobbysync -c 'SELECT id, email, role, is_active FROM users ORDER BY role, id;'"

Write-Host ""
Write-Host "Sincronizacion completada" -ForegroundColor Green

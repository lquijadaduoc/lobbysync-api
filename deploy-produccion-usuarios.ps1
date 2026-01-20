# ========================================
# SCRIPT DE DEPLOYMENT A PRODUCCION
# LobbySync Backend - Gestion de Usuarios
# ========================================

Write-Host ""
Write-Host "=================================" -ForegroundColor Cyan
Write-Host " DEPLOYMENT A PRODUCCION" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# Configuracion
$VPS_IP = "168.197.50.14"
$VPS_USER = "root"
$VPS_PASSWORD = "SebaErica12.18"
$PROJECT_PATH = "C:\Users\Sebastian\Desktop\Examen Final\lobbysync-api"
$JAR_NAME = "backend-1.0.0.jar"

# Paso 1: Compilar el proyecto
Write-Host "[1/6] Compilando proyecto Maven..." -ForegroundColor Yellow
Set-Location $PROJECT_PATH

# Buscar Maven
$MVN = "C:\Program Files\NetBeans-23\netbeans\java\maven\bin\mvn.cmd"
if (-Not (Test-Path $MVN)) {
    $MVN = "mvn"
}

# Limpiar y compilar
& $MVN clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: Fallo la compilacion de Maven" -ForegroundColor Red
    Write-Host ""
    exit 1
}

Write-Host "OK - Compilacion exitosa" -ForegroundColor Green

# Verificar que el JAR existe
$JAR_PATH = "$PROJECT_PATH\target\$JAR_NAME"
if (-Not (Test-Path $JAR_PATH)) {
    Write-Host ""
    Write-Host "ERROR: No se encontro el JAR en: $JAR_PATH" -ForegroundColor Red
    Write-Host ""
    exit 1
}

$jarSize = (Get-Item $JAR_PATH).Length / 1MB
Write-Host "   JAR generado: $jarSize MB" -ForegroundColor Gray

# Paso 2: Subir JAR al VPS usando SCP (con plink)
Write-Host ""
Write-Host "[2/6] Subiendo JAR al VPS..." -ForegroundColor Yellow

# Usar pscp de PuTTY (si esta disponible)
if (Get-Command pscp -ErrorAction SilentlyContinue) {
    echo y | pscp -pw $VPS_PASSWORD $JAR_PATH "$VPS_USER@$VPS_IP`:/root/lobbysync-api/target/$JAR_NAME"
} else {
    Write-Host "ADVERTENCIA: pscp no encontrado. Usando metodo alternativo..." -ForegroundColor Yellow
    
    # Metodo alternativo: usar plink para copiar
    $command = "mkdir -p /root/lobbysync-api/target; echo 'Using fallback method'; ls -la /root/lobbysync-api/target/"
    echo y | plink -pw $VPS_PASSWORD "$VPS_USER@$VPS_IP" $command
    Write-Host "   ADVERTENCIA: Necesitas pscp (PuTTY) para subir el JAR" -ForegroundColor Yellow
    Write-Host "   Descargalo de: https://www.chiark.greenend.org.uk/~sgtatham/putty/latest.html" -ForegroundColor Gray
}

Write-Host "OK - JAR subido al VPS" -ForegroundColor Green

# Paso 3: Copiar serviceAccountKey.json
Write-Host ""
Write-Host "[3/6] Verificando archivo Firebase..." -ForegroundColor Yellow

$firebaseKeyPath = "$PROJECT_PATH\serviceAccountKey.json"
if (Test-Path $firebaseKeyPath) {
    Write-Host "   Subiendo serviceAccountKey.json..." -ForegroundColor Gray
    if (Get-Command pscp -ErrorAction SilentlyContinue) {
        echo y | pscp -pw $VPS_PASSWORD $firebaseKeyPath "$VPS_USER@$VPS_IP`:/root/lobbysync-api/serviceAccountKey.json"
    }
    Write-Host "OK - Firebase key sincronizada" -ForegroundColor Green
} else {
    Write-Host "ADVERTENCIA: serviceAccountKey.json no encontrado (puede que ya este en el servidor)" -ForegroundColor Yellow
}

# Paso 4: Detener contenedor actual
Write-Host ""
Write-Host "[4/6] Deteniendo contenedor actual..." -ForegroundColor Yellow

$stopCommand = "cd /root/lobbysync-api; docker-compose down; echo 'Container stopped'"

echo y | plink -pw $VPS_PASSWORD -batch "$VPS_USER@$VPS_IP" $stopCommand

Write-Host "OK - Contenedor detenido" -ForegroundColor Green

# Paso 5: Iniciar nuevo contenedor
Write-Host ""
Write-Host "[5/6] Iniciando nuevo contenedor..." -ForegroundColor Yellow

$startCommand = "cd /root/lobbysync-api; docker-compose up -d; echo 'Waiting for startup...'; sleep 15; docker ps"

echo y | plink -pw $VPS_PASSWORD -batch "$VPS_USER@$VPS_IP" $startCommand

Write-Host "OK - Contenedor iniciado" -ForegroundColor Green

# Paso 6: Verificar logs
Write-Host ""
Write-Host "[6/6] Verificando logs del backend..." -ForegroundColor Yellow

$logsCommand = "docker logs lobbysync_backend --tail 30"
echo y | plink -pw $VPS_PASSWORD -batch "$VPS_USER@$VPS_IP" $logsCommand

Write-Host ""
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "DEPLOYMENT COMPLETADO" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Backend disponible en:" -ForegroundColor White
Write-Host "   http://168.197.50.14:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "Swagger UI:" -ForegroundColor White
Write-Host "   http://168.197.50.14:8080/swagger-ui.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "Probar endpoints:" -ForegroundColor White
Write-Host "   GET  http://168.197.50.14:8080/api/v1/users" -ForegroundColor Gray
Write-Host "   POST http://168.197.50.14:8080/api/v1/users" -ForegroundColor Gray
Write-Host "   PUT  http://168.197.50.14:8080/api/v1/users/{id}" -ForegroundColor Gray
Write-Host "   DELETE http://168.197.50.14:8080/api/v1/users/{id}" -ForegroundColor Gray
Write-Host ""
Write-Host "Ver logs en tiempo real:" -ForegroundColor White
Write-Host "   ssh root@168.197.50.14" -ForegroundColor Gray
Write-Host "   docker logs -f lobbysync_backend" -ForegroundColor Gray
Write-Host ""
Write-Host "Si hay problemas, revisar:" -ForegroundColor Yellow
Write-Host "   docker logs lobbysync_backend --tail 100" -ForegroundColor Gray
Write-Host ""

# Preguntar si quiere ver logs en tiempo real
$answer = Read-Host "Ver logs en tiempo real? (s/n)"
if ($answer -eq "s" -or $answer -eq "S") {
    Write-Host ""
    Write-Host "Mostrando logs en tiempo real (Ctrl+C para salir)..." -ForegroundColor Yellow
    Write-Host ""
    
    $liveLogsCommand = "docker logs -f lobbysync_backend"
    echo y | plink -pw $VPS_PASSWORD -batch "$VPS_USER@$VPS_IP" $liveLogsCommand
}

Write-Host ""
Write-Host "Deployment completado exitosamente" -ForegroundColor Green
Write-Host ""

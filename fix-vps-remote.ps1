# Script para recuperar el VPS desde Windows
# Ejecutar: .\fix-vps-remote.ps1

$VPS_IP = "168.197.50.14"
$VPS_USER = "root"

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Recuperación del VPS LobbySync" -ForegroundColor Cyan
Write-Host "VPS: $VPS_USER@$VPS_IP" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# 1. Copiar archivos necesarios al VPS
Write-Host "1. Copiando archivos al VPS..." -ForegroundColor Yellow
scp fix-vps.sh "${VPS_USER}@${VPS_IP}:/root/lobbysync-api/"
scp seed-actual.sql "${VPS_USER}@${VPS_IP}:/root/lobbysync-api/"
scp docker-compose.yml "${VPS_USER}@${VPS_IP}:/root/lobbysync-api/"
scp Dockerfile "${VPS_USER}@${VPS_IP}:/root/lobbysync-api/"
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al copiar archivos" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Archivos copiados" -ForegroundColor Green
Write-Host ""

# 2. Dar permisos de ejecución
Write-Host "2. Configurando permisos..." -ForegroundColor Yellow
ssh "${VPS_USER}@${VPS_IP}" "chmod +x /root/lobbysync-api/fix-vps.sh"
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error al configurar permisos" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Permisos configurados" -ForegroundColor Green
Write-Host ""

# 3. Ejecutar el script en el VPS
Write-Host "3. Ejecutando script de recuperación en el VPS..." -ForegroundColor Yellow
Write-Host "Esto puede tomar varios minutos..." -ForegroundColor Yellow
Write-Host ""

ssh "${VPS_USER}@${VPS_IP}" "cd /root/lobbysync-api && bash fix-vps.sh"

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Proceso completado" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Verificación rápida desde el navegador:" -ForegroundColor Yellow
Write-Host "  Health: http://168.197.50.14:8080/actuator/health" -ForegroundColor White
Write-Host "  Swagger: http://168.197.50.14:8080/swagger-ui.html" -ForegroundColor White
Write-Host ""

# 4. Hacer un test rápido
Write-Host "Haciendo test de conectividad..." -ForegroundColor Yellow
$response = try { Invoke-WebRequest -Uri "http://168.197.50.14:8080/actuator/health" -TimeoutSec 5 -UseBasicParsing } catch { $null }

if ($response -and $response.StatusCode -eq 200) {
    Write-Host " API respondiendo correctamente" -ForegroundColor Green
    Write-Host ""
    Write-Host "Respuesta de health:" -ForegroundColor Cyan
    Write-Host $response.Content -ForegroundColor White
} else {
    Write-Host " API no responde aún. Puede necesitar más tiempo." -ForegroundColor Yellow
    Write-Host "Ejecuta para ver logs:" -ForegroundColor Yellow
    Write-Host '  ssh root@168.197.50.14 "docker logs -f lobbysync_backend"' -ForegroundColor White
}

Write-Host ""

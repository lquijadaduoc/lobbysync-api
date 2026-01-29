# Script para recuperar el VPS desde Windows
# Ejecutar: .\fix-vps-remote.ps1

$VPS_IP = "168.197.50.14"
$VPS_USER = "root"

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "RecuperaciÃ³n del VPS LobbySync" -ForegroundColor Cyan
Write-Host "VPS: $VPS_USER@$VPS_IP" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# 1. Copiar el script al VPS
Write-Host "1. Copiando script de recuperaciÃ³n al VPS..." -ForegroundColor Yellow
scp fix-vps.sh "${VPS_USER}@${VPS_IP}:/root/"
if ($LASTEXITCODE -ne 0) {
    Write-Host "âŒ Error al copiar el script" -ForegroundColor Red
    exit 1
}
Write-Host "âœ“ Script copiado" -ForegroundColor Green
Write-Host ""

# 2. Dar permisos de ejecuciÃ³n
Write-Host "2. Configurando permisos..." -ForegroundColor Yellow
ssh "${VPS_USER}@${VPS_IP}" "chmod +x /root/fix-vps.sh"
if ($LASTEXITCODE -ne 0) {
    Write-Host "âŒ Error al configurar permisos" -ForegroundColor Red
    exit 1
}
Write-Host "âœ“ Permisos configurados" -ForegroundColor Green
Write-Host ""

# 3. Ejecutar el script en el VPS
Write-Host "3. Ejecutando script de recuperaciÃ³n en el VPS..." -ForegroundColor Yellow
Write-Host "Esto puede tomar varios minutos..." -ForegroundColor Yellow
Write-Host ""

ssh "${VPS_USER}@${VPS_IP}" "bash /root/fix-vps.sh"

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Proceso completado" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "VerificaciÃ³n rÃ¡pida desde el navegador:" -ForegroundColor Yellow
Write-Host "  Health: http://168.197.50.14:8080/actuator/health" -ForegroundColor White
Write-Host "  Swagger: http://168.197.50.14:8080/swagger-ui.html" -ForegroundColor White
Write-Host ""

# 4. Hacer un test rÃ¡pido
Write-Host "Haciendo test de conectividad..." -ForegroundColor Yellow
$response = try { Invoke-WebRequest -Uri "http://168.197.50.14:8080/actuator/health" -TimeoutSec 5 -UseBasicParsing } catch { $null }

if ($response -and $response.StatusCode -eq 200) {
    Write-Host "âœ“ API respondiendo correctamente" -ForegroundColor Green
    Write-Host ""
    Write-Host "Respuesta de health:" -ForegroundColor Cyan
    Write-Host $response.Content -ForegroundColor White
} else {
    Write-Host "âš  API no responde aÃºn. Puede necesitar mÃ¡s tiempo." -ForegroundColor Yellow
    Write-Host "Ejecuta para ver logs:" -ForegroundColor Yellow
    Write-Host "  ssh root@168.197.50.14 'docker logs -f lobbysync_backend'" -ForegroundColor White
}

Write-Host ""


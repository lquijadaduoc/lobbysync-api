@echo off
REM Script para desplegar LobbySync API en VPS
REM Este script usa SSH para conectarse al VPS

echo.
echo ============================================
echo  LobbySync API - VPS Deployment
echo ============================================
echo.
echo Host: 168.197.50.14
echo User: root
echo.
echo Ejecutando comando de deployment...
echo.

REM Este comando se ejecutará en el VPS remotamente
ssh -o StrictHostKeyChecking=no root@168.197.50.14 ^
  "cd /root && ^
   if [ ! -d lobbysync-api ]; then git clone https://github.com/lquijadaduoc/lobbysync-api.git; fi && ^
   cd lobbysync-api && ^
   git pull origin main && ^
   docker-compose down -v 2>/dev/null || true && ^
   docker-compose up -d && ^
   echo '' && ^
   echo 'Esperando inicialización...' && ^
   sleep 30 && ^
   echo '' && ^
   docker ps && ^
   echo '' && ^
   echo 'API disponible en: http://168.197.50.14:8080'"

echo.
echo ============================================
echo Deployment completado
echo ============================================
pause

#!/bin/bash

# Script de reinicio rápido
# Ejecutar en el VPS: bash quick-restart.sh

echo "========================================"
echo "Reinicio Rápido de LobbySync"
echo "========================================"
echo ""

cd /root/lobbysync-api

echo "1. Deteniendo servicios..."
docker-compose down
echo "✓ Servicios detenidos"
echo ""

echo "2. Iniciando servicios..."
docker-compose up -d
echo "✓ Servicios iniciados"
echo ""

echo "3. Esperando inicialización (30s)..."
sleep 30
echo ""

echo "Estado:"
docker-compose ps
echo ""

echo "Logs del backend (últimas 20 líneas):"
docker logs lobbysync_backend --tail 20
echo ""

echo "✅ Reinicio completado"
echo ""
echo "Verificar en: http://168.197.50.14:8080/actuator/health"

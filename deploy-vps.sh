#!/bin/bash

# Script de despliegue en VPS
# Uso: ./deploy-vps.sh <ip_vps> <usuario>

set -e

VPS_IP="${1:-168.197.50.14}"
VPS_USER="${2:-root}"
VPS_PATH="/opt/lobbysync-api"

echo "========================================"
echo "LobbySyncAPI - Despliegue en VPS"
echo "========================================"
echo "VPS: $VPS_USER@$VPS_IP"
echo "Path: $VPS_PATH"
echo "========================================"

# Verificar que serviceAccountKey.json existe
if [ ! -f "serviceAccountKey.json" ]; then
    echo "❌ Error: serviceAccountKey.json no encontrado en el directorio actual"
    echo "Necesitas colocar el archivo serviceAccountKey.json antes de desplegar"
    exit 1
fi

echo "📦 Sincronizando código con VPS..."
rsync -avz --delete \
    --exclude='.git' \
    --exclude='target' \
    --exclude='node_modules' \
    --exclude='.docker' \
    --exclude='*.log' \
    ./ "$VPS_USER@$VPS_IP:$VPS_PATH/" 2>/dev/null || true

echo "🚀 Ejecutando despliegue en VPS..."

ssh "$VPS_USER@$VPS_IP" << 'DEPLOY_SCRIPT'
set -e

cd /opt/lobbysync-api

echo "📝 Actualizando repositorio..."
git pull origin main || git init && git remote add origin https://github.com/lquijadaduoc/lobbysync-api.git && git fetch origin main && git reset --hard origin/main

echo "🛑 Deteniendo servicios antiguos..."
docker-compose down -v 2>/dev/null || true

echo "🗑️  Limpiando imágenes antiguas..."
docker image rm lobbysync-api-backend 2>/dev/null || true

echo "🔨 Construyendo y levantando nuevos servicios..."
docker-compose up -d

echo "⏳ Esperando que los servicios inicien..."
sleep 50

echo "✅ Verificando salud de servicios..."
curl -s http://localhost:8080/actuator/health | jq . || curl -s http://localhost:8080/actuator/health

echo "✨ Despliegue completado exitosamente"
echo ""
echo "URLs disponibles:"
echo "  - API: http://168.197.50.14:8080"
echo "  - Swagger: http://168.197.50.14:8080/swagger-ui.html"
echo "  - Health: http://168.197.50.14:8080/actuator/health"

DEPLOY_SCRIPT

echo ""
echo "✅ Despliegue finalizado"

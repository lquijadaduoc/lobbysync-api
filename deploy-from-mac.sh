#!/bin/bash

# Script para ejecutar deployment en VPS desde Mac
# Uso: bash deploy-from-mac.sh

set -e

VPS_HOST="168.197.50.14"
VPS_USER="root"
VPS_PATH="/root/lobbysync-api"

echo "========================================"
echo "LobbySync - Deployment desde Mac a VPS"
echo "========================================"
echo ""

# 1. Subir cambios locales a GitHub
echo "1. Verificando cambios locales..."
if [[ -n $(git status -s) ]]; then
    echo "⚠️  Hay cambios sin commitear. Por favor commit primero."
    exit 1
fi

echo "2. Haciendo push a GitHub..."
git push origin main || echo "✓ Ya está actualizado en GitHub"
echo ""

# 3. Conectar al VPS y ejecutar fix-vps.sh
echo "3. Conectando al VPS y ejecutando deployment..."
echo ""

ssh ${VPS_USER}@${VPS_HOST} << 'ENDSSH'
cd /root/lobbysync-api

echo "========================================"
echo "LobbySync - Script de Recuperación VPS"
echo "========================================"
echo ""

# 1. Actualizar código desde GitHub
echo "1. Actualizando código desde GitHub..."
git pull origin main
echo "✓ Código actualizado"
echo ""

# 2. Detener todo y limpiar
echo "2. Deteniendo contenedores..."
docker-compose down
echo "✓ Contenedores detenidos"
echo ""

# 3. Asegurar que los puertos estén libres
echo "3. Verificando puertos..."
if lsof -i:8080 >/dev/null 2>&1; then
    echo "⚠ Puerto 8080 ocupado, liberando..."
    fuser -k 8080/tcp || true
fi
echo "✓ Puertos verificados"
echo ""

# 4. Iniciar solo las bases de datos primero
echo "4. Iniciando bases de datos..."
docker-compose up -d postgres_db mongo_db
echo "✓ Contenedores de BD iniciados"
echo ""

# 5. Esperar a que PostgreSQL esté listo
echo "5. Esperando PostgreSQL (máx 60s)..."
for i in {1..60}; do
    if docker exec postgres_db pg_isready -U postgres -d lobbysync >/dev/null 2>&1; then
        echo "✓ PostgreSQL listo en ${i}s"
        break
    fi
    echo -n "."
    sleep 1
    if [ $i -eq 60 ]; then
        echo ""
        echo "❌ ERROR: PostgreSQL no respondió en 60s"
        exit 1
    fi
done
echo ""

# 6. Verificar que la base de datos existe
echo "6. Verificando base de datos lobbysync..."
DB_EXISTS=$(docker exec postgres_db psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='lobbysync'")
if [ "$DB_EXISTS" != "1" ]; then
    echo "⚠ Base de datos no existe, creándola..."
    docker exec postgres_db psql -U postgres -c "CREATE DATABASE lobbysync;"
fi
echo "✓ Base de datos lobbysync verificada"
echo ""

# 6.5 Resetear contraseña de postgres para que coincida con docker-compose
echo "6.5 Configurando contraseña de PostgreSQL..."
docker exec postgres_db psql -U postgres -c "ALTER USER postgres WITH PASSWORD 'postgres';" >/dev/null 2>&1
sleep 2  # Esperar a que PostgreSQL procese el cambio
# Verificar conectividad con la nueva contraseña
docker exec postgres_db psql -U postgres -d lobbysync -c "SELECT 1;" >/dev/null 2>&1
echo "✓ Contraseña de PostgreSQL configurada"
echo ""

# 6.6 Cargar datos iniciales (seed)
echo "6.6 Cargando datos iniciales..."
if [ -f "seed-production.sql" ]; then
    docker exec -i postgres_db psql -U postgres -d lobbysync < seed-production.sql >/dev/null 2>&1
    echo "✓ Datos iniciales cargados desde seed-production.sql"
else
    echo "⚠ Archivo seed-production.sql no encontrado, omitiendo..."
fi
echo ""

# 7. Esperar a que MongoDB esté listo
echo "7. Esperando MongoDB (máx 30s)..."
for i in {1..30}; do
    if docker exec mongo_db mongosh --eval "db.adminCommand('ping')" >/dev/null 2>&1; then
        echo "✓ MongoDB listo en ${i}s"
        break
    fi
    echo -n "."
    sleep 1
done
echo ""

# 8. Iniciar el backend
echo "8. Iniciando backend..."
docker-compose up -d backend
echo "✓ Backend iniciado"
echo ""

# 9. Esperar a que el backend esté listo
echo "9. Esperando backend (máx 90s)..."
for i in {1..90}; do
    if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
        HEALTH=$(curl -s http://localhost:8080/actuator/health)
        echo "✓ Backend respondiendo en ${i}s"
        echo "Health: $HEALTH"
        break
    fi
    echo -n "."
    sleep 1
    if [ $i -eq 90 ]; then
        echo ""
        echo "⚠ Backend no respondió en 90s, verificar logs:"
        docker logs lobbysync_backend --tail 50
    fi
done
echo ""

# 10. Verificar estado final
echo "========================================"
echo "Estado de los Servicios:"
echo "========================================"
docker-compose ps
echo ""

echo "========================================"
echo "Verificación de Conectividad:"
echo "========================================"

# Test PostgreSQL
echo -n "PostgreSQL (5432): "
if docker exec postgres_db pg_isready -U postgres -d lobbysync >/dev/null 2>&1; then
    echo "✓ OK"
else
    echo "❌ ERROR"
fi

# Test MongoDB
echo -n "MongoDB (27017): "
if docker exec mongo_db mongosh --eval "db.adminCommand('ping')" >/dev/null 2>&1; then
    echo "✓ OK"
else
    echo "❌ ERROR"
fi

# Test API Health
echo -n "API Health: "
HEALTH=$(curl -s http://localhost:8080/actuator/health 2>/dev/null || echo "DOWN")
if echo "$HEALTH" | grep -q "UP"; then
    echo "✓ OK"
else
    echo "❌ ERROR - Health: $HEALTH"
fi

# Test API Endpoint
echo -n "API Endpoint (/api/v1/users): "
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/users 2>/dev/null)
if echo "$HTTP_CODE" | grep -q "200\|401\|403"; then
    echo "✓ OK (HTTP $HTTP_CODE)"
else
    echo "❌ ERROR (HTTP $HTTP_CODE)"
fi

echo ""
echo "========================================"
echo "Información del Sistema:"
echo "========================================"
echo "URL Base: http://168.197.50.14:8080"
echo "Swagger UI: http://168.197.50.14:8080/swagger-ui.html"
echo "API Docs: http://168.197.50.14:8080/v3/api-docs"
echo ""
echo "✅ DEPLOYMENT COMPLETADO"
echo "========================================"

ENDSSH

echo ""
echo "========================================"
echo "✅ Deployment completado exitosamente"
echo "========================================"
echo ""
echo "API disponible en: http://168.197.50.14:8080"
echo "Swagger UI: http://168.197.50.14:8080/swagger-ui.html"
echo ""

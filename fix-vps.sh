#!/bin/bash

# Script de recuperación para VPS
# Ejecutar en el VPS: bash fix-vps.sh

set -e

echo "========================================"
echo "LobbySync - Script de Recuperación VPS"
echo "========================================"
echo ""

cd /root/lobbysync-api

# 1. Detener todo y limpiar
echo "1. Deteniendo contenedores y limpiando volúmenes..."
docker-compose down -v
docker system prune -f
echo "✓ Limpieza completada"
echo ""

# 2. Asegurar que los puertos estén libres
echo "2. Verificando puertos..."
if lsof -i:8080 >/dev/null 2>&1; then
    echo "⚠ Puerto 8080 ocupado, liberando..."
    fuser -k 8080/tcp || true
fi
if lsof -i:5432 >/dev/null 2>&1; then
    echo "⚠ Puerto 5432 ocupado, liberando..."
    fuser -k 5432/tcp || true
fi
if lsof -i:27017 >/dev/null 2>&1; then
    echo "⚠ Puerto 27017 ocupado, liberando..."
    fuser -k 27017/tcp || true
fi
echo "✓ Puertos verificados"
echo ""

# 3. Crear directorios necesarios
echo "3. Creando directorios de datos..."
mkdir -p /root/lobbysync-data/{postgres,mongo}
chmod -R 755 /root/lobbysync-data
echo "✓ Directorios creados"
echo ""

# 4. Verificar que el archivo JAR existe
echo "4. Verificando JAR de backend..."
if [ ! -f "target/backend-1.0.0.jar" ]; then
    echo "⚠ JAR no encontrado, compilando..."
    mvn clean package -DskipTests
fi
echo "✓ JAR listo"
echo ""

# 5. Iniciar solo las bases de datos primero
echo "5. Iniciando bases de datos..."
docker-compose up -d postgres_db mongo_db
echo "✓ Contenedores de BD iniciados"
echo ""

# 6. Esperar a que PostgreSQL esté listo
echo "6. Esperando PostgreSQL (máx 60s)..."
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
        echo "Logs de PostgreSQL:"
        docker logs postgres_db --tail 50
        exit 1
    fi
done
echo ""

# 7. Verificar que la base de datos existe
echo "7. Verificando base de datos lobbysync..."
DB_EXISTS=$(docker exec postgres_db psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='lobbysync'")
if [ "$DB_EXISTS" != "1" ]; then
    echo "⚠ Base de datos no existe, creándola..."
    docker exec postgres_db psql -U postgres -c "CREATE DATABASE lobbysync;"
fi
echo "✓ Base de datos lobbysync verificada"
echo ""

# 8. Esperar a que MongoDB esté listo
echo "8. Esperando MongoDB (máx 30s)..."
for i in {1..30}; do
    if docker exec mongo_db mongosh --eval "db.adminCommand('ping')" >/dev/null 2>&1; then
        echo "✓ MongoDB listo en ${i}s"
        break
    fi
    echo -n "."
    sleep 1
    if [ $i -eq 30 ]; then
        echo ""
        echo "⚠ MongoDB tardó más de lo esperado pero continuamos"
        break
    fi
done
echo ""

# 9. Iniciar el backend
echo "9. Iniciando backend..."
docker-compose up -d backend
echo "✓ Backend iniciado"
echo ""

# 10. Esperar a que el backend esté listo
echo "10. Esperando backend (máx 90s)..."
for i in {1..90}; do
    if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
        echo "✓ Backend respondiendo en ${i}s"
        break
    fi
    echo -n "."
    sleep 1
    if [ $i -eq 90 ]; then
        echo ""
        echo "⚠ Backend no respondió, verificar logs:"
        docker logs lobbysync_backend --tail 100
    fi
done
echo ""

# 11. Cargar datos de producción
echo "11. Cargando datos de producción..."
if [ -f "seed-actual.sql" ]; then
    cat seed-actual.sql | docker exec -i postgres_db psql -U postgres -d lobbysync
    echo "✓ Datos cargados"
else
    echo "⚠ Archivo seed-actual.sql no encontrado"
fi
echo ""

# 12. Verificar estado final
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
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/users 2>/dev/null | grep -q "200\|401\|403"; then
    echo "✓ OK"
else
    echo "❌ ERROR"
fi

echo ""
echo "========================================"
echo "Información del Sistema:"
echo "========================================"
echo "URL Base: http://168.197.50.14:8080"
echo "Swagger UI: http://168.197.50.14:8080/swagger-ui.html"
echo "API Docs: http://168.197.50.14:8080/v3/api-docs"
echo ""

echo "========================================"
echo "Comandos útiles:"
echo "========================================"
echo "Ver logs backend:   docker logs -f lobbysync_backend"
echo "Ver logs postgres:  docker logs -f postgres_db"
echo "Ver logs mongo:     docker logs -f mongo_db"
echo "Entrar a postgres:  docker exec -it postgres_db psql -U postgres -d lobbysync"
echo "Reiniciar todo:     docker-compose restart"
echo "Ver estado:         docker-compose ps"
echo ""

# 13. Mostrar últimas líneas de logs del backend
echo "========================================"
echo "Últimas líneas del log del backend:"
echo "========================================"
docker logs lobbysync_backend --tail 30

echo ""
echo "✅ Script de recuperación completado"
echo ""

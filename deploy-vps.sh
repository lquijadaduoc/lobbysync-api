#!/bin/bash

# Script de deployment para VPS
# Este script debe ser ejecutado en el VPS en /root

set -e

echo "================================"
echo "LobbySync API - VPS Deployment"
echo "================================"
echo ""

# Navegar a /root
cd /root

# Clonar o actualizar repositorio
if [ -d "lobbysync-api" ]; then
    echo "✓ Repositorio existe, actualizando..."
    cd lobbysync-api
    git pull origin main
else
    echo "✓ Clonando repositorio..."
    git clone https://github.com/lquijadaduoc/lobbysync-api.git
    cd lobbysync-api
fi

echo ""
echo "✓ Repositorio listo en: $(pwd)"
echo ""

# Detener contenedores existentes
echo "Deteniendo contenedores existentes..."
docker-compose down -v 2>/dev/null || true
echo "✓ Contenedores detenidos"
echo ""

# Crear volúmenes necesarios
mkdir -p /root/lobbysync-data/{postgres,mongo}
chmod -R 755 /root/lobbysync-data

# Iniciar contenedores
echo "Iniciando contenedores Docker..."
docker-compose up -d

echo ""
echo "Esperando a que los servicios inicien..."
sleep 30

# Verificar estado
echo ""
echo "================================"
echo "Estado de Contenedores:"
echo "================================"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "================================"
echo "Verificando conectividad..."
echo "================================"

# Test PostgreSQL
echo -n "PostgreSQL: "
if curl -s http://localhost:5432 >/dev/null 2>&1 || nc -z localhost 5432 2>/dev/null; then
    echo "✓ Conectado"
else
    echo "⚠ Verificar"
fi

# Test MongoDB
echo -n "MongoDB: "
if nc -z localhost 27017 2>/dev/null; then
    echo "✓ Conectado"
else
    echo "⚠ Verificar"
fi

# Test API
echo -n "API (8080): "
if curl -s http://localhost:8080/api/v1/users >/dev/null 2>&1; then
    echo "✓ Respondiendo"
else
    echo "⚠ Verificar"
fi

echo ""
echo "================================"
echo "✅ DEPLOYMENT COMPLETADO"
echo "================================"
echo ""
echo "API disponible en: http://168.197.50.14:8080"
echo "Swagger UI: http://168.197.50.14:8080/swagger-ui.html"
echo ""
echo "Comandos útiles:"
echo "  docker logs -f lobbysync_backend"
echo "  docker-compose ps"
echo "  docker-compose down"
echo ""

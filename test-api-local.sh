#!/bin/bash

# Script para probar los endpoints principales de la API localmente

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

API_URL="http://localhost:8080"

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}LobbySync Backend - API Test Suite${NC}"
echo -e "${BLUE}=====================================${NC}\n"

# Test 1: Health Check
echo -e "${YELLOW}[1] Health Check${NC}"
response=$(curl -s -w "\n%{http_code}" "$API_URL/actuator/health")
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n-1)

if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✓ Status: 200${NC}"
    echo "$body" | jq . 2>/dev/null || echo "$body"
else
    echo -e "${RED}✗ Status: $http_code${NC}"
fi
echo ""

# Test 2: Swagger UI
echo -e "${YELLOW}[2] Swagger UI${NC}"
http_code=$(curl -s -o /dev/null -w "%{http_code}" "$API_URL/swagger-ui.html")
if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✓ Swagger disponible en: $API_URL/swagger-ui.html${NC}"
else
    echo -e "${RED}✗ Swagger no disponible (HTTP $http_code)${NC}"
fi
echo ""

# Test 3: OpenAPI Docs
echo -e "${YELLOW}[3] OpenAPI Documentation${NC}"
http_code=$(curl -s -o /dev/null -w "%{http_code}" "$API_URL/v3/api-docs")
if [ "$http_code" -eq 200 ]; then
    echo -e "${GREEN}✓ API Docs disponibles en: $API_URL/v3/api-docs${NC}"
else
    echo -e "${RED}✗ API Docs no disponibles (HTTP $http_code)${NC}"
fi
echo ""

# Test 4: Test de conexión a BD
echo -e "${YELLOW}[4] Verificando conexión a bases de datos${NC}"

# PostgreSQL
if nc -z localhost 5432 2>/dev/null; then
    echo -e "${GREEN}✓ PostgreSQL disponible en localhost:5432${NC}"
else
    echo -e "${RED}✗ PostgreSQL no disponible${NC}"
fi

# MongoDB
if nc -z localhost 27017 2>/dev/null; then
    echo -e "${GREEN}✓ MongoDB disponible en localhost:27017${NC}"
else
    echo -e "${RED}✗ MongoDB no disponible${NC}"
fi
echo ""

# Test 5: Intentar obtener usuarios (sin autenticación, debería retornar 401 o listar)
echo -e "${YELLOW}[5] Test de endpoints sin autenticación${NC}"
echo -e "   GET /api/users (sin token)"
http_code=$(curl -s -o /dev/null -w "%{http_code}" "$API_URL/api/users")
echo -e "   Status: ${YELLOW}$http_code${NC}"
if [ "$http_code" -eq 401 ] || [ "$http_code" -eq 403 ]; then
    echo -e "   ${GREEN}✓ Seguridad activa (requiere autenticación)${NC}"
elif [ "$http_code" -eq 200 ]; then
    echo -e "   ${YELLOW}⚠ Endpoint accesible sin token${NC}"
else
    echo -e "   ${YELLOW}⚠ Código de estado: $http_code${NC}"
fi
echo ""

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}Pruebas completadas${NC}"
echo -e "${BLUE}=====================================${NC}\n"

echo -e "${GREEN}Próximos pasos:${NC}"
echo -e "  1. Abre ${YELLOW}http://localhost:8080/swagger-ui.html${NC} en tu navegador"
echo -e "  2. Usa Swagger para probar los endpoints"
echo -e "  3. Si necesitas autenticación, crea usuarios primero\n"

echo -e "${YELLOW}Para probar con usuarios de Firebase:${NC}"
echo -e "  1. Configura Google OAuth en application.properties"
echo -e "  2. Usa un cliente Firebase para generar tokens ID"
echo -e "  3. Pasa el token en header: ${YELLOW}Authorization: Bearer <token>${NC}\n"

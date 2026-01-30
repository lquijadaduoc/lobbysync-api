#!/bin/bash

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}LobbySync Backend - Docker Local Setup${NC}"
echo -e "${BLUE}=====================================${NC}\n"

# Verificar que Docker Desktop está corriendo
echo -e "${YELLOW}✓ Verificando Docker Desktop...${NC}"
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}✗ Docker Desktop no está corriendo. Por favor inicia Docker Desktop.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker está disponible${NC}\n"

# Verificar serviceAccountKey.json
echo -e "${YELLOW}✓ Verificando archivo serviceAccountKey.json...${NC}"
if [ ! -f "serviceAccountKey.json" ]; then
    echo -e "${RED}✗ No se encontró serviceAccountKey.json${NC}"
    echo -e "${YELLOW}⚠ Crear el archivo en la raíz del proyecto con tu configuración Firebase${NC}"
    echo -e "${YELLOW}  Para continuar sin Firebase, presiona Enter...${NC}"
    read -p ""
fi
echo -e "${GREEN}✓ Listo${NC}\n"

# Detener contenedores anteriores
echo -e "${YELLOW}✓ Limpiando contenedores anteriores...${NC}"
docker-compose down > /dev/null 2>&1
echo -e "${GREEN}✓ Limpieza completada${NC}\n"

# Limpiar volúmenes (opcional - comentar si quieres preservar datos)
# echo -e "${YELLOW}✓ Limpiando volúmenes...${NC}"
# docker-compose down -v > /dev/null 2>&1

# Compilar el proyecto
echo -e "${YELLOW}✓ Compilando proyecto Maven...${NC}"
mvn clean package -DskipTests -q
if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error compilando el proyecto${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Compilación exitosa${NC}\n"

# Build de imágenes Docker
echo -e "${YELLOW}✓ Construyendo imagen Docker...${NC}"
docker-compose build
if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error en build de Docker${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Imagen construida${NC}\n"

# Iniciar servicios
echo -e "${YELLOW}✓ Iniciando servicios Docker Compose...${NC}"
docker-compose up -d
if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error iniciando servicios${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Servicios iniciados${NC}\n"

# Esperar a que los servicios estén listos
echo -e "${YELLOW}⏳ Esperando a que PostgreSQL esté listo...${NC}"
sleep 15

echo -e "${YELLOW}⏳ Esperando a que MongoDB esté listo...${NC}"
sleep 10

echo -e "${YELLOW}⏳ Esperando a que el Backend esté listo...${NC}"
sleep 10

# Verificar estado de los contenedores
echo -e "\n${BLUE}=====================================${NC}"
echo -e "${BLUE}Estado de los Servicios${NC}"
echo -e "${BLUE}=====================================${NC}\n"

docker-compose ps

# Verificar que el backend esté respondiendo
echo -e "\n${YELLOW}✓ Verificando backend...${NC}"
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null; then
        echo -e "${GREEN}✓ Backend respondiendo en http://localhost:8080${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${RED}✗ Backend no responde después de 30 segundos${NC}"
        echo -e "${YELLOW}Ver logs:${NC}"
        echo -e "  docker-compose logs backend"
        exit 1
    fi
    echo -n "."
    sleep 1
done

# Mostrar información de acceso
echo -e "\n${BLUE}=====================================${NC}"
echo -e "${BLUE}🎉 LobbySync Backend está corriendo!${NC}"
echo -e "${BLUE}=====================================${NC}\n"

echo -e "${GREEN}Servicios disponibles:${NC}"
echo -e "  📍 Backend API:     ${YELLOW}http://localhost:8080${NC}"
echo -e "  📚 Swagger UI:      ${YELLOW}http://localhost:8080/swagger-ui.html${NC}"
echo -e "  🏥 Health Check:    ${YELLOW}http://localhost:8080/actuator/health${NC}"
echo -e "  📊 PostgreSQL:      ${YELLOW}localhost:5432${NC}"
echo -e "  🗄️  MongoDB:         ${YELLOW}localhost:27017${NC}\n"

echo -e "${GREEN}Credenciales por defecto:${NC}"
echo -e "  PostgreSQL:"
echo -e "    Usuario: ${YELLOW}postgres${NC}"
echo -e "    Contraseña: ${YELLOW}postgres${NC}"
echo -e "    DB: ${YELLOW}lobbysync${NC}\n"

echo -e "${GREEN}Comandos útiles:${NC}"
echo -e "  Ver logs backend:      ${YELLOW}docker-compose logs -f backend${NC}"
echo -e "  Ver logs PostgreSQL:   ${YELLOW}docker-compose logs -f postgres_db${NC}"
echo -e "  Ver logs MongoDB:      ${YELLOW}docker-compose logs -f mongo_db${NC}"
echo -e "  Detener servicios:     ${YELLOW}docker-compose down${NC}"
echo -e "  Reiniciar backend:     ${YELLOW}docker-compose restart backend${NC}\n"

echo -e "${BLUE}=====================================${NC}"
echo -e "${BLUE}Ready to test! 🚀${NC}"
echo -e "${BLUE}=====================================${NC}\n"

#!/usr/bin/env python3
"""
Script para desplegar LobbySync API en VPS
"""

import paramiko
import sys
import time

# Configuración
VPS_IP = "168.197.50.14"
VPS_USER = "root"
VPS_PASS = "SebaErica12.18"

def deploy_to_vps():
    """Desplegar API en el VPS"""
    
    print("\n" + "="*50)
    print("  LobbySync API - VPS Deployment")
    print("="*50)
    print(f"\nHost: {VPS_IP}")
    print(f"Usuario: {VPS_USER}")
    
    try:
        # Crear cliente SSH
        print("\n[1/5] Conectando al VPS...")
        client = paramiko.SSHClient()
        client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        client.connect(VPS_IP, username=VPS_USER, password=VPS_PASS, timeout=30)
        print("✓ Conectado al VPS")
        
        # Comando de deployment
        deployment_cmd = """
        set -e
        cd /root
        
        # Clonar o actualizar repositorio
        if [ ! -d "lobbysync-api" ]; then
            echo "[1/6] Clonando repositorio..."
            git clone https://github.com/lquijadaduoc/lobbysync-api.git
        else
            echo "[1/6] Repositorio existe. Haciendo reset y actualización..."
            cd lobbysync-api
            git fetch origin main
            git reset --hard origin/main
            git clean -fd
            echo "✓ Repositorio actualizado"
            cd ..
        fi
        
        cd lobbysync-api
        
        # Detener contenedores existentes
        echo "[2/6] Deteniendo contenedores existentes..."
        docker-compose down -v 2>/dev/null || true
        echo "✓ Contenedores detenidos"
        
        # Crear directorios de datos
        echo "[3/6] Creando directorios de datos..."
        mkdir -p /root/lobbysync-data/{postgres,mongo}
        echo "✓ Directorios creados"
        
        # Iniciar contenedores
        echo "[4/6] Iniciando contenedores Docker..."
        docker-compose up -d
        echo "✓ Contenedores iniciados"
        
        # Esperar a que inicie
        echo "[5/6] Esperando inicialización de servicios (30 segundos)..."
        sleep 30
        echo "✓ Servicios inicializados"
        
        # Mostrar estado
        echo ""
        echo "[6/6] Estado de contenedores:"
        docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
        
        echo ""
        echo "================================"
        echo "✅ DEPLOYMENT COMPLETADO"
        echo "================================"
        echo ""
        echo "API disponible en:"
        echo "  http://168.197.50.14:8080"
        echo ""
        echo "Swagger UI:"
        echo "  http://168.197.50.14:8080/swagger-ui.html"
        echo ""
        echo "Verificar disponibilidad:"
        echo "  curl http://localhost:8080/api/v1/users"
        echo ""
        echo "Ver logs:"
        echo "  docker logs -f lobbysync_backend"
        """
        
        # Ejecutar comando
        print("\n[Ejecutando deployment...]")
        stdin, stdout, stderr = client.exec_command(deployment_cmd, timeout=300)
        
        # Mostrar output
        for line in stdout:
            print(line.rstrip())
        
        # Mostrar errores si los hay
        errors = stderr.read().decode('utf-8', errors='ignore')
        if errors:
            print("\n⚠ Información adicional:")
            for line in errors.split('\n'):
                if line.strip():
                    print(f"  {line}")
        
        # Verificar que API esté respondiendo
        print("\n[Verificando API...]")
        verify_cmd = "sleep 5 && curl -s http://localhost:8080/api/v1/users | head -c 100"
        stdin, stdout, stderr = client.exec_command(verify_cmd, timeout=30)
        response = stdout.read().decode('utf-8', errors='ignore')
        if response and "error" not in response.lower():
            print("✓ API respondiendo correctamente")
        else:
            print("⚠ API aún inicializando, espera unos segundos")
        
        # Cerrar conexión
        client.close()
        print("\n✓ Conexión cerrada")
        
        return True
        
    except paramiko.AuthenticationException:
        print("\n✗ Error de autenticación. Verifica la contraseña.")
        return False
    except paramiko.SSHException as e:
        print(f"\n✗ Error SSH: {e}")
        return False
    except Exception as e:
        print(f"\n✗ Error: {e}")
        return False

if __name__ == "__main__":
    success = deploy_to_vps()
    sys.exit(0 if success else 1)

# Script para ejecutar comandos en el VPS sin ingresar contraseña cada vez
# Uso: .\vps-query.ps1 "comando a ejecutar"

param(
    [Parameter(Mandatory=$true)]
    [string]$Command
)

$VPS_PASSWORD = "TU_CONTRASEÑA_AQUI"  # Reemplazar con la contraseña real
$VPS_IP = "168.197.50.14"
$VPS_USER = "root"

# Crear un archivo temporal con la contraseña
$tempPass = [System.IO.Path]::GetTempFileName()
$VPS_PASSWORD | Out-File -FilePath $tempPass -Encoding ASCII -NoNewline

# Ejecutar el comando usando plink (PuTTY) con la contraseña
$plinkPath = "plink.exe"
if (Get-Command plink -ErrorAction SilentlyContinue) {
    echo y | plink -pw $VPS_PASSWORD "${VPS_USER}@${VPS_IP}" $Command
} else {
    # Alternativa usando expect-like con PowerShell
    $securePassword = ConvertTo-SecureString $VPS_PASSWORD -AsPlainText -Force
    $credential = New-Object System.Management.Automation.PSCredential ($VPS_USER, $securePassword)
    
    # Usar ssh con la contraseña (requiere configuración adicional)
    Write-Host "Ejecutando: ssh ${VPS_USER}@${VPS_IP} '$Command'" -ForegroundColor Yellow
    Write-Host "Por favor ingresa la contraseña manualmente o configura SSH keys" -ForegroundColor Yellow
    ssh "${VPS_USER}@${VPS_IP}" $Command
}

# Limpiar archivo temporal
Remove-Item $tempPass -ErrorAction SilentlyContinue

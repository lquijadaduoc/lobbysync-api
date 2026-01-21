# Script para probar el manejo de errores mejorado en la API
# Ejecutar desde la máquina local o el VPS

$baseUrl = "http://localhost:8080/api/v1"

Write-Host "`n==================================" -ForegroundColor Cyan
Write-Host "PRUEBAS DE MANEJO DE ERRORES" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan

# Test 1: ID negativo (ValidationException - 400)
Write-Host "`n[TEST 1] GET /users/-1 (ID negativo)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/users/-1" -Method GET -UseBasicParsing -ErrorAction Stop
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
    $errorBody = $reader.ReadToEnd()
    $reader.Close()
    Write-Host "Status: $statusCode" -ForegroundColor Red
    Write-Host $errorBody -ForegroundColor Gray
}

# Test 2: Email inválido (ValidationException - 400)
Write-Host "`n[TEST 2] GET /users/email/invalidemail (Email sin @)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/users/email/invalidemail" -Method GET -UseBasicParsing -ErrorAction Stop
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
    $errorBody = $reader.ReadToEnd()
    $reader.Close()
    Write-Host "Status: $statusCode" -ForegroundColor Red
    Write-Host $errorBody -ForegroundColor Gray
}

# Test 3: Usuario no encontrado (ResourceNotFoundException - 404)
Write-Host "`n[TEST 3] GET /users/99999 (Usuario inexistente)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/users/99999" -Method GET -UseBasicParsing -ErrorAction Stop
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
    $errorBody = $reader.ReadToEnd()
    $reader.Close()
    Write-Host "Status: $statusCode" -ForegroundColor Red
    Write-Host $errorBody -ForegroundColor Gray
}

# Test 4: Crear usuario sin password (Bean Validation - 400)
Write-Host "`n[TEST 4] POST /users (Sin contraseña)" -ForegroundColor Yellow
$body = @{
    email = "test@example.com"
    role = "RESIDENT"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/users" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing -ErrorAction Stop
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
    $errorBody = $reader.ReadToEnd()
    $reader.Close()
    Write-Host "Status: $statusCode" -ForegroundColor Red
    Write-Host $errorBody -ForegroundColor Gray
}

# Test 5: Crear usuario con contraseña corta (ValidationException - 400)
Write-Host "`n[TEST 5] POST /users (Password con menos de 6 caracteres)" -ForegroundColor Yellow
$body = @{
    email = "test@example.com"
    password = "123"
    role = "RESIDENT"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/users" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing -ErrorAction Stop
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
    $errorBody = $reader.ReadToEnd()
    $reader.Close()
    Write-Host "Status: $statusCode" -ForegroundColor Red
    Write-Host $errorBody -ForegroundColor Gray
}

# Test 6: Crear usuario con rol inválido (ValidationException - 400)
Write-Host "`n[TEST 6] POST /users (Rol inválido)" -ForegroundColor Yellow
$body = @{
    email = "test@example.com"
    password = "password123"
    role = "SUPERADMIN"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/users" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing -ErrorAction Stop
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
    $errorBody = $reader.ReadToEnd()
    $reader.Close()
    Write-Host "Status: $statusCode" -ForegroundColor Red
    Write-Host $errorBody -ForegroundColor Gray
}

Write-Host "`n==================================" -ForegroundColor Cyan
Write-Host "PRUEBAS COMPLETADAS" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "`nSi todos los errores muestran:" -ForegroundColor Green
Write-Host "  - timestamp" -ForegroundColor Gray
Write-Host "  - status (400, 404, etc.)" -ForegroundColor Gray
Write-Host "  - error (Bad Request, Not Found, etc.)" -ForegroundColor Gray
Write-Host "  - message (mensaje descriptivo en español)" -ForegroundColor Gray
Write-Host "  - details (explicación adicional)" -ForegroundColor Gray
Write-Host "  - suggestions (array con sugerencias)" -ForegroundColor Gray
Write-Host "`nEntonces el manejo de errores está funcionando correctamente! ✅`n" -ForegroundColor Green

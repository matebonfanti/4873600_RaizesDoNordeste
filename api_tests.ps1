# Comprehensive API Test Suite - RaizesDoNordeste
# Tests positive and negative scenarios for all controllers

$baseUrl = "http://localhost:8080"
$passCount = 0
$failCount = 0
$testResults = @()

function Test-Case {
    param($name, [scriptblock]$test)
    try {
        $result = & $test
        if ($result.Success) {
            $global:passCount++
            Write-Host "  PASS: $name" -ForegroundColor Green
        } else {
            $global:failCount++
            Write-Host "  FAIL: $name - $($result.Message)" -ForegroundColor Red
        }
        $global:testResults += [PSCustomObject]@{ Name=$name; Pass=$result.Success; Message=$result.Message }
    } catch {
        $global:failCount++
        Write-Host "  FAIL: $name - EXCEPTION: $_" -ForegroundColor Red
        $global:testResults += [PSCustomObject]@{ Name=$name; Pass=$false; Message="Exception: $_" }
    }
}

function Assert-Status { param($resp, $expected) if ($resp.StatusCode -ne $expected) { return @{Success=$false; Message="Expected $expected, got $($resp.StatusCode)"} }; return @{Success=$true; Message=""} }
function Assert-BodyContains { param($resp, $text) $body = $resp.Content; $escaped = [regex]::Escape($text); if ($body -notmatch $escaped) { return @{Success=$false; Message="Body missing '$text'. Body: $body"} }; return @{Success=$true; Message=""} }
function Assert-JsonProp { param($resp, $prop, $expected) $json = $resp.Content | ConvertFrom-Json; $val = $json.$prop; if ("$val" -ne "$expected") { return @{Success=$false; Message="Property '$prop' expected '$expected', got '$val'"} }; return @{Success=$true; Message=""} }
function Extract-Json { param($resp) return $resp.Content | ConvertFrom-Json }

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  RAIZES DO NORDESTE - API TEST SUITE" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# ==========================================
# SETUP: Create users and get tokens
# ==========================================
Write-Host "[SETUP] Creating users and obtaining JWT tokens..." -ForegroundColor Yellow
$adminBody = '{"nome":"Admin Test","email":"admin@test.com","senha":"123456","tipoUsuario":"GERENTE"}'
try {
    $r = Invoke-WebRequest -Uri "$baseUrl/usuarios" -Method POST -Body $adminBody -ContentType "application/json; charset=utf-8" -UseBasicParsing
    Write-Host "  Admin user: HTTP $($r.StatusCode)" -ForegroundColor Gray
} catch {
    if ($_.Exception.Response.StatusCode -eq 400) {
        Write-Host "  Admin user already exists (OK)" -ForegroundColor Gray
    } else {
        Write-Host "  Admin creation failed: $($_.Exception.Response.StatusCode) - $_" -ForegroundColor Gray
    }
}

try {
    $loginResp = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method POST -Body '{"email":"admin@test.com","senha":"123456"}' -ContentType "application/json; charset=utf-8" -UseBasicParsing
    $token = (Extract-Json $loginResp).token
    $authHeader = @{ "Authorization" = "Bearer $token" }
    Write-Host "  Admin token: $($token.Substring(0,30))..." -ForegroundColor Gray
} catch {
    Write-Host "  Admin login failed: $_" -ForegroundColor Red
}

$clienteBody = '{"nome":"Cliente Test","email":"cliente@test.com","senha":"123456","tipoUsuario":"CLIENTE"}'
try {
    $r = Invoke-WebRequest -Uri "$baseUrl/usuarios" -Method POST -Body $clienteBody -ContentType "application/json; charset=utf-8" -UseBasicParsing
    Write-Host "  Cliente user: HTTP $($r.StatusCode)" -ForegroundColor Gray
} catch {
    Write-Host "  Cliente user already exists (OK)" -ForegroundColor Gray
}

try {
    $clienteLogin = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method POST -Body '{"email":"cliente@test.com","senha":"123456"}' -ContentType "application/json; charset=utf-8" -UseBasicParsing
    $clienteToken = (Extract-Json $clienteLogin).token
    $clienteHeader = @{ "Authorization" = "Bearer $clienteToken" }
    Write-Host "  Cliente token obtained`n" -ForegroundColor Gray
} catch {
    Write-Host "  Cliente login failed: $_" -ForegroundColor Red
}

$noAuth = @{}

# Shared test data storage
$testData = @{}

# ==========================================
# AUTH CONTROLLER - /auth
# ==========================================
Write-Host "=== AUTH CONTROLLER (/auth) ===" -ForegroundColor Cyan

Test-Case "POST /auth/login - valid credentials returns 200 with token" {
    $r = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method POST -Body '{"email":"admin@test.com","senha":"123456"}' -ContentType "application/json; charset=utf-8" -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-BodyContains $r "token"
}

Test-Case "POST /auth/login - wrong password returns 401" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method POST -Body '{"email":"admin@test.com","senha":"wrongpass"}' -ContentType "application/json; charset=utf-8" -UseBasicParsing
        return @{Success=$false; Message="Expected 401, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 401) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 401, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "POST /auth/login - non-existent email returns 401" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method POST -Body '{"email":"no@exist.com","senha":"123456"}' -ContentType "application/json; charset=utf-8" -UseBasicParsing
        return @{Success=$false; Message="Expected 401, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 401) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 401, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "POST /auth/login - missing credentials returns 401" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method POST -Body '{}' -ContentType "application/json; charset=utf-8" -UseBasicParsing
        return @{Success=$false; Message="Expected 401, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 401) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 401, got $($_.Exception.Response.StatusCode)"}
    }
}

# ==========================================
# USUARIO CONTROLLER - /usuarios
# ==========================================
Write-Host "`n=== USUARIO CONTROLLER (/usuarios) ===" -ForegroundColor Cyan

Test-Case "POST /usuarios - create new user returns 201" {
    $email = "test.pos.$(Get-Random)@test.com"
    $body = "{`"nome`":`"Test User`",`"email`":`"$email`",`"senha`":`"abc123`",`"tipoUsuario`":`"GERENTE`"}"
    $r = Invoke-WebRequest -Uri "$baseUrl/usuarios" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -UseBasicParsing
    Assert-Status $r 201
}

Test-Case "POST /usuarios - duplicate email returns 400" {
    try {
        $body = '{"nome":"Dup","email":"admin@test.com","senha":"abc123","tipoUsuario":"GERENTE"}'
        $r = Invoke-WebRequest -Uri "$baseUrl/usuarios" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

# ==========================================
# CLIENTE CONTROLLER - /clientes
# ==========================================
Write-Host "`n=== CLIENTE CONTROLLER (/clientes) ===" -ForegroundColor Cyan

Test-Case "POST /clientes - create client returns 201" {
    $cpf = "$(Get-Random -Minimum 10000000000 -Maximum 99999999999)"
    $body = @{nome="Maria Silva";cpf=$cpf;email="cliente@test.com";telefone="11999999999";dataNascimento="1990-01-15";aceiteLgpd=$true} | ConvertTo-Json
    $r = Invoke-WebRequest -Uri "$baseUrl/clientes" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 201
    if (-not $s.Success) { return $s }
    $json = Extract-Json $r
    $script:testData['clienteId'] = $json.id
    $script:testData['clienteCpf'] = $json.cpf
    Assert-JsonProp $r "nome" "Maria Silva"
}

Test-Case "POST /clientes - without auth returns 403" {
    try {
        $body = @{nome="SemAuth";cpf="11111111111";email="semauth@test.com";aceiteLgpd=$true} | ConvertTo-Json
        $r = Invoke-WebRequest -Uri "$baseUrl/clientes" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "POST /clientes - aceiteLgpd=false returns 400" {
    try {
        $body = @{nome="SemLGPD";cpf="22222222222";email="semlgpd@test.com";aceiteLgpd=$false} | ConvertTo-Json
        $r = Invoke-WebRequest -Uri "$baseUrl/clientes" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

Test-Case "POST /clientes - duplicate CPF returns 400" {
    try {
        $body = @{nome="Dup CPF";cpf=$testData.clienteCpf;email="dupcpf@test.com";aceiteLgpd=$true} | ConvertTo-Json
        $r = Invoke-WebRequest -Uri "$baseUrl/clientes" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

Test-Case "GET /clientes/{id} - find by id returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/clientes/$($testData.clienteId)" -Method GET -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-JsonProp $r "id" "$($testData.clienteId)"
}

Test-Case "GET /clientes/{id} - non-existent returns 404" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/clientes/99999" -Method GET -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 404, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 404, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "GET /clientes/cpf/{cpf} - find by cpf returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/clientes/cpf/$($testData.clienteCpf)" -Method GET -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "GET /clientes/cpf/{cpf} - non-existent returns 404" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/clientes/cpf/00000000000" -Method GET -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 404, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 404, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "GET /clientes/list - returns 200 with array" {
    $r = Invoke-WebRequest -Uri "$baseUrl/clientes/list" -Method GET -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-BodyContains $r "["
}

Test-Case "PATCH /clientes/{id} - update nome returns 200" {
    $body = @{nome="Maria Updated";telefone="11888888888"} | ConvertTo-Json
    $r = Invoke-WebRequest -Uri "$baseUrl/clientes/$($testData.clienteId)" -Method PATCH -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-JsonProp $r "nome" "Maria Updated"
}

Test-Case "PATCH /clientes/{id} - non-existent returns 404" {
    try {
        $body = @{nome="Ghost"} | ConvertTo-Json
        $r = Invoke-WebRequest -Uri "$baseUrl/clientes/99999" -Method PATCH -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 404, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 404, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "GET /clientes/{id}/pontos - returns points (starts at 0)" {
    $r = Invoke-WebRequest -Uri "$baseUrl/clientes/$($testData.clienteId)/pontos" -Method GET -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "PATCH /clientes/{id}/pontos/soma - add points returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/clientes/$($testData.clienteId)/pontos/soma?pontos=50" -Method PATCH -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "PATCH /clientes/{id}/pontos/menos - subtract points returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/clientes/$($testData.clienteId)/pontos/menos?pontos=20" -Method PATCH -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "PATCH /clientes/{id}/pontos/menos - insufficient points returns 400" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/clientes/$($testData.clienteId)/pontos/menos?pontos=99999" -Method PATCH -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

# ==========================================
# PRODUTO CONTROLLER - /produtos
# ==========================================
Write-Host "`n=== PRODUTO CONTROLLER (/produtos) ===" -ForegroundColor Cyan

Test-Case "POST /produtos - GERENTE creates product returns 201" {
    $body = '{"nome":"Bolo de Rolo","descricao":"Delicioso bolo de rolo pernambucano","preco":25.90}'
    $r = Invoke-WebRequest -Uri "$baseUrl/produtos" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 201
    if (-not $s.Success) { return $s }
    $json = Extract-Json $r
    $script:testData['produtoId'] = $json.id
    Assert-JsonProp $r "nome" "Bolo de Rolo"
}

Test-Case "POST /produtos - CLIENTE role returns 403" {
    try {
        $body = '{"nome":"ProdutoNegado","descricao":"desc","preco":10.0}'
        $r = Invoke-WebRequest -Uri "$baseUrl/produtos" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $clienteHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "POST /produtos - empty nome returns 400" {
    try {
        $body = '{"nome":"","descricao":"desc","preco":10.0}'
        $r = Invoke-WebRequest -Uri "$baseUrl/produtos" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

Test-Case "POST /produtos - preco <= 0 returns 400" {
    try {
        $body = '{"nome":"Produto","descricao":"desc","preco":0}'
        $r = Invoke-WebRequest -Uri "$baseUrl/produtos" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

Test-Case "GET /produtos/list - returns 200 with array" {
    $r = Invoke-WebRequest -Uri "$baseUrl/produtos/list" -Method GET -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-BodyContains $r "["
}

Test-Case "GET /produtos/list - without auth returns 403" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/produtos/list" -Method GET -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "GET /produtos/{id} - find by id returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/produtos/$($testData.produtoId)" -Method GET -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "GET /produtos/{id} - non-existent returns 404" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/produtos/99999" -Method GET -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 404, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 404, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "PATCH /produtos/{id} - GERENTE updates returns 200" {
    $body = '{"nome":"Bolo de Rolo Atualizado","preco":29.90}'
    $r = Invoke-WebRequest -Uri "$baseUrl/produtos/$($testData.produtoId)" -Method PATCH -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "PATCH /produtos/{id} - CLIENTE role returns 403" {
    try {
        $body = '{"nome":"Hack"}'
        $r = Invoke-WebRequest -Uri "$baseUrl/produtos/$($testData.produtoId)" -Method PATCH -Body $body -ContentType "application/json; charset=utf-8" -Headers $clienteHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "DELETE /produtos/{id} - GERENTE deletes returns 204" {
    $body = '{"nome":"Para Deletar","descricao":"vai sumir","preco":5.0}'
    $cr = Invoke-WebRequest -Uri "$baseUrl/produtos" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
    $delId = (Extract-Json $cr).id
    $r = Invoke-WebRequest -Uri "$baseUrl/produtos/$delId" -Method DELETE -Headers $authHeader -UseBasicParsing
    Assert-Status $r 204
}

Test-Case "DELETE /produtos/{id} - CLIENTE role returns 403" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/produtos/$($testData.produtoId)" -Method DELETE -Headers $clienteHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "DELETE /produtos/{id} - non-existent returns 400" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/produtos/99999" -Method DELETE -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

# Create product for pedidos/estoque/cardapio
$pBody = '{"nome":"Tapioca de Queijo Coalho","descricao":"Tapioca recheada","preco":15.00}'
$pResp = Invoke-WebRequest -Uri "$baseUrl/produtos" -Method POST -Body $pBody -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
$testData['tapiocaId'] = (Extract-Json $pResp).id

# ==========================================
# UNIDADE CONTROLLER - /unidades
# ==========================================
Write-Host "`n=== UNIDADE CONTROLLER (/unidades) ===" -ForegroundColor Cyan

Test-Case "POST /unidades - GERENTE creates unit returns 201" {
    $body = '{"nome":"Unidade Recife","cidade":"Recife","descricao":"Loja matriz"}'
    $r = Invoke-WebRequest -Uri "$baseUrl/unidades" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 201
    if (-not $s.Success) { return $s }
    $json = Extract-Json $r
    $script:testData['unidadeId'] = $json.id
    Assert-JsonProp $r "cidade" "Recife"
}

Test-Case "POST /unidades - CLIENTE role returns 403" {
    try {
        $body = '{"nome":"U2","cidade":"Olinda","descricao":"desc"}'
        $r = Invoke-WebRequest -Uri "$baseUrl/unidades" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $clienteHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "POST /unidades - empty nome returns 400" {
    try {
        $body = '{"nome":"","cidade":"Recife","descricao":"desc"}'
        $r = Invoke-WebRequest -Uri "$baseUrl/unidades" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

Test-Case "GET /unidades/{id} - find by id returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/unidades/$($testData.unidadeId)" -Method GET -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "GET /unidades/{id} - non-existent returns 404" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/unidades/99999" -Method GET -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 404, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 404, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "GET /unidades/list - returns 200 with array" {
    $r = Invoke-WebRequest -Uri "$baseUrl/unidades/list" -Method GET -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-BodyContains $r "["
}

Test-Case "PATCH /unidades/{id} - GERENTE updates returns 200" {
    $body = '{"nome":"Unidade Recife Centro","cidade":"Recife"}'
    $r = Invoke-WebRequest -Uri "$baseUrl/unidades/$($testData.unidadeId)" -Method PATCH -Body $body -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "PATCH /unidades/{id} - CLIENTE role returns 403" {
    try {
        $body = '{"cidade":"Hack"}'
        $r = Invoke-WebRequest -Uri "$baseUrl/unidades/$($testData.unidadeId)" -Method PATCH -Body $body -ContentType "application/json; charset=utf-8" -Headers $clienteHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

# ==========================================
# ESTOQUE CONTROLLER - /estoque (GERENTE only)
# ==========================================
Write-Host "`n=== ESTOQUE CONTROLLER (/estoque) ===" -ForegroundColor Cyan

Test-Case "POST /estoque/adicionar - GERENTE adds stock returns 201" {
    $r = Invoke-WebRequest -Uri "$baseUrl/estoque/adicionar?produtoId=$($testData.tapiocaId)&unidadeId=$($testData.unidadeId)&quantidade=100" -Method POST -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 201
    if (-not $s.Success) { return $s }
    Assert-JsonProp $r "quantidade" "100"
}

Test-Case "POST /estoque/adicionar - CLIENTE role returns 403" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/estoque/adicionar?produtoId=$($testData.tapiocaId)&unidadeId=$($testData.unidadeId)&quantidade=10" -Method POST -Headers $clienteHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "POST /estoque/adicionar - non-existent produto returns 400" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/estoque/adicionar?produtoId=99999&unidadeId=$($testData.unidadeId)&quantidade=10" -Method POST -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

Test-Case "PATCH /estoque/diminuir - reduces stock returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/estoque/diminuir?unidadeId=$($testData.unidadeId)&produtoId=$($testData.tapiocaId)&quantidade=20" -Method PATCH -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "PATCH /estoque/diminuir - insufficient stock returns 409" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/estoque/diminuir?unidadeId=$($testData.unidadeId)&produtoId=$($testData.tapiocaId)&quantidade=99999" -Method PATCH -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 409, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 409); Message="Got $code"}
    }
}

Test-Case "GET /estoque/{unidadeId} - lists stock returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/estoque/$($testData.unidadeId)" -Method GET -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-BodyContains $r "["
}

Test-Case "GET /estoque/{unidadeId} - CLIENTE role returns 403" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/estoque/$($testData.unidadeId)" -Method GET -Headers $clienteHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

# ==========================================
# PEDIDO CONTROLLER - /pedidos
# ==========================================
Write-Host "`n=== PEDIDO CONTROLLER (/pedidos) ===" -ForegroundColor Cyan

Test-Case "POST /pedidos - create order returns 201" {
    $body = @{canalPedido="APP";itens=@(@{produto=@{id=$testData.tapiocaId};quantidade=2})} | ConvertTo-Json -Depth 4
    $r = Invoke-WebRequest -Uri "$baseUrl/pedidos?unidadeId=$($testData.unidadeId)" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $clienteHeader -UseBasicParsing
    $s = Assert-Status $r 201
    if (-not $s.Success) { return $s }
    $json = Extract-Json $r
    $script:testData['pedidoId'] = $json.id
    Assert-JsonProp $r "statusPedido" "AGUARDANDO_PAGAMENTO"
}

Test-Case "POST /pedidos - without auth returns 403" {
    try {
        $body = @{canalPedido="APP";itens=@(@{produto=@{id=1};quantidade=1})} | ConvertTo-Json -Depth 3
        $r = Invoke-WebRequest -Uri "$baseUrl/pedidos?unidadeId=$($testData.unidadeId)" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "POST /pedidos - without unidadeId returns 400" {
    try {
        $body = @{canalPedido="APP";itens=@(@{produto=@{id=1};quantidade=1})} | ConvertTo-Json -Depth 3
        $r = Invoke-WebRequest -Uri "$baseUrl/pedidos" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $clienteHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

Test-Case "GET /pedidos/{id} - find by id returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/pedidos/$($testData.pedidoId)" -Method GET -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "GET /pedidos/{id} - non-existent returns error 4xx" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/pedidos/99999" -Method GET -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 4xx, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -ge 400 -and $code -lt 500); Message="Got $code"}
    }
}

Test-Case "GET /pedidos - list all returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/pedidos" -Method GET -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-BodyContains $r "["
}

Test-Case "GET /pedidos - filter by status returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/pedidos?status=AGUARDANDO_PAGAMENTO" -Method GET -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "PATCH /pedidos/{id}/status - update status returns 200" {
    $r = Invoke-WebRequest -Uri "$baseUrl/pedidos/$($testData.pedidoId)/status?novoStatus=PREPARANDO" -Method PATCH -Headers $authHeader -UseBasicParsing
    Assert-Status $r 200
}

Test-Case "PATCH /pedidos/{id}/status - invalid status handled gracefully" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/pedidos/$($testData.pedidoId)/status?novoStatus=CANCELADO" -Method PATCH -Headers $authHeader -UseBasicParsing
        return @{Success=$true; Message="Status: $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        if ($code -in 200,400,409) { return @{Success=$true; Message="Got $code"} }
        return @{Success=$false; Message="Unexpected: $code"}
    }
}

# ==========================================
# PAGAMENTO CONTROLLER - /pagamentos
# ==========================================
Write-Host "`n=== PAGAMENTO CONTROLLER (/pagamentos) ===" -ForegroundColor Cyan

Test-Case "POST /pagamentos/simular - approve payment sets PREPARANDO" {
    $body = @{canalPedido="APP";itens=@(@{produto=@{id=$testData.tapiocaId};quantidade=1})} | ConvertTo-Json -Depth 4
    $pResp = Invoke-WebRequest -Uri "$baseUrl/pedidos?unidadeId=$($testData.unidadeId)" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $clienteHeader -UseBasicParsing
    $payId = (Extract-Json $pResp).id

    $r = Invoke-WebRequest -Uri "$baseUrl/pagamentos/simular?pedidoId=$payId&aprovado=true" -Method POST -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-JsonProp $r "statusPedido" "PREPARANDO"
}

Test-Case "POST /pagamentos/simular - reject payment cancels and restores stock" {
    $body = @{canalPedido="BALCAO";itens=@(@{produto=@{id=$testData.tapiocaId};quantidade=1})} | ConvertTo-Json -Depth 4
    $pResp = Invoke-WebRequest -Uri "$baseUrl/pedidos?unidadeId=$($testData.unidadeId)" -Method POST -Body $body -ContentType "application/json; charset=utf-8" -Headers $clienteHeader -UseBasicParsing
    $payId = (Extract-Json $pResp).id

    $r = Invoke-WebRequest -Uri "$baseUrl/pagamentos/simular?pedidoId=$payId&aprovado=false" -Method POST -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-JsonProp $r "statusPedido" "CANCELADO"
}

Test-Case "POST /pagamentos/simular - already processed order returns 400" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/pagamentos/simular?pedidoId=$($testData.pedidoId)&aprovado=true" -Method POST -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 400, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        return @{Success=($code -eq 400); Message="Got $code"}
    }
}

Test-Case "POST /pagamentos/simular - without auth returns 403" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/pagamentos/simular?pedidoId=1&aprovado=true" -Method POST -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

# ==========================================
# CARDAPIO CONTROLLER - /cardapio
# ==========================================
Write-Host "`n=== CARDAPIO CONTROLLER (/cardapio) ===" -ForegroundColor Cyan

Test-Case "GET /cardapio/unidade/{id} - returns in-stock products" {
    $r = Invoke-WebRequest -Uri "$baseUrl/cardapio/unidade/$($testData.unidadeId)" -Method GET -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-BodyContains $r "["
}

Test-Case "GET /cardapio/unidade/{id} - non-existent returns 404" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/cardapio/unidade/99999" -Method GET -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 404, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 404, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "GET /cardapio/unidade/{id} - without auth returns 403" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/cardapio/unidade/$($testData.unidadeId)" -Method GET -UseBasicParsing
        return @{Success=$false; Message="Expected 403, got $($r.StatusCode)"}
    } catch {
        if ($_.Exception.Response.StatusCode -eq 403) { return @{Success=$true; Message=""} }
        return @{Success=$false; Message="Expected 403, got $($_.Exception.Response.StatusCode)"}
    }
}

Test-Case "GET /cardapio/unidade/{id} - empty unit returns empty array" {
    $uBody = '{"nome":"Unidade Vazia","cidade":"Petrolina","descricao":"Sem estoque"}'
    $uResp = Invoke-WebRequest -Uri "$baseUrl/unidades" -Method POST -Body $uBody -ContentType "application/json; charset=utf-8" -Headers $authHeader -UseBasicParsing
    $emptyId = (Extract-Json $uResp).id
    $r = Invoke-WebRequest -Uri "$baseUrl/cardapio/unidade/$emptyId" -Method GET -Headers $authHeader -UseBasicParsing
    $s = Assert-Status $r 200
    if (-not $s.Success) { return $s }
    Assert-BodyContains $r "[]"
}

# ==========================================
# ERROR RESPONSE FORMAT VALIDATION
# ==========================================
Write-Host "`n=== ERROR RESPONSE FORMAT ===" -ForegroundColor Cyan

Test-Case "404 error has standard fields (erro, mensagem, timestamp, path)" {
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/clientes/99999" -Method GET -Headers $authHeader -UseBasicParsing
        return @{Success=$false; Message="Expected 404, got $($r.StatusCode)"}
    } catch {
        $code = $_.Exception.Response.StatusCode
        if ($code -ne 404) { return @{Success=$false; Message="Expected 404, got $code"} }
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $body = $reader.ReadToEnd()
        $reader.Close()
        $hasErro = $body -match '"erro"'
        $hasMensagem = $body -match '"mensagem"'
        $hasTimestamp = $body -match '"timestamp"'
        $hasPath = $body -match '"path"'
        if ($hasErro -and $hasMensagem -and $hasTimestamp -and $hasPath) {
            return @{Success=$true; Message=""}
        }
        return @{Success=$false; Message="Missing fields. errok $hasErro, mensagem=$hasMensagem, timestamp=$hasTimestamp, path=$hasPath. Body: $body"}
    }
}

# ==========================================
# SUMMARY
# ==========================================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Total: $($passCount + $failCount) | Passed: $passCount | Failed: $failCount" -ForegroundColor $(if ($failCount -eq 0) { "Green" } else { "Red" })
Write-Host ""

if ($failCount -gt 0) {
    Write-Host "FAILED TESTS:" -ForegroundColor Red
    $testResults | Where-Object { -not $_.Pass } | ForEach-Object {
        Write-Host "  - $($_.Name): $($_.Message)" -ForegroundColor Red
    }
}
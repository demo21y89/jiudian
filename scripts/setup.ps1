@'
# ============================================
# AI Agent 农产品溯源智能交易平台
# 环境安装脚本 (PowerShell)
# ============================================

Write-Host "正在检查环境..." -ForegroundColor Cyan

# ---------- Java 21 ----------
$javaVer = java --version 2>$null
if ($LASTEXITCODE -eq 0 -and $javaVer -match "21") {
    Write-Host "✓ Java 21 已安装" -ForegroundColor Green
} else {
    Write-Host "→ 请安装 JDK 21 (Adoptium)" -ForegroundColor Yellow
    Write-Host "  下载: https://adoptium.net/temurin/releases/?version=21"
}

# ---------- Maven ----------
$mvnVer = mvn --version 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Maven 已安装" -ForegroundColor Green
} else {
    Write-Host "→ 请安装 Maven 3.9+" -ForegroundColor Yellow
    Write-Host "  下载: https://maven.apache.org/download.cgi"
}

# ---------- PostgreSQL ----------
$psqlVer = psql --version 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ PostgreSQL 已安装" -ForegroundColor Green

    # 创建数据库
    $env:PGPASSWORD = "postgres"
    & pg_isready -h localhost -p 5432 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) {
        & createdb -U postgres -h localhost agritrace 2>$null
        & psql -U postgres -h localhost -d agritrace -c "CREATE EXTENSION IF NOT EXISTS vector;" 2>$null
        Write-Host "✓ 数据库 agritrace 已创建，pgvector 已启用" -ForegroundColor Green
    } else {
        Write-Host "! PostgreSQL 服务未启动，请先启动服务" -ForegroundColor Yellow
    }
} else {
    Write-Host "→ 请安装 PostgreSQL 16+ (含 pgvector 扩展)" -ForegroundColor Yellow
    Write-Host "  下载: https://www.enterprisedb.com/downloads/postgres-postgresql-downloads"
}

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "  环境检查完成" -ForegroundColor Cyan

Write-Host "`n📦 H2 开发模式（推荐，无需外部数据库）:" -ForegroundColor Green
Write-Host "   cd D:\shixun2026\group_test" -ForegroundColor White
Write-Host "   .\mvnw spring-boot:run" -ForegroundColor White

Write-Host "`n🐘 PostgreSQL 生产模式:" -ForegroundColor Green
Write-Host "   .\mvnw spring-boot:run -Dspring-boot.run.profiles=default" -ForegroundColor White

Write-Host "`n🌐 访问地址: http://localhost:8080" -ForegroundColor Green
Write-Host "📋 H2控制台: http://localhost:8080/h2-console" -ForegroundColor Green

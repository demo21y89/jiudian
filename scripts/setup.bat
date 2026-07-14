@echo off
chcp 65001 >nul
title AI Agent 农产品溯源智能交易平台 - 环境安装工具
echo ============================================
echo   AI Agent 农产品溯源智能交易平台
echo   环境自动安装脚本
echo ============================================
echo.

REM ========== 检测管理员权限 ==========
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] 请以管理员身份运行此脚本
    pause
    exit /b 1
)

echo [1/4] 正在安装 Java 21...
where java >nul 2>&1
if %errorlevel% equ 0 (
    java --version 2>nul | find "21" >nul
    if %errorlevel% equ 0 (
        echo [✓] Java 21 已安装
    ) else (
        echo [!] 已安装非 JDK 21 版本，请手动安装 JDK 21
        echo     下载地址: https://adoptium.net/temurin/releases/?version=21
    )
) else (
    echo [*] 正在下载 Java 21...
    curl -L -o %TEMP%\java21.msi https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.3%2B9/OpenJDK21U-jdk_x64_windows_hotspot_21.0.3_9.msi
    msiexec /i %TEMP%\java21.msi /quiet INSTALLDIR="C:\Program Files\Eclipse Adoptium\jdk-21.0.3-hotspot" ADDLOCAL=FeatureEnvironment
    echo [✓] Java 21 安装完成，请重启终端
)

echo.
echo [2/4] 正在安装 Maven...
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] Maven 已安装
) else (
    echo [*] 正在下载 Maven 3.9.9...
    curl -L -o %TEMP%\maven.zip https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip
    powershell -Command "Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath 'C:\Program Files\Maven' -Force"
    setx PATH "%PATH%;C:\Program Files\Maven\apache-maven-3.9.9\bin" /M
    echo [✓] Maven 安装完成，请重启终端
)

echo.
echo [3/4] 正在安装 PostgreSQL 16 + pgvector...
where psql >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] PostgreSQL 已安装
) else (
    echo [*] 正在下载 PostgreSQL 16...
    curl -L -o %TEMP%\postgresql.exe https://get.enterprisedb.com/postgresql/postgresql-16.3-1-windows-x64.exe
    echo [*] 请在弹出的安装程序中：
    echo      1. 设置密码为: postgres
    echo      2. 保持端口: 5432
    echo      3. 取消勾选 Stack Builder
    start /WAIT %TEMP%\postgresql.exe --unattendedmodeui minimal --superpassword postgres --installerdir "C:\Program Files\PostgreSQL\16"
    echo [*] 安装 pgvector 扩展...
    curl -L -o %TEMP%\pgvector.zip https://github.com/pgvector/pgvector/releases/download/v0.7.4/pgvector-0.7.4-windows-x64.zip
    powershell -Command "Expand-Archive -Path '%TEMP%\pgvector.zip' -DestinationPath 'C:\Program Files\PostgreSQL\16' -Force"
    echo [✓] PostgreSQL 16 安装完成
)

echo.
echo [4/4] 创建数据库...
set PGPASSWORD=postgres
"C:\Program Files\PostgreSQL\16\bin\createdb" -U postgres -h localhost agritrace 2>nul
if %errorlevel% equ 0 (
    echo [✓] 数据库 agritrace 创建成功
) else (
    echo [*] 数据库可能已存在，跳过创建
)

echo [*] 启用 pgvector 扩展...
"C:\Program Files\PostgreSQL\16\bin\psql" -U postgres -h localhost -d agritrace -c "CREATE EXTENSION IF NOT EXISTS vector;" 2>nul
echo [✓] pgvector 扩展已启用

echo.
echo ============================================
echo   环境安装完成！
echo.
echo   快速启动命令：
echo     cd /d D:\shixun2026\group_test
echo     mvn spring-boot:run -Dspring-boot.run.profiles=default
echo.
echo   或使用 H2 开发模式（无需 PostgreSQL）：
echo     mvn spring-boot:run
echo.
echo   H2 控制台：http://localhost:8080/h2-console
echo   JDBC URL：jdbc:h2:mem:agritrace
echo ============================================
pause

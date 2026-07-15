@echo off
chcp 65001 >nul
echo 启动农产品溯源平台
echo 首次使用请先编译：mvn clean package -DskipTests
echo.
java -jar target\agriculture-trace-platform-1.0.0.jar
pause
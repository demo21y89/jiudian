@echo off
chcp 65001 >nul
echo 启动农产品溯源平台
echo 首次使用请先编译：mvn clean package -DskipTests
echo.
echo 首次运行需设置 AI_API_KEY 环境变量
echo 或直接编辑此文件，将 YOUR_API_KEY 替换为实际密钥
echo.
set AI_API_KEY=YOUR_API_KEY
java -jar target\agriculture-trace-platform-1.0.0.jar
pause
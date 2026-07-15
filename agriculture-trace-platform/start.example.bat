@echo off
chcp 65001 >nul
echo 启动农产品溯源平台
echo.
echo 首次使用请先编译：mvn clean package -DskipTests
echo.
echo 请将下方 YOUR_API_KEY 替换为你的 API Key
echo 获取Key：https://openrouter.ai/keys
echo.
pause

java -DAI_API_KEY="YOUR_API_KEY" ^
     -DAI_PROVIDER=openai ^
     -DAI_BASE_URL="https://openrouter.ai/api/v1" ^
     -DAI_MODEL="deepseek/deepseek-chat" ^
     -jar target\agriculture-trace-platform-1.0.0.jar
pause
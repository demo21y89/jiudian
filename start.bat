@echo off
set JAVA_HOME=D:\shixun2026\group_test\tools\jdk17
set Path=%JAVA_HOME%\bin;D:\shixun2026\group_test\tools\maven\bin;%Path%
cd /d D:\shixun2026\group_test
echo Starting AgriTrace Platform...
echo ?????: http://localhost:8080
java -jar target\agri-trace-platform-1.0.0-SNAPSHOT.jar
pause

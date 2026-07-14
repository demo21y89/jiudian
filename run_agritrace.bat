@echo off
cd /d D:\shixun2026\group_test
set JAVA_HOME=D:\shixun2026\group_test\tools\jdk17
set Path=%JAVA_HOME%\bin;%Path%
echo %date% %time% Starting AgriTrace... >> D:\shixun2026\group_test\startup.log
D:\shixun2026\group_test\tools\jdk17\bin\java.exe -jar D:\shixun2026\group_test\target\agri-trace-platform-1.0.0-SNAPSHOT.jar >> D:\shixun2026\group_test\startup.log 2>&1

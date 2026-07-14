@echo off
cd /d D:\shixun2026\group_test
tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I "java.exe" >NUL
if "%ERRORLEVEL%"=="0" (
    echo [OK] AgriTrace ????
    echo ??: http://localhost:8080
) else (
    echo [!!] AgriTrace ????????...
    start /B D:\shixun2026\group_test\tools\jdk17\bin\java.exe -jar D:\shixun2026\group_test\target\agri-trace-platform-1.0.0-SNAPSHOT.jar > D:\shixun2026\group_test\startup.log 2>&1
    echo ??????20?...
    timeout /T 20 /NOBREAK >NUL
    echo ??: http://localhost:8080
)
pause

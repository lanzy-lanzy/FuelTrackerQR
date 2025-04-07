@echo off
set JAVA_HOME=C:\Users\dttsi\AndroidStudioProjects\FuelTrackerQR-Clean\jdk
set PATH=%JAVA_HOME%\bin;%PATH%
echo Using Java from %JAVA_HOME%
call gradlew.bat assembleDebug
if %ERRORLEVEL% EQU 0 (
    echo Build successful! APK is located at: %CD%\app\build\outputs\apk\debug\app-debug.apk
) else (
    echo Build failed with exit code %ERRORLEVEL%
)

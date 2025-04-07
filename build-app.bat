@echo off
echo Setting up environment...

REM Try to find Java in common locations
set JAVA_HOME=C:\Program Files\Java\jdk-17
if exist "%JAVA_HOME%" goto found_java

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_301
if exist "%JAVA_HOME%" goto found_java

set JAVA_HOME=C:\Program Files\Android\Android Studio\jre
if exist "%JAVA_HOME%" goto found_java

set JAVA_HOME=%LOCALAPPDATA%\Android\Sdk\jdk
if exist "%JAVA_HOME%" goto found_java

echo Java not found in common locations. Trying to use Android Studio's bundled JBR...
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
if exist "%JAVA_HOME%" goto found_java

echo Java not found. Please install Java and set JAVA_HOME.
exit /b 1

:found_java
echo Found Java at %JAVA_HOME%
set PATH=%JAVA_HOME%\bin;%PATH%

echo Building the app...
call gradlew.bat --info assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo Build successful! APK is located at: %CD%\app\build\outputs\apk\debug\app-debug.apk
) else (
    echo Build failed with exit code %ERRORLEVEL%
)

@echo off
echo ===================================================
echo FuelTrackerQR Build and Install Launcher
echo ===================================================

REM Try to run Gradle commands directly first
echo Trying to run Gradle commands directly...

REM Check if Java is available
java -version > nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Java not found in PATH. Will try alternative methods.
    goto :try_alternative
)

REM Try to build the app
echo Building the app with Gradle...
call gradlew.bat assembleDebug
if %ERRORLEVEL% neq 0 (
    echo Direct Gradle build failed. Will try alternative methods.
    goto :try_alternative
)

REM Try to install the app
echo Installing the app with Gradle...
call gradlew.bat installDebug
if %ERRORLEVEL% neq 0 (
    echo Direct Gradle installation failed. Will try alternative methods.
    goto :try_alternative
)

echo ===================================================
echo Success! App built and installed directly.
echo ===================================================
echo You can now test the profile picture upload functionality.
goto :end

:try_alternative
echo ===================================================
echo Trying alternative build method with Java download...
echo ===================================================
call build_with_java_download.bat
goto :end

:end
echo ===================================================
echo Script completed.
echo ===================================================

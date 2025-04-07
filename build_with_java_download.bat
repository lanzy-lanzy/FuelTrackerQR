@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo FuelTrackerQR Build and Install Script
echo ===================================================

REM Check if Java is installed
set "JAVA_PATH="
set "JAVA_DOWNLOAD_NEEDED=false"

REM Check common locations
if exist "%PROGRAMFILES%\Java" (
    for /d %%i in ("%PROGRAMFILES%\Java\jdk*") do set "JAVA_PATH=%%i"
)

if exist "%PROGRAMFILES%\Android\Android Studio\jbr" (
    set "JAVA_PATH=%PROGRAMFILES%\Android\Android Studio\jbr"
)

if "%JAVA_PATH%"=="" (
    echo Java not found. Will download and install OpenJDK.
    set "JAVA_DOWNLOAD_NEEDED=true"
)

REM Download and install Java if needed
if "%JAVA_DOWNLOAD_NEEDED%"=="true" (
    echo ===================================================
    echo Downloading OpenJDK...
    echo ===================================================
    
    REM Create a temporary directory
    set "TEMP_DIR=%TEMP%\java_download"
    if not exist "%TEMP_DIR%" mkdir "%TEMP_DIR%"
    
    REM Download OpenJDK
    echo Downloading OpenJDK...
    powershell -Command "& {Invoke-WebRequest -Uri 'https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip' -OutFile '%TEMP_DIR%\openjdk.zip'}"
    
    if not exist "%TEMP_DIR%\openjdk.zip" (
        echo Failed to download OpenJDK.
        goto :error
    )
    
    REM Extract OpenJDK
    echo Extracting OpenJDK...
    powershell -Command "& {Expand-Archive -Path '%TEMP_DIR%\openjdk.zip' -DestinationPath '%TEMP_DIR%' -Force}"
    
    REM Find the extracted JDK directory
    for /d %%i in ("%TEMP_DIR%\jdk*") do set "JAVA_PATH=%%i"
    
    if "%JAVA_PATH%"=="" (
        echo Failed to extract OpenJDK.
        goto :error
    )
    
    echo OpenJDK extracted to: %JAVA_PATH%
)

echo Using Java from: %JAVA_PATH%
set "JAVA_HOME=%JAVA_PATH%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Check if ADB is available
set "ADB_PATH="

if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    set "ADB_PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
    echo Found ADB at: %ADB_PATH%
) else if exist "%ANDROID_HOME%\platform-tools\adb.exe" (
    set "ADB_PATH=%ANDROID_HOME%\platform-tools\adb.exe"
    echo Found ADB at: %ADB_PATH%
) else (
    echo ADB not found. Will try to use Gradle for installation.
)

REM Build the app
echo ===================================================
echo Building the app...
echo ===================================================

if exist "gradlew.bat" (
    echo Running Gradle wrapper...
    call gradlew.bat --no-daemon assembleDebug
    
    if %ERRORLEVEL% neq 0 (
        echo Build failed!
        goto :error
    )
    
    echo Build successful!
) else (
    echo Gradle wrapper not found.
    goto :error
)

REM Install the app
echo ===================================================
echo Installing the app...
echo ===================================================

REM Try using Gradle for installation
if exist "gradlew.bat" (
    echo Installing with Gradle...
    call gradlew.bat --no-daemon installDebug
    
    if %ERRORLEVEL% neq 0 (
        echo Installation with Gradle failed.
        
        REM Try using ADB as fallback
        if not "%ADB_PATH%"=="" (
            echo Trying installation with ADB...
            
            REM Check for connected devices
            "%ADB_PATH%" devices | findstr /R /C:"device$" > nul
            if %ERRORLEVEL% neq 0 (
                echo No Android devices connected.
                echo Please connect a device and try again.
                goto :error
            )
            
            REM Install the app
            if exist "app\build\outputs\apk\debug\app-debug.apk" (
                "%ADB_PATH%" install -r "app\build\outputs\apk\debug\app-debug.apk"
                
                if %ERRORLEVEL% neq 0 (
                    echo Installation with ADB failed.
                    goto :error
                ) else (
                    echo App installed successfully with ADB!
                )
            ) else (
                echo Debug APK not found.
                goto :error
            )
        ) else (
            echo ADB not found for fallback installation.
            goto :error
        )
    ) else (
        echo App installed successfully with Gradle!
    )
) else (
    echo Gradle wrapper not found for installation.
    goto :error
)

echo ===================================================
echo Success! App built and installed.
echo ===================================================
echo You can now test the profile picture upload functionality.
goto :end

:error
echo ===================================================
echo Error occurred during build or install.
echo ===================================================
echo Alternative method:
echo 1. Open the project in Android Studio
echo 2. Click on "Build" in the top menu
echo 3. Select "Build Bundle(s) / APK(s)" then "Build APK(s)"
echo 4. After the build completes, click on "Run" in the top menu
echo 5. Select "Run 'app'" to install and run the app

:end
echo ===================================================
echo Script completed.
echo ===================================================
pause

@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo FuelTrackerQR Build and Install Script
echo ===================================================

REM Check if Android Studio is installed
set "AS_PATH="
if exist "%LOCALAPPDATA%\Android\Sdk" (
    set "ANDROID_SDK=%LOCALAPPDATA%\Android\Sdk"
    echo Found Android SDK at: %ANDROID_SDK%
) else (
    echo Android SDK not found in default location.
    echo Please make sure Android Studio is installed.
)

REM Find Java
echo Searching for Java...
set "JAVA_PATH="

REM Check Android Studio JDK
if exist "%PROGRAMFILES%\Android\Android Studio\jbr" (
    set "JAVA_PATH=%PROGRAMFILES%\Android\Android Studio\jbr"
    echo Found Java in Android Studio: !JAVA_PATH!
    goto :found_java
)

REM Check common JDK locations
for %%p in (
    "%PROGRAMFILES%\Java\jdk*"
    "%PROGRAMFILES%\Eclipse Adoptium\jdk*"
    "%PROGRAMFILES%\Eclipse Foundation\jdk*"
    "%PROGRAMFILES%\AdoptOpenJDK\jdk*"
    "%PROGRAMFILES%\Zulu\zulu*"
) do (
    for /d %%j in (%%p) do (
        if exist "%%j\bin\java.exe" (
            set "JAVA_PATH=%%j"
            echo Found Java at: !JAVA_PATH!
            goto :found_java
        )
    )
)

REM Check if JAVA_HOME is already set
if defined JAVA_HOME (
    if exist "!JAVA_HOME!\bin\java.exe" (
        set "JAVA_PATH=!JAVA_HOME!"
        echo Using existing JAVA_HOME: !JAVA_PATH!
        goto :found_java
    )
)

echo Java not found. Will try to use Android Studio's embedded Gradle.
goto :try_direct_adb

:found_java
echo Setting JAVA_HOME to: !JAVA_PATH!
set "JAVA_HOME=!JAVA_PATH!"
set "PATH=!JAVA_HOME!\bin;%PATH%"

echo ===================================================
echo Building the app with Gradle...
echo ===================================================

REM Try using Gradle wrapper
if exist "gradlew.bat" (
    echo Running Gradle wrapper...
    call gradlew.bat --no-daemon assembleDebug

    if !ERRORLEVEL! neq 0 (
        echo Build failed with Gradle wrapper.
        goto :try_direct_adb
    ) else (
        echo Build successful!
        goto :install_app
    )
) else (
    echo Gradle wrapper not found.
    goto :try_direct_adb
)

:install_app
echo ===================================================
echo Installing the app with Gradle...
echo ===================================================

REM Try using Gradle wrapper for installation
if exist "gradlew.bat" (
    echo Running Gradle wrapper for installation...
    call gradlew.bat --no-daemon installDebug

    if !ERRORLEVEL! neq 0 (
        echo Installation failed with Gradle wrapper.
        goto :try_direct_adb
    ) else (
        echo App installed successfully with Gradle!
        goto :success
    )
) else (
    echo Gradle wrapper not found for installation.
    goto :try_direct_adb
)

:try_direct_adb
echo ===================================================
echo Trying direct ADB installation...
echo ===================================================

REM Check if ADB is available
if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"

    REM Check for connected devices
    "!ADB!" devices | findstr /R /C:"device$" > nul
    if !ERRORLEVEL! neq 0 (
        echo No Android devices connected.
        echo Please connect a device and try again.
        goto :error
    )

    REM Install the app
    echo Installing the app on connected device...
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        "!ADB!" install -r "app\build\outputs\apk\debug\app-debug.apk"
        if !ERRORLEVEL! neq 0 (
            echo Installation failed with ADB.
            goto :error
        ) else (
            echo App installed successfully with ADB!
            goto :success
        )
    ) else (
        echo Debug APK not found.
        echo Please check if the build was successful.
        goto :error
    )
) else (
    echo ADB not found.
    echo Please install the app manually from Android Studio.
    goto :error
)

:success
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

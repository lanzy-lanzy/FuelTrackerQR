@echo off
echo Opening Android Studio...

REM Try to find Android Studio
set "AS_PATH="

if exist "%PROGRAMFILES%\Android\Android Studio\bin\studio64.exe" (
    set "AS_PATH=%PROGRAMFILES%\Android\Android Studio\bin\studio64.exe"
) else if exist "%PROGRAMFILES(x86)%\Android\Android Studio\bin\studio64.exe" (
    set "AS_PATH=%PROGRAMFILES(x86)%\Android\Android Studio\bin\studio64.exe"
) else if exist "%LOCALAPPDATA%\Android\Android Studio\bin\studio64.exe" (
    set "AS_PATH=%LOCALAPPDATA%\Android\Android Studio\bin\studio64.exe"
)

if "%AS_PATH%"=="" (
    echo Android Studio not found in common locations.
    echo Please open Android Studio manually and build the project.
    pause
    exit /b 1
)

echo Found Android Studio at: %AS_PATH%
echo.
echo Please follow these steps in Android Studio:
echo 1. Wait for the project to load
echo 2. Click on "Build" in the top menu
echo 3. Select "Build Bundle(s) / APK(s)" then "Build APK(s)"
echo 4. After the build completes, click on "Run" in the top menu
echo 5. Select "Run 'app'" to install and run the app
echo.
echo Press any key to open Android Studio...
pause > nul

start "" "%AS_PATH%"
echo Android Studio is opening. Please follow the steps above.
pause

# FuelTrackerQR Build and Install Script

Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "FuelTrackerQR Build and Install Script" -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

# Check if Android Studio is installed
$androidSdkPath = "$env:LOCALAPPDATA\Android\Sdk"
if (Test-Path $androidSdkPath) {
    Write-Host "Found Android SDK at: $androidSdkPath" -ForegroundColor Green
} else {
    Write-Host "Android SDK not found in default location." -ForegroundColor Yellow
    Write-Host "Please make sure Android Studio is installed." -ForegroundColor Yellow
}

# Find Java
Write-Host "Searching for Java..." -ForegroundColor Cyan
$javaPath = $null

# Check Android Studio JDK
$asJdkPath = "$env:PROGRAMFILES\Android\Android Studio\jbr"
if (Test-Path $asJdkPath) {
    $javaPath = $asJdkPath
    Write-Host "Found Java in Android Studio: $javaPath" -ForegroundColor Green
}

# Check common JDK locations if not found yet
if (-not $javaPath) {
    $jdkLocations = @(
        "$env:PROGRAMFILES\Java\jdk*",
        "$env:PROGRAMFILES\Eclipse Adoptium\jdk*",
        "$env:PROGRAMFILES\Eclipse Foundation\jdk*",
        "$env:PROGRAMFILES\AdoptOpenJDK\jdk*",
        "$env:PROGRAMFILES\Zulu\zulu*"
    )

    foreach ($location in $jdkLocations) {
        $jdkDirs = Get-ChildItem -Path $location -ErrorAction SilentlyContinue
        foreach ($dir in $jdkDirs) {
            if (Test-Path "$($dir.FullName)\bin\java.exe") {
                $javaPath = $dir.FullName
                Write-Host "Found Java at: $javaPath" -ForegroundColor Green
                break
            }
        }
        if ($javaPath) { break }
    }
}

# Check if JAVA_HOME is already set
if (-not $javaPath -and $env:JAVA_HOME) {
    if (Test-Path "$env:JAVA_HOME\bin\java.exe") {
        $javaPath = $env:JAVA_HOME
        Write-Host "Using existing JAVA_HOME: $javaPath" -ForegroundColor Green
    }
}

# Set JAVA_HOME if Java was found
if ($javaPath) {
    Write-Host "Setting JAVA_HOME to: $javaPath" -ForegroundColor Green
    $env:JAVA_HOME = $javaPath
    $env:Path = "$javaPath\bin;$env:Path"
} else {
    Write-Host "Java not found. Will try to use Android Studio's embedded Gradle." -ForegroundColor Yellow
}

# Build the app
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "Building the app..." -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

$buildSuccess = $false

# Try using Gradle wrapper
if (Test-Path "gradlew.bat") {
    Write-Host "Running Gradle wrapper..." -ForegroundColor Cyan
    & .\gradlew.bat --no-daemon assembleDebug
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Build successful!" -ForegroundColor Green
        $buildSuccess = $true
    } else {
        Write-Host "Build failed with Gradle wrapper." -ForegroundColor Red
    }
} else {
    Write-Host "Gradle wrapper not found." -ForegroundColor Yellow
}

# Install the app if build was successful
if ($buildSuccess) {
    Write-Host "===================================================" -ForegroundColor Cyan
    Write-Host "Installing the app..." -ForegroundColor Cyan
    Write-Host "===================================================" -ForegroundColor Cyan
    
    # Try using Gradle wrapper for installation
    if (Test-Path "gradlew.bat") {
        Write-Host "Running Gradle wrapper for installation..." -ForegroundColor Cyan
        & .\gradlew.bat --no-daemon installDebug
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "App installed successfully with Gradle!" -ForegroundColor Green
            Write-Host "===================================================" -ForegroundColor Cyan
            Write-Host "Success! App built and installed." -ForegroundColor Green
            Write-Host "You can now test the profile picture upload functionality." -ForegroundColor Green
            Write-Host "===================================================" -ForegroundColor Cyan
            exit 0
        } else {
            Write-Host "Installation failed with Gradle wrapper." -ForegroundColor Red
        }
    }
}

# Try direct ADB installation as fallback
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "Trying direct ADB installation..." -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

$adbPath = "$androidSdkPath\platform-tools\adb.exe"
if (Test-Path $adbPath) {
    # Check for connected devices
    $devices = & $adbPath devices
    if ($devices -match "device$") {
        Write-Host "Found connected Android device." -ForegroundColor Green
        
        # Install the app
        $apkPath = "app\build\outputs\apk\debug\app-debug.apk"
        if (Test-Path $apkPath) {
            Write-Host "Installing the app on connected device..." -ForegroundColor Cyan
            & $adbPath install -r $apkPath
            
            if ($LASTEXITCODE -eq 0) {
                Write-Host "App installed successfully with ADB!" -ForegroundColor Green
                Write-Host "===================================================" -ForegroundColor Cyan
                Write-Host "Success! App installed." -ForegroundColor Green
                Write-Host "You can now test the profile picture upload functionality." -ForegroundColor Green
                Write-Host "===================================================" -ForegroundColor Cyan
                exit 0
            } else {
                Write-Host "Installation failed with ADB." -ForegroundColor Red
            }
        } else {
            Write-Host "Debug APK not found at: $apkPath" -ForegroundColor Red
            Write-Host "Please check if the build was successful." -ForegroundColor Red
        }
    } else {
        Write-Host "No Android devices connected." -ForegroundColor Red
        Write-Host "Please connect a device and try again." -ForegroundColor Red
    }
} else {
    Write-Host "ADB not found at: $adbPath" -ForegroundColor Red
    Write-Host "Please install the app manually from Android Studio." -ForegroundColor Red
}

# Show alternative method if all else fails
Write-Host "===================================================" -ForegroundColor Red
Write-Host "Error occurred during build or install." -ForegroundColor Red
Write-Host "===================================================" -ForegroundColor Yellow
Write-Host "Alternative method:" -ForegroundColor Yellow
Write-Host "1. Open the project in Android Studio" -ForegroundColor Yellow
Write-Host "2. Click on 'Build' in the top menu" -ForegroundColor Yellow
Write-Host "3. Select 'Build Bundle(s) / APK(s)' then 'Build APK(s)'" -ForegroundColor Yellow
Write-Host "4. After the build completes, click on 'Run' in the top menu" -ForegroundColor Yellow
Write-Host "5. Select 'Run 'app'' to install and run the app" -ForegroundColor Yellow
Write-Host "===================================================" -ForegroundColor Cyan

Write-Host "Press any key to exit..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

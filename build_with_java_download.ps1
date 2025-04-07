# FuelTrackerQR Build and Install Script with Java Download

Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "FuelTrackerQR Build and Install Script" -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

# Check if Java is installed
$javaPath = $null
$javaDownloadNeeded = $false

# Check common locations
$jdkLocations = @(
    "$env:PROGRAMFILES\Java\jdk*",
    "$env:PROGRAMFILES\Android\Android Studio\jbr",
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

# Check if JAVA_HOME is already set
if (-not $javaPath -and $env:JAVA_HOME) {
    if (Test-Path "$env:JAVA_HOME\bin\java.exe") {
        $javaPath = $env:JAVA_HOME
        Write-Host "Using existing JAVA_HOME: $javaPath" -ForegroundColor Green
    }
}

# Download and install Java if needed
if (-not $javaPath) {
    Write-Host "Java not found. Will download and install OpenJDK." -ForegroundColor Yellow
    $javaDownloadNeeded = $true
    
    # Create a temporary directory
    $tempDir = "$env:TEMP\java_download"
    if (-not (Test-Path $tempDir)) {
        New-Item -ItemType Directory -Path $tempDir -Force | Out-Null
    }
    
    # Download OpenJDK
    Write-Host "Downloading OpenJDK..." -ForegroundColor Cyan
    $jdkUrl = "https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip"
    $jdkZip = "$tempDir\openjdk.zip"
    
    try {
        Invoke-WebRequest -Uri $jdkUrl -OutFile $jdkZip
    }
    catch {
        Write-Host "Failed to download OpenJDK: $_" -ForegroundColor Red
        exit 1
    }
    
    # Extract OpenJDK
    Write-Host "Extracting OpenJDK..." -ForegroundColor Cyan
    try {
        Expand-Archive -Path $jdkZip -DestinationPath $tempDir -Force
    }
    catch {
        Write-Host "Failed to extract OpenJDK: $_" -ForegroundColor Red
        exit 1
    }
    
    # Find the extracted JDK directory
    $jdkDirs = Get-ChildItem -Path "$tempDir\jdk*" -Directory
    if ($jdkDirs.Count -gt 0) {
        $javaPath = $jdkDirs[0].FullName
        Write-Host "OpenJDK extracted to: $javaPath" -ForegroundColor Green
    }
    else {
        Write-Host "Failed to find extracted OpenJDK directory." -ForegroundColor Red
        exit 1
    }
}

# Set JAVA_HOME and update PATH
Write-Host "Setting JAVA_HOME to: $javaPath" -ForegroundColor Green
$env:JAVA_HOME = $javaPath
$env:Path = "$javaPath\bin;$env:Path"

# Check if ADB is available
$adbPath = $null
$adbLocations = @(
    "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
    "$env:ANDROID_HOME\platform-tools\adb.exe"
)

foreach ($location in $adbLocations) {
    if (Test-Path $location) {
        $adbPath = $location
        Write-Host "Found ADB at: $adbPath" -ForegroundColor Green
        break
    }
}

if (-not $adbPath) {
    Write-Host "ADB not found. Will try to use Gradle for installation." -ForegroundColor Yellow
}

# Build the app
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "Building the app..." -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

if (Test-Path "gradlew.bat") {
    Write-Host "Running Gradle wrapper..." -ForegroundColor Cyan
    & .\gradlew.bat --no-daemon assembleDebug
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed!" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "Build successful!" -ForegroundColor Green
}
else {
    Write-Host "Gradle wrapper not found." -ForegroundColor Red
    exit 1
}

# Install the app
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "Installing the app..." -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

# Try using Gradle for installation
if (Test-Path "gradlew.bat") {
    Write-Host "Installing with Gradle..." -ForegroundColor Cyan
    & .\gradlew.bat --no-daemon installDebug
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Installation with Gradle failed." -ForegroundColor Yellow
        
        # Try using ADB as fallback
        if ($adbPath) {
            Write-Host "Trying installation with ADB..." -ForegroundColor Cyan
            
            # Check for connected devices
            $devices = & $adbPath devices
            if ($devices -notmatch "device$") {
                Write-Host "No Android devices connected." -ForegroundColor Red
                Write-Host "Please connect a device and try again." -ForegroundColor Red
                exit 1
            }
            
            # Install the app
            $apkPath = "app\build\outputs\apk\debug\app-debug.apk"
            if (Test-Path $apkPath) {
                & $adbPath install -r $apkPath
                
                if ($LASTEXITCODE -ne 0) {
                    Write-Host "Installation with ADB failed." -ForegroundColor Red
                    exit 1
                }
                else {
                    Write-Host "App installed successfully with ADB!" -ForegroundColor Green
                }
            }
            else {
                Write-Host "Debug APK not found." -ForegroundColor Red
                exit 1
            }
        }
        else {
            Write-Host "ADB not found for fallback installation." -ForegroundColor Red
            exit 1
        }
    }
    else {
        Write-Host "App installed successfully with Gradle!" -ForegroundColor Green
    }
}
else {
    Write-Host "Gradle wrapper not found for installation." -ForegroundColor Red
    exit 1
}

Write-Host "===================================================" -ForegroundColor Green
Write-Host "Success! App built and installed." -ForegroundColor Green
Write-Host "===================================================" -ForegroundColor Green
Write-Host "You can now test the profile picture upload functionality." -ForegroundColor Green

Write-Host "Press any key to exit..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

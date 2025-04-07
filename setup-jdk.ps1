# Script to extract and set up JDK from the downloaded zip file

# Path to the downloaded JDK zip file
$jdkZipPath = "C:\Users\dttsi\Downloads\openjdk-17.zip"
$jdkInstallPath = "$PSScriptRoot\jdk"

# Check if the zip file exists
if (-not (Test-Path $jdkZipPath)) {
    Write-Host "Error: JDK zip file not found at $jdkZipPath"
    exit 1
}

# Clean up any previous installation
if (Test-Path $jdkInstallPath) {
    Write-Host "Removing previous JDK installation..."
    Remove-Item $jdkInstallPath -Recurse -Force
}

# Create the JDK directory
Write-Host "Creating JDK directory..."
New-Item -ItemType Directory -Path $jdkInstallPath -Force | Out-Null

# Extract JDK
Write-Host "Extracting JDK from $jdkZipPath..."
Expand-Archive -Path $jdkZipPath -DestinationPath $env:TEMP -Force

# Find the extracted JDK directory
$jdkExtractedDir = Get-ChildItem -Path $env:TEMP -Directory | Where-Object { $_.Name -like "jdk*" -or $_.Name -like "openjdk*" } | Select-Object -First 1

if ($jdkExtractedDir) {
    # Move JDK files to the installation directory
    Write-Host "Installing JDK from $($jdkExtractedDir.FullName) to $jdkInstallPath..."
    Copy-Item -Path "$($jdkExtractedDir.FullName)\*" -Destination $jdkInstallPath -Recurse -Force
    
    # Clean up
    Write-Host "Cleaning up temporary files..."
    Remove-Item $jdkExtractedDir.FullName -Recurse -Force -ErrorAction SilentlyContinue
    
    Write-Host "JDK set up successfully at $jdkInstallPath"
    
    # Set JAVA_HOME and update PATH for this session
    $env:JAVA_HOME = $jdkInstallPath
    $env:PATH = "$jdkInstallPath\bin;$env:PATH"
    
    # Verify Java installation
    Write-Host "Verifying Java installation..."
    try {
        $javaVersion = & "$jdkInstallPath\bin\java" -version 2>&1
        Write-Host "Java installation verified successfully."
        
        # Create a batch file to build with the correct Java
        $buildBatContent = @"
@echo off
set JAVA_HOME=$jdkInstallPath
set PATH=%JAVA_HOME%\bin;%PATH%
echo Using Java from %JAVA_HOME%
call gradlew.bat assembleDebug
if %ERRORLEVEL% EQU 0 (
    echo Build successful! APK is located at: %CD%\app\build\outputs\apk\debug\app-debug.apk
) else (
    echo Build failed with exit code %ERRORLEVEL%
)
"@
        
        Set-Content -Path "$PSScriptRoot\build-with-jdk.bat" -Value $buildBatContent
        Write-Host "Created build-with-jdk.bat script. Run this script to build the app with the extracted JDK."
        
    } catch {
        Write-Host "Error verifying Java installation: $_"
    }
} else {
    Write-Host "Error: Could not find extracted JDK directory in $env:TEMP"
    exit 1
}

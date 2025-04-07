# Script to download and install AdoptOpenJDK
$jdkUrl = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.8_7.zip"
$jdkZip = "$env:TEMP\jdk.zip"
$jdkDir = "$PSScriptRoot\jdk"

# Create directory if it doesn't exist
if (!(Test-Path $jdkDir)) {
    New-Item -ItemType Directory -Path $jdkDir -Force
}

# Download JDK
Write-Host "Downloading JDK..."
Invoke-WebRequest -Uri $jdkUrl -OutFile $jdkZip

# Extract JDK
Write-Host "Extracting JDK..."
Expand-Archive -Path $jdkZip -DestinationPath $env:TEMP -Force

# Find the extracted directory
$extractedDir = Get-ChildItem -Path $env:TEMP -Directory | Where-Object { $_.Name -like "jdk*" } | Select-Object -First 1

if ($extractedDir) {
    # Copy JDK files
    Write-Host "Installing JDK to $jdkDir..."
    Copy-Item -Path "$($extractedDir.FullName)\*" -Destination $jdkDir -Recurse -Force
    
    # Clean up
    Remove-Item $jdkZip -Force
    Remove-Item $extractedDir.FullName -Recurse -Force
    
    Write-Host "JDK installed successfully to $jdkDir"
    Write-Host "Set JAVA_HOME to $jdkDir"
} else {
    Write-Host "Failed to find extracted JDK directory"
}

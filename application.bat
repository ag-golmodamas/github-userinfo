@echo off
setlocal enabledelayedexpansion

set GRADLE_DIST_DIR=local-gradle
set GRADLE_VERSION=7.6

REM Check if we already have a local Gradle distribution
if not exist "%GRADLE_DIST_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat" (
    echo Gradle not found locally.
    echo ---Downloading Gradle %GRADLE_VERSION%---
    
    REM Create directory for local Gradle
    mkdir "%GRADLE_DIST_DIR%" 2>nul
    
    REM Download Gradle zip
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%GRADLE_DIST_DIR%\gradle-%GRADLE_VERSION%-bin.zip'"
    
    echo Extracting Gradle...
    powershell -Command "Expand-Archive -Path '%GRADLE_DIST_DIR%\gradle-%GRADLE_VERSION%-bin.zip' -DestinationPath '%GRADLE_DIST_DIR%' -Force"
)

REM Build the application first
call "%GRADLE_DIST_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat" jar --quiet

if %errorlevel% neq 0 (
    echo Build failed.
    pause
    exit /b 1
)

REM Run the application directly with Java

java -jar build\libs\GitHubUserInfo-1.0-SNAPSHOT.jar

echo.
pause
endlocal 
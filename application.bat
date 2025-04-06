@echo off
setlocal enabledelayedexpansion

set GRADLE_DIST_DIR=local-gradle
set GRADLE_VERSION=7.6

if not exist "%GRADLE_DIST_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat" (
    echo Downloading Gradle %GRADLE_VERSION%
    mkdir "%GRADLE_DIST_DIR%" 2>nul
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%GRADLE_DIST_DIR%\gradle-%GRADLE_VERSION%-bin.zip'"
    powershell -Command "Expand-Archive -Path '%GRADLE_DIST_DIR%\gradle-%GRADLE_VERSION%-bin.zip' -DestinationPath '%GRADLE_DIST_DIR%' -Force"
)

call "%GRADLE_DIST_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat" jar --quiet

if %errorlevel% neq 0 (
    echo Build failed.
    pause
    exit /b 1
)

java -jar build\libs\GitHubUserInfo-1.0-SNAPSHOT.jar

echo.
pause
endlocal 
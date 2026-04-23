@echo off
echo Compiling Aura-Retail-OS...
if not exist bin mkdir bin

REM Using dir to get all java files avoids PowerShell encoding issues with javac
dir /s /B src\*.java > sources.txt

javac -d bin @sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /B 1
)

echo.
echo Compilation successful! Running Main...
echo --------------------------------------------------
java -cp bin Main
pause

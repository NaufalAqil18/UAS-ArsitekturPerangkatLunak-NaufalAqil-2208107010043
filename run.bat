# ==================== WINDOWS (run.bat) ====================
@echo off
echo ====================================
echo  Running Doctor Scheduling App
echo ====================================
echo.

REM Check if bin directory exists
if not exist "bin" (
    echo Error: bin directory not found!
    echo Please compile first using: compile.bat
    pause
    exit /b
)

REM Check if data directory exists
if not exist "data" mkdir data

REM Run the program
java -cp bin DoctorSchedulingApp

pause
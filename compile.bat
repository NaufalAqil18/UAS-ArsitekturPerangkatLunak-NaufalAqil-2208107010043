@echo off
echo ====================================
echo  Compiling Doctor Scheduling App
echo ====================================

REM Create directories if not exist
if not exist "bin" mkdir bin
if not exist "data" mkdir data

REM Compile Java file
echo Compiling...
javac -d bin src/DoctorSchedulingApp.java

if %errorlevel% == 0 (
    echo.
    echo ====================================
    echo  Compilation Successful!
    echo  .class files saved to bin/
    echo ====================================
    echo.
    echo To run the program, use: run.bat
) else (
    echo.
    echo ====================================
    echo  Compilation Failed!
    echo  Please check your source code.
    echo ====================================
)

pause
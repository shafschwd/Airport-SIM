@echo off

:: Create the output directory if it doesn't exist
if not exist out mkdir out

:: Clean previous compilation outputs
del /Q out\*

:: Record the start time
echo Starting compilation...
set start_time=%time%

:: Compile the Java files with UTF-8 encoding
javac -encoding UTF-8 -d out src\main\AirportSimulation.java src\main\controllers\*.java src\main\entities\*.java

:: Check if compilation was successful
if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilation Successful!

    :: Run the Java program
    java -cp out main.AirportSimulation

    :: Skip time calculation for now to avoid parsing issues
    echo.
    echo ⏱️ Simulation completed successfully
) else (
    echo ❌ Compilation Failed. Please check for errors above.
)

pause
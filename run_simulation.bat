@echo off
cls

echo Compiling Java files...
javac -d out src\main\AirportSimulation.java src\main\controllers\*.java src\main\entities\*.java src\main\utils\*.java src\main\resources\*.java

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Compilation Failed. Please check for errors.
    pause
    exit /b
)

echo ✅ Compilation Successful!
echo Running the simulation...
java -cp out AirportSimulation

pause

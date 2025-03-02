@echo off

:: Create the output directory if it doesn't exist
if not exist out mkdir out

:: Clean previous compilation outputs
del /Q out\*

:: Record the start time
echo Starting compilation...
set start_time=%time%

:: Compile the Java files
javac -d out src\main\AirportSimulation.java src\main\controllers\*.java src\main\entities\*.java

:: Check if compilation was successful
if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilation Successful!

    :: Run the Java program
    java -cp out main.AirportSimulation

    :: Calculate and display execution time
    set end_time=%time%

    :: Convert the time strings to centiseconds for easier calculation
    for /F "tokens=1-4 delims=:,. " %%a in ("%start_time%") do (
        set /A start_cs=(((%%a*60)+1%%b %% 100)*60+1%%c %% 100)*100+1%%d %% 100
    )

    for /F "tokens=1-4 delims=:,. " %%a in ("%end_time%") do (
        set /A end_cs=(((%%a*60)+1%%b %% 100)*60+1%%c %% 100)*100+1%%d %% 100
    )

    :: Calculate the elapsed time in centiseconds
    set /A elapsed_cs=end_cs-start_cs
    if %elapsed_cs% lss 0 set /A elapsed_cs+=24*60*60*100

    :: Convert back to a more readable format
    set /A elapsed_s=%elapsed_cs% / 100

    echo.
    echo ⏱️ Total simulation time: %elapsed_s% seconds
) else (
    echo ❌ Compilation Failed. Please check for errors above.
)

pause
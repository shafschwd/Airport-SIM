#!/bin/bash

# Create the output directory if it doesn't exist
mkdir -p out

# Clean previous compilation outputs
rm -rf out/*

# Record the start time
start_time=$(date +%s.%N)

# Compile the Java files - remove the 2>/dev/null to see errors
javac -d out src/main/AirportSimulation.java src/main/controllers/*.java src/main/entities/*.java src/main/utils/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "✅ Compilation Successful!"
    # Run the Java program
    java -cp out main.AirportSimulation

    # Calculate and display the execution time
    end_time=$(date +%s.%N)
    execution_time=$(echo "$end_time - $start_time" | bc)
    printf "\n⏱️ Total execution time: %.2f seconds\n" $execution_time
else
    echo "❌ Compilation Failed. Please check for errors above."
fi
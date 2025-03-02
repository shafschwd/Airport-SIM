#!/bin/bash

# Create the output directory if it doesn't exist
mkdir -p out

# Clean previous compilation outputs
rm -rf out/*

# Record the start time
start_time=$(date +%s.%N)

# Compile the Java files - remove the 2>/dev/null to see errors
javac -d out src/main/AirportSimulation.java src/main/controllers/*.java src/main/entities/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "✅ Compilation Successful!"
    # Run the Java program
    java -cp out main.AirportSimulation

else
    echo "❌ Compilation Failed. Please check for errors above."
fi
#!/bin/bash

# Compile the Java files
javac -d out src/main/AirportSimulation.java src/main/controllers/*.java src/main/entities/*.java src/main/utils/*.java src/main/resources/*.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "✅ Compilation Successful!"
    # Run the Java program
    java -cp out AirportSimulation
else
    echo "❌ Compilation Failed. Please check for errors."
fi

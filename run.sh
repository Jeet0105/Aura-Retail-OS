#!/bin/bash

echo "Compiling Aura-Retail-OS..."
mkdir -p bin

# Find all java files and compile them
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

if [ $? -eq 0 ]; then
    echo ""
    echo "Compilation successful! Running Main..."
    echo "--------------------------------------------------"
    java -cp bin Main
else
    echo "Compilation failed!"
    exit 1
fi

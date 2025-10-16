#!/bin/bash

# Stop on first error
set -e

# Create bin directory if not exists
mkdir -p bin

# Compile all Java files inside src folder into bin
javac -d bin $(find src -name "*.java")

echo "✅ Build successful!"

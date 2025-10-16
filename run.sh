#!/bin/bash

# Stop on first error
set -e

# Build first (optional but handy)
./build.sh

# Run the main class from bin
java -cp bin Main

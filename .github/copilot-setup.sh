#!/bin/bash
# Setup script for Copilot Coding Agent environment
# This runs before the firewall is enabled, allowing access to required repositories

set -e

echo "Setting up Android development environment for Copilot..."

# Ensure we're in the project directory
cd /home/runner/work/Issuetrax/Issuetrax

# Make gradlew executable
chmod +x gradlew

# Pre-download Gradle wrapper and dependencies
# This accesses dl.google.com before the firewall is enabled
echo "Downloading Gradle wrapper and dependencies..."
./gradlew --version

# Download all dependencies and build configuration
echo "Resolving build dependencies..."
./gradlew tasks --no-daemon || echo "Initial tasks resolution completed"

# Pre-download Android Gradle Plugin and other classpath dependencies
echo "Downloading build classpath dependencies..."
./gradlew buildEnvironment --no-daemon || echo "Build environment setup completed"

# Download project dependencies
echo "Downloading project dependencies..."
./gradlew dependencies --no-daemon || echo "Project dependencies downloaded"

echo "Copilot environment setup complete!"
echo "Android Gradle Plugin and dependencies should now be cached."

#!/usr/bin/env bash
# ==============================================================================
# KG Grand Hotel - Quick Startup Script
# Compiles application and launches embedded Apache Tomcat 10.1 on port 8080
# ==============================================================================

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

echo "Building and starting KG Grand Hotel Reservation System on Tomcat 10.1..."
mvn compile exec:java -Dexec.mainClass="com.hotel.TomcatLauncher"

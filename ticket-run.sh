#!/bin/bash

export JAR_FILE=$1

# Validate the JAR file
if [[ ! -f "$JAR_FILE" ]]; then
  echo "Error: JAR file '$JAR_FILE' not found."
  exit 1
fi
cd /etc/singularit/ || exit
java -jar "$JAR_FILE" --spring.config.name=application
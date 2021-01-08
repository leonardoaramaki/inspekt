#!/bin/sh

# Clean and rebuild
./gradlew clean build

# Install at local repository
./gradlew :gradle-plugin:install :compiler:install


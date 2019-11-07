#!/bin/bash
mvn clean package -Dmaven.test.skip=true
mvn com.spotify:dockerfile-maven-plugin:build
#!/bin/bash
mvn clean package -Dmaven.test.skip=true
mvn com.spotify:dockerfile-maven-plugin:build

docker run -it -p 8080:8080 springboot/springboot-docker-002
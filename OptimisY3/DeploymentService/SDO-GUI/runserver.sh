#!/bin/bash

set -e

./clean.sh
export MAVEN_OPTS="-Xmx1024m -Xms512m -XX:PermSize=256m -XX:MaxPermSize=1024m"
mvn clean compile  jetty:run
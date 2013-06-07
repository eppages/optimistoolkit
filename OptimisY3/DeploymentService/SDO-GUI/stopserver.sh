#!/bin/bash
set -e

export MAVEN_OPTS="-Xmx1024m -Xms512m -XX:PermSize=256m -XX:MaxPermSize=1024m"
mvn -o jetty:stop
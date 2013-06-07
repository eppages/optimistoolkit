#!/bin/bash
#replacement=2s/.*/provider=$1/
#sed $replacement ../EcoEfficiencyToolCore/src/main/resources/config.properties > temp
#mv temp ../EcoEfficiencyToolCore/src/main/resources/config.properties
cd ../EcoEfficiencyToolCoreIP
mvn -o clean install
cd ../EcoEfficiencyToolRESTIP
mvn clean -P$1 package cargo:redeploy

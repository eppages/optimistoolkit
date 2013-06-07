#!/bin/bash
cd ../EcoEfficiencyToolCoreSP
mvn clean install
cd ../EcoEfficiencyToolRESTSP
mvn clean -P$1 package cargo:redeploy

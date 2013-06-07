#!/bin/bash

function startSLAServer() {
    echo "optimis sla server Starting..."
    cd optimis-sla-server
    export CATALINA_HOME=`pwd`
    export JAVA_OPTS="-Xms512m -Xmx1024m -XX:MaxPermSize=196m"
    bin/startup.sh
    cd -
}


function isServerStarted() {
    # Set some variables here
    logfile=$2
    pattern=$3
    
    echo "Waiting for $1 to start up..."

    # read each new line as it gets written
    # to the log file
    found=false
    tail -fn10  $logfile | while read line  ; do

    # check each line against our pattern
    echo "$line" | grep -i "$pattern"

    # if a line matches...
    if [ $? = 0 ]; then
         echo "$1 is STARTED"
         exit 0
    fi
    done
}

startSLAServer

#isServerStarted "SLA Server" "optimis-sla-server/logs/catalina.out" "Server startup"

echo "**********************************"
echo "Optimis SLA Server STARTED"
echo "**********************************"



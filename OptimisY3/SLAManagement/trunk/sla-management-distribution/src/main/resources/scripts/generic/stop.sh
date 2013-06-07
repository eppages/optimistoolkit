#!/bin/bash

function stopSLAServer() {
    echo "... stopping optimis sla server"
    cd optimis-sla-server
    export CATALINA_HOME=`pwd`
    bin/shutdown.sh
    echo "... waiting for sla server to shutdown"
    sleep 10
    for p in `ps aux | grep optimis-sla-server | awk '{print $2}'`; do kill -9 $p; done
    cd -
}

stopSLAServer

echo "**********************************"
echo "Optimis SLA Server STOPPED"
echo "**********************************"

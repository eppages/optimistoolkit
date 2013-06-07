#!/bin/bash

function stopLicenseServer(){
        export CATALINA_HOME=`pwd`
        bin/shutdown.sh
        echo "... waiting for license server to shutdown"
        sleep 5
        for p in `ps aux | grep license-server/bin/bootstrap | awk '{print $2}'`; do kill -9 $p; done
}

function startLicenseServer(){
         export JAVA_OPTS="-Xms128m -Xmx512m -XX:MaxPermSize=196m"
         export CATALINA_HOME=`pwd`
         bin/startup.sh
}

if [ "$1" = "start" ]
    then startLicenseServer
  elif [ "$1" = "stop" ]
    then stopLicenseServer
  elif [ "$1" = "restart" ]
    then
        stopLicenseServer
        startLicenseServer
  else
    echo "Usage: $0 start|stop|restart"
fi

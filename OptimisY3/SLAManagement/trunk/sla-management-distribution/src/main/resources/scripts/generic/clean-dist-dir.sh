#!/bin/sh

if [ -z $1 ] ; then
    echo "need to supply the distribution directory, usually optimis-sla-x.y.z"
    exit -1
fi

cleanSLAServer() {
    cd optimis-sla-server
    
    rm -f logs/*
    rm -rf work/*
    rm -rf temp/*
    rm -rf optimis-sla-storage/*
    cd - > /dev/null
}

cd $1

cleanSLAServer

#!/bin/bash

BASEDIR=$1
TIMESTAMP=$2
OPTIMISSERVER=$(echo $3 | tr '[:lower:]' '[:upper:]')
CURTARGET=$4

source $BASEDIR/hppdu.properties

function get_params {
   snmpget -v 2c -c public $REMOTESERVER $1 | cut -d " " -f 4
}

echo "$OPTIMISSERVER|real_power|$(get_params $CURTARGET)|W|$TIMESTAMP"


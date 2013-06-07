#!/bin/bash

BASEDIR=$1
TIMESTAMP=$2
OPTIMISSERVER=$(echo $3 | tr '[:lower:]' '[:upper:]')
CURTARGET=$4

source $BASEDIR/flexiantpdu.properties

function get_params {
   INTENSITY=$(snmpwalk -c public -v 1 $REMOTESERVER $1 | cut -d ":" -f 4 | sed "s/^ //")
   REALPOWER=$(( $INTENSITY * $VOLTAGE / 10 ))
   echo $REALPOWER
}

echo "$OPTIMISSERVER|real_power|$(get_params $CURTARGET)|W|$TIMESTAMP"


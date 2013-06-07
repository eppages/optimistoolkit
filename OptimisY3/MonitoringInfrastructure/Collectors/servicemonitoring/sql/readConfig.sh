#!/bin/bash

# Read config properties (USERNAME, PASSWORD, DATABASE, TABLE)
# Used by other scripts to populate values

SQLSCRIPT_DIR=/opt/optimis/modules/service-monitoring/sql
PROP_FILE=$SQLSCRIPT_DIR/mysql.properties

if [ ! -f "$PROP_FILE" ]; then
    echo "MySQL properties not found, trying local dir"
    SQLSCRIPT_DIR=../src/main/resources/
    PROP_FILE=$SQLSCRIPT_DIR/mysql.properties
fi

if [ ! -f "$PROP_FILE" ]; then
    echo "Local MySQL properties not found, exiting"
    exit -1
fi

USERNAME=`sed '/^\#/d' ${PROP_FILE} | grep 'username'  | tail -n 1 | sed 's/^.*=//;s/^[[:space:]]*//;s/[[:space:]]*$//'`
PASSWORD=`sed '/^\#/d' ${PROP_FILE} | grep 'password'  | tail -n 1 | sed 's/^.*=//;s/^[[:space:]]*//;s/[[:space:]]*$//'`
DATABASE=`sed '/^\#/d' ${PROP_FILE} | grep 'database'  | tail -n 1 | sed 's/^.*=//;s/^[[:space:]]*//;s/[[:space:]]*$//'`
TABLE=`sed '/^\#/d' ${PROP_FILE} | grep 'table'  | tail -n 1 | sed 's/^.*=//;s/^[[:space:]]*//;s/[[:space:]]*$//'`

ERRORFLAG=0

if [ "${USERNAME:-notdefined}" = "notdefined" ];then
   echo "username property not found in config"
   ERRORFLAG=1
fi 

if [ "${PASSWORD:-notdefined}" = "notdefined" ];then
   echo "password property not found in config"
   ERRORFLAG=1
fi 

if [ "${DATABASE:-notdefined}" = "notdefined" ];then
   echo "database property not found in config"
   ERRORFLAG=1
fi 

if [ "${TABLE:-notdefined}" = "notdefined" ];then
   echo "table property not found in config"
   ERRORFLAG=1
fi 

if [ "${ERRORFLAG}" = 1 ];then
   echo "Aborting." 
   exit -1
fi 

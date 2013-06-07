#!/bin/bash
#
# Written by Daniel Henriksson <danielh@cs.umu.se> 2011
#
#

#Default dir and file names
JARFILEDIR=target
JARFILENAMEPATTERN=ElasticityEngine-0.0.5-SNAPSHOT.jar

#Arguments to be passed to java
MANIFESTFILE=$1
LOWRISK=$2

#Verify that the dir with the jar file exists
if [ ! -e "$JARFILEDIR" ]; then
    echo "No '$JARFILEDIR' directory found, please make sure the directory exists."
    exit -1
fi

#Look for a suitable filename (ignoring any irritating version numbers)
FILE=`ls $JARFILEDIR/$JARFILENAMEPATTERN 2> /dev/null`

#Make sure its found
if [ ! -e "$FILE" ]; then
    echo "Could not find any suitable .jar file to execute in '$JARFILEDIR' directory."
    exit -1
fi

#Define help messages
printUsage(){
    script=$0
    echo "Usage: "
    echo -e "\t$script <manifest file>" 
    echo "Example:"
    echo -e "\t$script manifests/service_manifest.xml" 
}

#Print help if amount of arguments is not 2
if [ $# -ne 2 ]
then
    printUsage
    exit 1
fi

#Verify that the manifest file exists
if [ ! -e "$MANIFESTFILE" ]; then
    echo "Could not find any file at $MANIFESTFILE"
    exit -1
fi

#Create full paths for configuration and manifest file
FULLMANIFESTPATH=$(readlink -f $MANIFESTFILE)

#echo "Full manifestpath: $FULLMANIFESTPATH"

#Execute the default target of the jar file
java -DfakeMonitoring=true -Dlowrisk=$LOWRISK -Dlog4j.configuration=log4j_silent.properties -jar $FILE $FULLMANIFESTPATH
#java -DfakeMonitoring=false -Dlog4j.configuration=log4j_silent.properties -jar $FILE $FULLMANIFESTPATH


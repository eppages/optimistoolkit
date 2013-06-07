#!/bin/sh

if [ $# -lt 4 ]
then
echo
echo "Usage:"
echo "./run.sh project_file resources_file app_classpath full_application_name app_params"
exit 127
fi

# Descriptors for resources and project
projFile=$1
resFile=$2

shift 2

# Application-related parameters
appClassPath=$1
fullAppPath=$2

shift 2


# Go to working dir
userDir=~
scriptDir=`pwd`
workingDir=$userDir/IT/$fullAppPath
mkdir -p $workingDir
cd $workingDir
mkdir -p jobs

# Export jvm properties to be set by env.sh
export projFile
export resFile
export fullAppPath
export appClassPath
. $scriptDir/env.sh


echo -e "\n----------------- Running $fullAppPath --------------------------\n"
  
# Run the application
$JAVACMD $fullAppPath $*

echo
echo ------------------------------------------------------------

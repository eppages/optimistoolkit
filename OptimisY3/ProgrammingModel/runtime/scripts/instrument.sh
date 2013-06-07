#!/bin/sh

if [ $# -lt 4 ]
then
echo
echo "Usage:"
echo "./instrument.sh full_application_name app_classpath servicess_inst_dir dest_dir"
exit 127
fi

userDir=~
scriptDir=`pwd`

fullAppName=$1
appClassPath=$2
runtimeInstDir=$3
destDir=$4

libDir=$runtimeInstDir/integratedtoolkit/lib/

echo -e "\n----------------- Instrumenting $fullAppName --------------------------\n"

cd $destDir

runtimeClassPath=$libDir/IT.jar:$libDir/log4j/log4j-1.2.15.jar:$libDir/javassist/javassist.jar

$JAVA_HOME/bin/java \
-Dlog4j.configuration=$runtimeInstDir/log/it-log4j \
-Dit.lib=$libDir \
-Dit.to.file=true \
-classpath $appClassPath:$runtimeClassPath \
integratedtoolkit.loader.ITAppLoader total "" $fullAppName 

echo
echo ------------------------------------------------------------

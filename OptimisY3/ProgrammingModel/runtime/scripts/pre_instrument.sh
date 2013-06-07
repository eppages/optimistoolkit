#!/bin/sh

if [ $# -lt 4 ]
then
echo
echo "Usage:"
echo "./pre_instrument.sh app_classpath servicess_inst_dir dest_dir full_class_name method_labels..."
exit 127
fi

userDir=~
scriptDir=`pwd`

appClassPath=$1
runtimeInstDir=$2
destDir=$3
fullAppName=$4
shift 4
libDir=$runtimeInstDir/integratedtoolkit/lib/

echo -e "\n----------------- Instrumenting $fullAppName --------------------------\n"

cd $destDir

runtimeClassPath=$libDir/IT.jar:$libDir/log4j/log4j-1.2.15.jar:$libDir/javassist/javassist.jar

$JAVA_HOME/bin/java \
-Dlog4j.configuration=$runtimeInstDir/log/it-log4j \
-Dit.lib=$libDir \
-Dit.to.file=true \
-classpath $appClassPath:$runtimeClassPath \
integratedtoolkit.loader.AddOrchestration $fullAppName $@ 

echo
echo ------------------------------------------------------------

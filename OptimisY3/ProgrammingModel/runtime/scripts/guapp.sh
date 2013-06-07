#!/bin/sh

if [ $# -lt 2 ]
then
echo
echo "Usage:"
echo "./guapp.sh execution_mode project_file resources_file loader full_application_name params"
exit 127
fi

userDir=~
scriptDir=`pwd`

mode=$1
shift 1
workercp=$1
projFile=$2
resFile=$3
loader=$4
shift 4 # $* == loader + full_app_name + params 
fullAppPath=$1

if [ $mode != sequential ] && [ $mode != IT ] && [ $mode != CLOUD ]
then
echo
echo "Execution mode $mode is not valid"
echo "Please, choose either IT or sequential"
exit 127
fi

echo -e "\n----------------- Executing $fullAppPath in $mode mode $loader--------------------------\n"



workingDir=$userDir/IT/$fullAppPath
mkdir -p $workingDir/jobs
cd $workingDir

export workercp
export projFile
export resFile
export fullAppPath
. $scriptDir/env.sh


if [ $mode = sequential ]
then
# $* == full_app_name + params 
#$JAVA_HOME/bin/java -Xint $*
#$JAVA_HOME/bin/java $*
shift 1
$JAVA_HOME/bin/java -Xshare:off $*
else
# $* == loader + full_app_name + params 
#$JAVACMD -Xshare:off integratedtoolkit.loader.ITAppLoader $*
$JAVACMD integratedtoolkit.loader.ITAppLoader $loader $workercp $*
#$JAVACMD -Xrunhprof:cpu=samples,depth=7,thread=y,file=/home/flordan/hprof integratedtoolkit.loader.ITAppLoader $CLASSPATH $*
#$JAVACMD $*
fi


echo
echo ------------------------------------------------------------

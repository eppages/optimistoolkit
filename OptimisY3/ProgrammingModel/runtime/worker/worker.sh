#!/bin/sh
echo $*

cp=$1
working_dir=$2
shift 2
cd ${working_dir}

rmfiles_num=$1
shift 1

for (( i=0;i<$rmfiles_num;i++)); do
 echo removing $1
 rm -f $1
 shift 1 
done

if [ $# -eq 0 ]
then
echo "no parameters to run any method"
exit 0
fi


debug=$1
app=$2
method=$3
nparams=$4
shift 4
if [ $# -eq 0 ]
then                                                                                                                                                                                
exit 0;                                                                                                                                                                             
fi  
params=$*

echo WorkingDir $working_dir
echo RemoveFiles $rmfiles_num
echo debug $debug
echo app $app
echo method $method
echo nparams $nparams
echo params $params

JAVA_HOME=${JAVA_HOME-NULL};
if [ "$JAVA_HOME" = "NULL" ]
then
echo 1>&2
echo 1>&2 "Enviroment variable JAVA_HOME not set"
echo 1>&2 "Please set it and rebuild this worker script"
exit 7
fi

if [ ! -d ${working_dir} ]
then
        /bin/mkdir -p ${working_dir}
fi


add_to_classpath () {
	DIRLIBS=${1}/*.jar
	for i in ${DIRLIBS}
	do
		if [ "$i" != "${DIRLIBS}" ] ; then
			CLASSPATH=$CLASSPATH:"$i"
		fi
	done
}

current_dir=`dirname $0`
add_to_classpath "$current_dir"
add_to_classpath "$current_dir/lib"

cd ${working_dir}
$JAVA_HOME/bin/java -Xms128m -Xmx2048m -classpath $cp:`dirname $0`:$CLASSPATH integratedtoolkit.worker.Worker $debug $app $method $nparams $params

if [ $? -eq 0 ]
then
exit 0
else
echo 1>&2 "Application method failed"
exit 7
fi

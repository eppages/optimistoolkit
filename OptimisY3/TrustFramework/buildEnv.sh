#/bin/bash

#set -x

function usage {
	echo "usage:buildEnv.sh environment layer"
	echo "example:buildEnv.sh TST IP"
	echo "example:buildEnv.sh TST SP"
	echo "example:buildEnv.sh INT IP"
	echo "example:buildEnv.sh INT SP"
	exit 1
}

confPath=../optimis-parent/conf/

#if [ "$#" -lt "2" ]; then
#   usage
#fi

if [ "$1" == "TST" ]; then
	if [ "$2" == "IP" ]; then
        	# Use TST profile over 3 IPs
       		for (( i=1; i<=3; i++ ))
        	do
                	cp ${confPath}IPenvironment${i}.TST.properties ${confPath}environment.properties
	                #mvn -PTST package redeploy
			echo TST IP: $i
	        done
	else
	        if [ "$2" == "SP" ]; then
	                # Use TST profile over SP
	                cp ${confPath}SPenvironment.TST.properties ${confPath}environment.properties
	                #mvn -PTST package redeploy
			echo TST SP
	        else
	                echo No layer provided
			echo
	                usage
	        fi

	fi
else
 	if [ "$1" == "INT" ]; then
        	 if [ "$2" == "IP" ]; then
	                 # Use INT profile over 3 IPs
	                 for (( i=1; i<=3; i++ ))
	                 do
	                         cp ${confPath}IPenvironment${i}.INT.properties ${confPath}environment.properties
	                         #mvn -PINT package redeploy
				 echo INT IP: $i
	                 done
	         else
	                 if [ "$2" == "SP" ]; then
	                         # Use INT profile over SP
	                         cp ${confPath}SPenvironment.INT.properties ${confPath}environment.properties
	                         #mvn -PINT package redeploy
				 echo INT SP
	                 else
	                        echo No layer provided
				echo
				usage
	                 fi
	         fi
 	else
        	 echo No environment profile found
		 echo
		 usage
	fi
fi


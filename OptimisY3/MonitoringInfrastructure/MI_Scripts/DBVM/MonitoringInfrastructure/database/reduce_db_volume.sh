#!/bin/bash

###
# Script for MI: replace old values with a single mean value 
# Version: 1.0
# Date: 2012-11-30
# Author: Tinghe Wang, Pierre Gilet
#
# $1 = start date (yyyy-mm)
# $2 = decrement parameter
# Step 1: calculate mean value
# Step 2: insert new mean value
# Step 3: delete old values

# Mean values are calculated for the tables monitoring_resource_energy,  monitoring_resource_physical, monitoring_resource_virtual, and
# for the parameters listed in reduce_db_volume.properties.

# Example with the cpu_speed parameter stored in the monitoring_resource_physical table.
# Let's suppose that start-date = 2012-12 and decrement-parameter = 6.
# Step 1: calculate mean value of cpu_speed for month 2012-11.
# Step 2: insert a new record into monitoring_resource_physical with the new mean value.
# Step 3: delete from monitoring_resource_physical all the other cpu_speed records having a timestamp belonging to 2012-11.
# Go back to step 1 and run the same process for month 2012-10, and continue so until 2012-06.

if [ ! $# -eq 2 ]
then 
	echo "Usage: reduce_db_volume.sh [start-date in yyyy-mm] [decrement-parameter]"
	exit 1
else 
	DATEOFFSET=$1
        DECREMENT=$2
fi

TMP=/tmp/$$
BASEDIR=$(dirname $0)

YEAR_ORIG=$(echo $DATEOFFSET | awk -F "-" '{print $1}')
MONTH_ORIG=$(echo $DATEOFFSET | awk -F "-" '{print $2}' | sed "s/^0//")

source $BASEDIR/../share/database.properties
source $BASEDIR/reduce_db_volume.properties

# Uncomment to debug.
# set -xv

function run_process {
COUNTER=0
YEAR=$YEAR_ORIG
MONTH=$MONTH_ORIG

case "$1" in
energy)   metrics=("${energy_metrics[@]}")
          SQLTEMPLATEFILE=$BASEDIR/cal_mean_procedure_energy_physical.sql
          ;;
physical) metrics=("${physical_metrics[@]}")
          SQLTEMPLATEFILE=$BASEDIR/cal_mean_procedure_energy_physical.sql
          ;;
virtual)  metrics=("${virtual_metrics[@]}")
          SQLTEMPLATEFILE=$BASEDIR/cal_mean_procedure_virtual.sql
          ;;
*) echo   "Impossible to reach this location in the code."
          SQLTEMPLATEFILE="no_file"
          ;;
esac

while [ $COUNTER -lt $DECREMENT ] ; do
        MONTH=$(( $MONTH-1 ))
	if [ $MONTH -eq 0 ]; then
		MONTH=12
		YEAR=$(( $YEAR-1 ))
	fi
	for (( i=0 ; i < ${#metrics[@]} ; i++ )) do
	   sed -e "s/\[MONTH\]/$MONTH/" -e "s/\[YEAR\]/$YEAR/" -e "s/\[METRICNAME\]/${metrics[$i]}/" -e "s/\[TABLENAME\]/monitoring_resource_$1/g" \
           $SQLTEMPLATEFILE > $TMP.cal_mean_$1.sql
	   mysql -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE <$TMP.cal_mean_$1.sql
	done		
	let COUNTER=COUNTER+1
done
}

run_process energy
run_process physical
run_process virtual

rm -f $TMP.* 2>/dev/null
exit 0


#!/bin/bash

###
# Archive and delete monitoring data older than x months 
# Version: 1.0
# Date: 2012-11-19
# Author: Tinghe Wang
#
# Step 1: set archive months
# Step 2: mysql procedure: query data for extraction
# Step 3: output in gzip
# Step 4: mysql procedure: delete data
# Step 5: remove archive files older than x months
###

if [ ! $# -eq 1 ]
then 
   echo "Usage: archive_old_mdata.sh [offset-in-months]"
   exit 1
else
   OFFSETINMONTHS=$1
fi

TMP=/tmp/$$
BASEDIR=$(dirname $0)
DATE=$(date +"%Y%m%d_%H%M%S")
ARCHI_SERVICE=service-$DATE.txt
ARCHI_VIRTUAL=virtual-$DATE.txt
ARCHI_PHYSICAL=physical-$DATE.txt
ARCHI_ENERGY=energy-$DATE.txt
ARCHI_GZIP=$BASEDIR/archive
source $BASEDIR/../share/database.properties

function run_process 
{
    sed -e "s/\[ARGUMENT\]/$OFFSETINMONTHS/" -e "s/\[TABLENAME\]/$1/g" -e "s/\[FILENAME\]/$2/" $BASEDIR/query_procedure.sql > $TMP.query_$1.sql;
    sed -e "s/\[ARGUMENT\]/$OFFSETINMONTHS/" -e "s/\[TABLENAME\]/$1/g" $BASEDIR/del_procedure.sql > $TMP.del_$1.sql
    mysql -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE <$TMP.query_$1.sql
    gzip /tmp/$2
    mv /tmp/$2.gz $ARCHI_GZIP
    mysql -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE <$TMP.del_$1.sql
}

# Archive and delete.
run_process monitoring_resource_service $ARCHI_SERVICE
run_process monitoring_resource_virtual $ARCHI_VIRTUAL
run_process monitoring_resource_physical $ARCHI_PHYSICAL
run_process monitoring_resource_energy $ARCHI_ENERGY

# Delete old archive files.
OFFSETINDAYS=$(( $OFFSETINMONTHS * 30 ))
find $ARCHI_GZIP -iname '[energy|service|physical|virtual]*.gz' -mtime +$OFFSETINDAYS -delete

rm -f $TMP.* 2>/dev/null

exit 0


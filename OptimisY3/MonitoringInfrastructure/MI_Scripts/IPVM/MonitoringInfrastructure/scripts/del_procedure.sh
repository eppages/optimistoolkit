#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 1.0
# Date: 2011-03-28
#
# Description:
# Delete rows from monitoring_resource_*.
# param: offset-in-months. The stored proc deletes rows with timestamp <= current date - offset-in-months.
#
###

if [ ! $# -eq 1 ]
then
   echo "Usage: del_procedure.sh [offset-in-months]"
   exit 1
else
   OFFSETINMONTHS=$1
fi

TMP=/tmp/$$
BASEDIR=`echo $0 | sed "s/\/del_procedure\.sh//"`
source $BASEDIR/../share/database.properties

sed "s/\[ARGUMENT\]/$OFFSETINMONTHS/" $BASEDIR/del_procedure.sql > $TMP.del_procedure.sql

sed "s/\[TABLENAME\]/monitoring_resource_energy/"   $TMP.del_procedure.sql > $TMP.del_procedure_energy.sql
sed "s/\[TABLENAME\]/monitoring_resource_physical/" $TMP.del_procedure.sql > $TMP.del_procedure_physical.sql
sed "s/\[TABLENAME\]/monitoring_resource_virtual/"  $TMP.del_procedure.sql > $TMP.del_procedure_virtual.sql
sed "s/\[TABLENAME\]/monitoring_resource_service/"  $TMP.del_procedure.sql > $TMP.del_procedure_service.sql

mysql -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE <$TMP.del_procedure_energy.sql
mysql -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE <$TMP.del_procedure_physical.sql
mysql -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE <$TMP.del_procedure_virtual.sql
mysql -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE <$TMP.del_procedure_service.sql

rm -f $TMP.* 2>/dev/null

exit 0


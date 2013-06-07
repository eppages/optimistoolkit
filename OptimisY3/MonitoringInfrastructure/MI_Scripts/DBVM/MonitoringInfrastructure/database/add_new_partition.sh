#!/bin/bash

###
# Add a new partition to the monitoring tables.
# Version: 1.0
# Date: 2013-28-02
# Author: Pierre Gilet
#
###

BASEDIR=`echo $0 | sed "s/\/add_new_partition\.sh//"`
TMP=/tmp/$$
source $BASEDIR/../share/database.properties

function add_new_part {

TABLENAME=$1
echo "################"
echo Table: $TABLENAME

echo "SHOW CREATE TABLE $TABLENAME;" > $TMP.check_latest_partition.sql

mysql -s -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE < $TMP.check_latest_partition.sql > $TMP.check_latest_partition.out

cat $TMP.check_latest_partition.out

MAXPARTNUM=$(grep -o "part[0-9]*" $TMP.check_latest_partition.out | tail -1 | sed "s/^part//")
echo Current max partition number: ${MAXPARTNUM}
NEXTPARTNUM=$(( $MAXPARTNUM + 1 ))
echo Next max partition number: $NEXTPARTNUM

MAXTIMESTAMP=$(grep -o "VALUES LESS THAN ([0-9]*)" $TMP.check_latest_partition.out | tail -1 | sed "s/^VALUES LESS THAN (//" | sed "s/)$//")
echo Current max timestamp: ${MAXTIMESTAMP}

MAXDATE=$(awk "BEGIN { print strftime(\"%Y-%m-%d\", $MAXTIMESTAMP) }")
MAXYEAR=$(echo $MAXDATE | cut -d "-" -f 1)
MAXMONTH=$(echo $MAXDATE | cut -d "-" -f 2)
MAXDAY=$(echo $MAXDATE | cut -d "-" -f 3)
echo Current max date: ${MAXYEAR}-${MAXMONTH}-${MAXDAY}

if [ "$MAXMONTH" -eq "12" ]
then
   NEXTMONTH=1
   NEXTYEAR=$(( $MAXYEAR + 1 ))
else
   NEXTMONTH=$(( $MAXMONTH + 1 ))
   NEXTYEAR=$MAXYEAR
fi

echo Next max date: ${NEXTYEAR}-${NEXTMONTH}-1

NEXTTIMESTAMP=$(date --date="${NEXTYEAR}-${NEXTMONTH}-1 00:00:00" +"%s")
echo Next max timestamp: $NEXTTIMESTAMP

# Check if the current max timestamp of the table is in the past.
# Only in this case is this necessary to create any new partition.
CURRENTSYSTEMTIMESTAMP=$(date +"%s")
echo Current system timestamp: $CURRENTSYSTEMTIMESTAMP

if [ "$MAXTIMESTAMP" -lt "$CURRENTSYSTEMTIMESTAMP" ]
then
   echo "ALTER TABLE $TABLENAME DROP PARTITION part$MAXPARTNUM;"                                              > $TMP.add_new_partition.sql
   echo "ALTER TABLE $TABLENAME ADD PARTITION (PARTITION part$MAXPARTNUM VALUES LESS THAN ($NEXTTIMESTAMP));" >> $TMP.add_new_partition.sql
   echo "ALTER TABLE $TABLENAME ADD PARTITION (PARTITION part$NEXTPARTNUM VALUES LESS THAN MAXVALUE);"        >> $TMP.add_new_partition.sql

   echo About to create new partition...
   cat $TMP.add_new_partition.sql
   mysql -s -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE < $TMP.add_new_partition.sql

   # ADDNEWPART stays TRUE to check if an additional new partition should be created.
   ADDNEWPART=TRUE
else
   echo "Current max timestamp in the table is in the future. It is therefore not needed to create any new partition."
   # ADDNEWPART becomes FALSE to exit from the WHILE loop used in the main section.
   ADDNEWPART=FALSE
fi

rm -f $TMP.* 2>/dev/null

}

for TABLE in monitoring_resource_energy monitoring_resource_physical monitoring_resource_service monitoring_resource_virtual
do
   ADDNEWPART=TRUE
   # Add new partitions as long as the max current timestamp of the table is in the past.
   while [ "$ADDNEWPART" = "TRUE" ]
   do
      add_new_part $TABLE
   done
done

exit 0


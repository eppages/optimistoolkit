#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 1.3
# Date: 2013-03-12
# $Id$
#
# Description:
# Run various checks to assess health of the Monitoring Infrastructure.
# Send a notification email if something wrong was detected.
#
###

TMP=/tmp/$$
BASEDIR=`echo $0 | sed "s/\/check_data_files\.sh//"`
source $BASEDIR/../share/database.properties
source $BASEDIR/check_data_files.properties

# Check if too many files are in the var directory (this is a bad sign meaning that something broke down in the MI).
ENERGYFILECOUNT=`ls -1 $BASEDIR/../var/energy/ | wc -l`
PHYSICALFILECOUNT=`ls -1 $BASEDIR/../var/physical/ | wc -l`
SERVICEFILECOUNT=`ls -1 $BASEDIR/../var/service/ | wc -l`
VIRTUALFILECOUNT=`ls -1 $BASEDIR/../var/virtual/ | wc -l`

if [ $ENERGYFILECOUNT -ge 25 ]
then
   echo "Count of files waiting in energy directory greater than 15. Current count: $ENERGYFILECOUNT. Something must have broken down in the MI. Check the logs in $BASEDIR/../logs" > $TMP.errortext
fi

if [ $PHYSICALFILECOUNT -ge 25 ]
then
   echo "Count of files waiting in physical directory greater than 15. Current count: $PHYSICALFILECOUNT. Something must have broken down in the MI. Check the logs in $BASEDIR/../logs" >> $TMP.errortext
fi

if [ $SERVICEFILECOUNT -ge 25 ]
then
   echo "Count of files waiting in service directory greater than 15. Current count: $SERVICEFILECOUNT. Something must have broken down in the MI. Check the logs in $BASEDIR/../logs" >> $TMP.errortext
fi

if [ $VIRTUALFILECOUNT -ge 25 ]
then
   echo "Count of files waiting in virtual directory greater than 15. Current count: $VIRTUALFILECOUNT. Something must have broken down in the MI. Check the logs in $BASEDIR/../logs" >> $TMP.errortext
fi

# Check if there is something wrong with curl. If such is the case, Tomcat is most likely down or malfunctioning.
LOGFILES=$(ls -1 $BASEDIR/../logs/*.log | grep -v "check_data_files.log")

CURLOCCUR=0
for i in $LOGFILES; do CURLOCCUR=$(( `grep -ic "curl: " $i` + $CURLOCCUR )); done

if [ $CURLOCCUR -ne 0 ]; then echo "There is something wrong with curl. Errors were detected in $BASEDIR/../logs. Tomcat is most likely down or malfunctioning." >> $TMP.errortext; fi

# Check if the string "Timeout: " can be found in the logs. Would be a bad sign if such were the case.
TIMEOUTOCCUR=0
for i in $LOGFILES; do TIMEOUTOCCUR=$(( `grep -ic "Timeout: " $i` + $TIMEOUTOCCUR )); done

if [ $TIMEOUTOCCUR -ne 0 ]; then echo "The string 'Timeout: ' (or possibly 'timeout: ') was found in one or more log files. Please, check the logs in $BASEDIR/../logs. Possible issue: the PDU does not respond to calls anymore." >> $TMP.errortext; fi

# Check if there is something wrong with RabbitMQ.
RABBITMQCOUNT=$(pgrep -f "rabbitmq-server" | wc -l)

if [ $RABBITMQCOUNT -eq 0 ]; then echo "There is something wrong with RabbitMQ. Apparently, rabbitmq-server is down (or maybe partly down). Restart it manually by running 'service rabbitmq-server restart'." >> $TMP.errortext; fi

# Check database connection.
mysql -s -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE <<END >$TMP.check_db.out
SELECT 'Hello world';
END

CHECKDBCONNECT=$(grep -c "Hello world" $TMP.check_db.out)

if [ $CHECKDBCONNECT -ne 1 ]; then echo "There is something wrong with MySQL. Apparently, the connection to the monitoring database is down (-h $SQLHOST -u $SQLUSER -D $SQLDATABASE)." >> $TMP.errortext; fi

# Send notification email if errors were found.
if [ -e "$TMP.errortext" ]
then
   echo "Information about the IPVM where the problem occurred." >> $TMP.errortext
   echo "HOSTNAME = $HOSTNAME"                                   >> $TMP.errortext 
   echo "ifconfig = "                                            >> $TMP.errortext
   $IFCONFIGUTILITY                                              >> $TMP.errortext
   echo "Message sent by the Monitoring Infrastructure."         >> $TMP.errortext
   cat $TMP.errortext
   if [ "$EMAILNOTIF" = "TRUE" ] ; then mail -s "Possible failure in OPTIMIS Monitoring Infrastructure" "$RECIPIENTS" < $TMP.errortext ; fi
fi

rm -f $TMP.* 2>/dev/null
exit 0


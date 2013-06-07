#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 1.2
# Date: 2013-05-06
#
# Description:
# Compute trough time of previous day based on Downstream and Upstream figures
# stored in the Monitoring database.
#
###

TMP=/tmp/$$
BASEDIR=`echo $0 | sed "s/\/get_trough_time\.sh//"`
source $BASEDIR/../share/database.properties
source $BASEDIR/get_trough_time.properties

echo "<?xml version='1.0'?><MonitoringResources>" > $TMP.collector_output.xml

for NODE in $NODELIST
do
echo "SELECT DATE_FORMAT(metric_timestamp, '%Y%m%d%H%i'), SUM(metric_value) AS timesum"             > $TMP.$NODE
echo "FROM monitoring_resource_physical"                                                                     >> $TMP.$NODE
echo "WHERE"                                                                                        >> $TMP.$NODE
echo "   metric_name IN ('Downstream', 'Upstream') AND"                                             >> $TMP.$NODE
echo "   physical_resource_id = '$NODE' AND"                                                        >> $TMP.$NODE
echo "   DATE_FORMAT(metric_timestamp, '%Y%m%d') = SUBDATE(CURDATE(), INTERVAL $INTERVAL DAY)"      >> $TMP.$NODE
echo "GROUP BY DATE_FORMAT(metric_timestamp, '%Y%m%d%H%i')"                                         >> $TMP.$NODE
echo "ORDER BY timesum ASC"                                                                         >> $TMP.$NODE
echo "LIMIT 1;"                                                                                     >> $TMP.$NODE

mysql -s -h $SQLHOST -u $SQLUSER -p$SQLPASSWORD -D $SQLDATABASE <$TMP.$NODE >$TMP.$NODE.result

if [ -s $TMP.$NODE.result ]
then
   METRIC_VALUE=$(cat $TMP.$NODE.result | tr -s "\t" "|" | cut -d \| -f 2)
   RAW_TIMESTAMP=$(cat $TMP.$NODE.result | tr -s "\t" "|" | cut -d \| -f 1)
   UNIX_TIMESTAMP=$(date --utc --date "${RAW_TIMESTAMP:0:4}-${RAW_TIMESTAMP:4:2}-${RAW_TIMESTAMP:6:2} ${RAW_TIMESTAMP:8:2}:${RAW_TIMESTAMP:10:2}:00" +%s)
else
   METRIC_VALUE=0
   UNIX_TIMESTAMP=$(date --utc +%s)
fi

echo "<monitoring_resource>"\
"<physical_resource_id>$NODE</physical_resource_id>"\
"<metric_name>trough_time</metric_name>"\
"<metric_value>$METRIC_VALUE</metric_value>"\
"<metric_unit>Kbps</metric_unit>"\
"<metric_timestamp>$UNIX_TIMESTAMP</metric_timestamp>"\
"<service_resource_id></service_resource_id>"\
"<virtual_resource_id></virtual_resource_id>"\
"<resource_type>physical</resource_type>"\
"<monitoring_information_collector_id>005</monitoring_information_collector_id>"\
"</monitoring_resource>" >> $TMP.collector_output.xml

done

echo "</MonitoringResources>" >> $TMP.collector_output.xml

if [ -s "$TMP.collector_output.xml" ]
then
   # Debug
   cat $TMP.collector_output.xml

   curl -X POST -d @$TMP.collector_output.xml -H "Content-Type:text/plain" http://${VMHOST}:8080/Aggregator/Aggregator/monitoringresources/$RESOURCETYPE
fi

rm -f /tmp/$$.*


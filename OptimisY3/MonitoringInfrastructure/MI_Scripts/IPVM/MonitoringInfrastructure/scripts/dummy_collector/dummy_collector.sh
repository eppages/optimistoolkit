#!/bin/bash
###
#
# Author: Pierre Gilet
# Version: 3.2
# Date: 2012-09-07
#
# Input param: resource type (allowed values: energy, physical, virtual, service)
#
# Description:
# This collector script posts dummy monitoring data
# coming from the data file var/dummy_collector.data.  
#
###

if [ ! $# -eq 1 ]
then
   echo "Usage: dummy_collector.sh resource-type"
   exit 1
else
   RESOURCETYPE=$1
fi

if [ ! "$RESOURCETYPE" = "energy" ] && [ ! "$RESOURCETYPE" = "physical" ] && [ ! "$RESOURCETYPE" = "virtual" ] && [ ! "$RESOURCETYPE" = "service" ]
then
   echo "Invalid resource type value (allowed values: energy, physical, virtual, service)"
   exit 1
fi

TMP=/tmp/$$
VMHOST=localhost

BASEDIR=`echo $0 | sed "s/\/dummy_collector\.sh//"`
CURRUNFILE=$BASEDIR/var/dummy_collector.data
COLLECTORID=008
TIMESTAMP=$(date +%s)

if [ ! -e $CURRUNFILE ] ; then exit 1 ; fi

awk -F "|" -v collectorId=$COLLECTORID -v timeStamp=$TIMESTAMP 'BEGIN{outstring="<?xml version=\"1.0\"?><MonitoringResources>"}{if (substr($0, 1 , 1) != "#") {outstring=outstring""sprintf("<monitoring_resource><physical_resource_id>%s</physical_resource_id><metric_name>%s</metric_name><metric_value>%s</metric_value><metric_unit>%s</metric_unit><metric_timestamp>%s</metric_timestamp><service_resource_id>%s</service_resource_id><virtual_resource_id>%s</virtual_resource_id><resource_type>%s</resource_type><monitoring_information_collector_id>%s</monitoring_information_collector_id></monitoring_resource>", $1, $2, $3, $4, timeStamp, $5, $6, $7, collectorId)}}END{outstring=outstring"</MonitoringResources>"; print outstring}' $CURRUNFILE > $TMP.collector_output.xml

if [ -s "$TMP.collector_output.xml" ]
then
   curl -X POST -d @$TMP.collector_output.xml -H "Content-Type:text/plain" http://${VMHOST}:8080/Aggregator/Aggregator/monitoringresources/$RESOURCETYPE
fi

rm -f $TMP.* 1>/dev/null 2>&1

exit 0


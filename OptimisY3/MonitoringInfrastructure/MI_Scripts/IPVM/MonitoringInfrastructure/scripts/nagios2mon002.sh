#!/bin/bash

TMP=/tmp/$$

BASEDIR=`echo $0 | sed "s/\/nagios2mon002\.sh//"`
source $BASEDIR/nagios2mon002.properties

if [ $ENHANCED = Y ]
then
   # Code for enhanced environments (e.g. FLEXIANT ENHANCED)
   ssh -i $KEYFILE root@$REMOTEHOST "/opt/optimis/MonitoringInfrastructure/scripts/$ENHANCEDSCRIPT" > $TMP.collector_output.xml
else
   # Code for full OPTIMIS environments (e.g. ATOS, UMU, FLEXIANT OPTIMIS)
   curl $REMOTEHOST/nagios/xml/status2xml.cgi?"001+'count_of_users','cpu_average_load','disk_free_space','Downstream','free_memory','status','Upstream','cpu_speed','hardware_error'" > $TMP.collector_output.xml
fi

if [ -s "$TMP.collector_output.xml" ]
then
   # Debug
   cat $TMP.collector_output.xml

   curl -X POST -d @$TMP.collector_output.xml -H "Content-Type:text/plain" http://${VMHOST}:8080/Aggregator/Aggregator/monitoringresources/$RESOURCETYPE
fi

rm -f $TMP.* 1>/dev/null 2>&1


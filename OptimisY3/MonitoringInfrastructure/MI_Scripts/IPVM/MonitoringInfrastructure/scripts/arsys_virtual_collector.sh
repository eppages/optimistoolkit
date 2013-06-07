#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 1.4
# Date: 2013-05-06
#
# Description:
# Calls SOAP web service of ARSYS to get virtual monitoring data.
#
###

TMP=/tmp/$$.arsys_virtual_collector
BASEDIR=`echo $0 | sed "s/\/arsys_virtual_collector\.sh//"`
source $BASEDIR/arsys_virtual_collector.properties

# Fetch data from ARSYS.
curl --silent --insecure --header "content-type: text/xml" -d "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><getPerfDataProvider xmlns=\"http://arsys.es/\"></getPerfDataProvider></soap:Body></soap:Envelope>" --basic --user $USER:$PASSWORD $SERVER:$PORT/OptimisWebService/performanceMonitor/performanceMonitor.asmx > $TMP.output.xml

# Check if error in XML file.
ERRORCOUNT=`grep -c ERROR $TMP.output.xml`

if [ ! $ERRORCOUNT -eq 0 ]
then
   echo Error message returned by the ARSYS web service. Cannot continue execution. Exiting now at: `date`
   rm $TMP.* 1>/dev/null 2>&1
   exit 1
fi

cat $TMP.output.xml | sed "s/<soap.*<MonitoringResources/<MonitoringResources/" | sed "s/<\/getPerfDataProviderResult><\/getPerfDataProviderResponse><\/soap:Body><\/soap:Envelope>$//" | sed "s/<MonitoringResources xmlns=\"\">/<MonitoringResources>/" > $TMP.collector_output.xml

if [ -s "$TMP.collector_output.xml" ]
then

   # Debug.
   cat $TMP.collector_output.xml

   # Check if empty XML.
   CHECKCONTENTS=$(grep -c "<MonitoringResources></MonitoringResources>" $TMP.collector_output.xml)

   # Push message only if XML not empty.
   if [ $CHECKCONTENTS -eq 0 ]
   then
      curl -X POST -d @$TMP.collector_output.xml -H "Content-Type:text/plain" http://${VMHOST}:8080/Aggregator/Aggregator/monitoringresources/$RESOURCETYPE
   fi
fi

rm $TMP.* 1>/dev/null 2>&1
exit 0


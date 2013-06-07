#!/bin/bash

TMP=/tmp/$$

#BASEDIR=`echo $0 | sed "s/\/virtual_collector\.sh//"`
#source $BASEDIR/virtual_collector.properties

#CLOUDMIDDLEWAREUC=$(echo $CLOUDMIDDLEWARE | tr '[:lower:]' #'[:upper:]')

   CLOUDMIDDLEWAREURI=http://localhost:8080/OpenNebulaVirtual/virtual/data/str


# Call API of cloud middleware.
curl $CLOUDMIDDLEWAREURI > $TMP.collector_output.xml

if [ -s "$TMP.collector_output.xml" ]
then
   # Check if empty XML. Step 1.
   CHECKCONTENTS1=$(grep -c "<MonitoringResources/>" $TMP.collector_output.xml)

   # Check if empty XML. Step 2.
   CHECKCONTENTS2=$(grep -c "<?xml .*?><MonitoringResources></MonitoringResources>" $TMP.collector_output.xml)

   # Check if error in XML.
   CHECKXMLERROR=$(grep -c "Error report" $TMP.collector_output.xml)

   # Push message only if XML is not empty and with no error.
   if [ $CHECKCONTENTS1 -eq 0 -a $CHECKCONTENTS2 -eq 0 -a $CHECKXMLERROR -eq 0 ]
   then
      # Debug
      cat $TMP.collector_output.xml
   fi
fi

rm -f $TMP.* 1>/dev/null 2>&1


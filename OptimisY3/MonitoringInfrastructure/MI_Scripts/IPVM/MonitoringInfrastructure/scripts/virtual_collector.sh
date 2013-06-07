#!/bin/bash

TMP=/tmp/$$

BASEDIR=`echo $0 | sed "s/\/virtual_collector\.sh//"`
source $BASEDIR/virtual_collector.properties

CLOUDMIDDLEWAREUC=$(echo $CLOUDMIDDLEWARE | tr '[:lower:]' '[:upper:]')

if [ "$CLOUDMIDDLEWAREUC" == "OPENNEBULA" ]
then
   CLOUDMIDDLEWAREURI=http://localhost:8080/OpenNebulaVirtual/virtual/data/str
   # Call API of cloud middleware.
   curl $CLOUDMIDDLEWAREURI > $TMP.collector_output.xml

elif [ "$CLOUDMIDDLEWAREUC" == "OPENSTACK" ] 
then
    TIME=`date +%s`
    echo "<?xml version=\"1.0\" encoding=\"utf-8\"?>" > $TMP.collector_output.xml
    echo "<MonitoringResources>" >> $TMP.collector_output.xml

    
    ## Please change the following parameters if needed:
    # list of nodes
    LIST="bscgrid21 bscgrid22"
    # location of storing the vm_collector.sh script in the node
    DIR=/opt/optimis/MonitoringInfrastructure
    # public key to access the node
    KEY=root_OPTIMIS1
    
    ## go through each host and collect the xml data
    for i in $LIST
    do
        ssh -i $DIR/scripts/get_metric/xentop_scripts/$KEY root@$i "$DIR/vm_collector.sh $TIME 2> /dev/null" >> $TMP.collector_output.xml
    done

    echo "</MonitoringResources>" >> $TMP.collector_output.xml
    
else
   # Default to Emotive.
   CLOUDMIDDLEWAREURI=http://localhost:8080/VirtualCollector/virtualmonitoring/data/str
   
   # Call API of cloud middleware.
   curl $CLOUDMIDDLEWAREURI > $TMP.collector_output.xml
fi


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

      curl -X POST -d @$TMP.collector_output.xml -H "Content-Type:text/plain" http://${VMHOST}:8080/Aggregator/Aggregator/monitoringresources/$RESOURCETYPE
   fi
fi

rm -f $TMP.* 1>/dev/null 2>&1


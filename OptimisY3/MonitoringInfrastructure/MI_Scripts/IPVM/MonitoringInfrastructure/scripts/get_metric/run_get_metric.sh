#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 3.6
# Date: 2013-03-06
# $Id$
#
# Description:
# This script calls another one fetching energy consumption figures
# from physical machines connected to a power distribution unit.
# Then, generate_xml.sh converts the output into XML.
#
# This script runs also the virt-top utility on the remote physical machines.
#
###

BASEDIR=`echo $0 | sed "s/\/run_get_metric\.sh//"`
TMP=/tmp/$$.run_get_metric
UNIXTIMESTAMP=$(date +%s)
source $BASEDIR/run_get_metric.properties

echo "<?xml version=\"1.0\"?>" > $TMP.final_output
echo "<MonitoringResources>"  >> $TMP.final_output

# Run xentop on remote physical servers in parallel.
echo "" > $TMP.output_from_get_metric.xentop
count=0
for OPTIMISSERVER in $SERVERLIST
do
   OPTIMISSERVERUPPER=$(echo $OPTIMISSERVER | tr '[:lower:]' '[:upper:]')
   $BASEDIR/xentop_scripts/check_optimis_xentop.sh root_${OPTIMISSERVERUPPER} $OPTIMISSERVER $UNIXTIMESTAMP  >> $TMP.output_from_get_metric.xentop &
   count=`expr $count + 1`
done

# find out the power consumption via snmp
if [ "$GETENERGYMETRICS" == "RACKTIVITY" ]
then
   $BASEDIR/get_metric.py  6 8 $BASEDIR $UNIXTIMESTAMP > $TMP.output_from_get_metric.068
   for OPTIMISSERVER in $SERVERLIST
   do
      $BASEDIR/generate_xml.sh $TMP.output_from_get_metric.068 $OPTIMISSERVER $COLLECTORID >> $TMP.final_output
   done

   $BASEDIR/get_metric.py  7 8 $BASEDIR $UNIXTIMESTAMP > $TMP.output_from_get_metric.078
   for OPTIMISSERVER in $SERVERLIST
   do
      $BASEDIR/generate_xml.sh $TMP.output_from_get_metric.078 $OPTIMISSERVER $COLLECTORID >> $TMP.final_output
   done

   $BASEDIR/get_metric.py  9 8 $BASEDIR $UNIXTIMESTAMP > $TMP.output_from_get_metric.098
   for OPTIMISSERVER in $SERVERLIST
   do
      $BASEDIR/generate_xml.sh $TMP.output_from_get_metric.098 $OPTIMISSERVER $COLLECTORID >> $TMP.final_output
   done

   $BASEDIR/get_metric.py 10 8 $BASEDIR $UNIXTIMESTAMP > $TMP.output_from_get_metric.108
   for OPTIMISSERVER in $SERVERLIST
   do
      $BASEDIR/generate_xml.sh $TMP.output_from_get_metric.108 $OPTIMISSERVER $COLLECTORID >> $TMP.final_output
   done

   $BASEDIR/get_metric.py 15 8 $BASEDIR $UNIXTIMESTAMP > $TMP.output_from_get_metric.158
   for OPTIMISSERVER in $SERVERLIST
   do
      $BASEDIR/generate_xml.sh $TMP.output_from_get_metric.158 $OPTIMISSERVER $COLLECTORID >> $TMP.final_output
   done

   $BASEDIR/get_metric.py 16 8 $BASEDIR $UNIXTIMESTAMP > $TMP.output_from_get_metric.168
   for OPTIMISSERVER in $SERVERLIST
   do
      $BASEDIR/generate_xml.sh $TMP.output_from_get_metric.168 $OPTIMISSERVER $COLLECTORID >> $TMP.final_output
   done
elif [ "$GETENERGYMETRICS" == "HPPDU" ] || [ "$GETENERGYMETRICS" == "FLEXIANT" ]
then
   if [ "$GETENERGYMETRICS" == "HPPDU" ]; then PDUSCRIPT=get_hppdu_parameters.sh ; else PDUSCRIPT=get_flexiantpdu_parameters.sh ; fi

   LOOPCOUNTER=0
   echo "" > $TMP.output_from_get_pdu_parameters
   for OPTIMISSERVER in $SERVERLIST
   do
      LOOPCOUNTER=$(( $LOOPCOUNTER + 1 ))
      CURTARGET=$(echo $SNMPTARGET | cut -d " " -f $LOOPCOUNTER)
      $BASEDIR/$PDUSCRIPT $BASEDIR $UNIXTIMESTAMP $OPTIMISSERVER $CURTARGET >> $TMP.output_from_get_pdu_parameters
   done

   for OPTIMISSERVER in $SERVERLIST
   do
      $BASEDIR/generate_xml.sh $TMP.output_from_get_pdu_parameters $OPTIMISSERVER $COLLECTORID >> $TMP.final_output
   done
else
   echo "Do nothing" > /dev/null
fi

# The xentop script runs in parallel or asynchronously. So need to wait
# until all results come back!
total=`cat $TMP.output_from_get_metric.xentop | wc -l`
count=`expr $count + 1`
while [ $total -lt $count ]
do
   total=`cat $TMP.output_from_get_metric.xentop | wc -l`
   sleep 1
done

for OPTIMISSERVER in $SERVERLIST
do
   $BASEDIR/generate_xml_for_string_metrics.sh $TMP.output_from_get_metric.xentop $OPTIMISSERVER $COLLECTORID >> $TMP.final_output
done

echo "</MonitoringResources>"  >> $TMP.final_output

if [ -s "$TMP.final_output" ]
then
   # Debug
   cat $TMP.final_output
   TS=$(date +%s)
   diff=`expr $TS - $UNIXTIMESTAMP`
   echo
   echo "Total execution time =" $diff "seconds."

   curl -X POST -d @$TMP.final_output -H "Content-Type:text/plain" http://${VMHOST}:8080/Aggregator/Aggregator/monitoringresources/$RESOURCETYPE
fi

rm -f $TMP.*


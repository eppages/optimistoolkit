#! /bin/bash

#Copyright 2012 Universitaet Stuttgart
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#
# Author: Anthony Sulistio

## NOTE: the "Domain ID" column should be in position 19. However, it may also
#be in the 18th position. Please check the virt-top output!
DOMAINIDPOSITION=19
ITERATIONS=2

#HOST=$(tail -n 1 $TMP.xentop.out | cut -d "," -f 1)
HOST=`cat /etc/hostname`

TIME=`date +%s`
if [ -n "$1" ]; then
    #echo "Use the given timestamp"
    TIME=$1
fi

TMP=/tmp/$$
virt-top --script -n $ITERATIONS -d 1 --csv $TMP.xentop.out

#################################################################
write_xml()
{
    ## need to take these parameters:
    RES=$1    # physical host name
    VMID=$2   # resource ID of the VM
    NAME=$3   # parameter name
    UNIT=$4   # parameter unit
    VALUE=$5  # parameter value
    TS=$6     # time stamp (unix time in seconds)
    echo "<monitoring_resource>"
    echo "<physical_resource_id>$RES</physical_resource_id>"
    echo "<virtual_resource_id>$VMID</virtual_resource_id>"
    echo "<service_resource_id></service_resource_id>"
    echo "<metric_name>$NAME</metric_name><metric_unit>$UNIT</metric_unit><metric_value>$VALUE</metric_value>"
    echo "<metric_timestamp>$TS</metric_timestamp><resource_type>virtual</resource_type>"
    echo "<monitoring_information_collector_id>openstack</monitoring_information_collector_id>"
    echo "</monitoring_resource>"
}


#################################################################

## determine how many VMs are currently running
## Looking at the "count" column - which is the 5th column. But including inactive
## Better approach is look for "Active" column - the 6th one
DOMAINCOUNT=$(tail -n 1 $TMP.xentop.out | cut -d "," -f 6)
#echo "domain count = "$DOMAINCOUNT

if [ $DOMAINCOUNT -gt 0 ]
then
   ## get the info about running VMs starting from position defined by $DOMAINIDPOSITION
   tail -n 1 $TMP.xentop.out | cut -d "," -f ${DOMAINIDPOSITION}- > $TMP.xentop.out.tmp
   mv $TMP.xentop.out.tmp $TMP.xentop.out

   ## NOTE: debugging
   #echo "cat $TMP.xentop.out"
   #cat $TMP.xentop.out

   I=0
   FINALSTRING=""

   ## iterating each running VMs
   while [ $I -lt $DOMAINCOUNT ]; do
      #echo "I = $I -- domain count = $DOMAINCOUNT"
      vmName=$(cut -d "," -f 2 $TMP.xentop.out)
      #cpuUsed=$(cut -d "," -f 4 $TMP.xentop.out)
      cpuUsed=$(cat $TMP.xentop.out | gawk -F"," '{printf("%0.2f", $4)}')
      MEM=$(cut -d "," -f 5 $TMP.xentop.out)
      MEM=`expr $MEM / 1024`                       ## total mem in MB
      memUsed=$(cut -d "," -f 6 $TMP.xentop.out)   ## % mem
      NETRX=$(cut -d "," -f 9 $TMP.xentop.out)     ## network received bytes
      NETTX=$(cut -d "," -f 10 $TMP.xentop.out)    ## network transmitted bytes
      VCPU=`ps aux | grep kvm | grep $vmName | gawk -F"-smp" '{print $2}' | cut -d "," -f 1`

      # get the <virtual_resource_id>
      resID=`ps aux | grep kvm | grep $vmName | gawk -F"uuid" '{print $2}' | cut -d " " -f 2`

      ## NOTE: debugging
      #echo "$vmName = $cpuUsed -- vcpu: $VCPU -- "$resID "--" $HOST
      write_xml $HOST $resID "cpu_user" "%" $cpuUsed $TIME
      write_xml $HOST $resID "cpu_vnum" "" $VCPU $TIME
      write_xml $HOST $resID "mem_total" "MB" $MEM $TIME
      write_xml $HOST $resID "mem_used" "%" $memUsed $TIME
      write_xml $HOST $resID "vm_status" "" "running" $TIME
      write_xml $HOST $resID "bytes_in" "bytes" $NETRX $TIME
      write_xml $HOST $resID "bytes_out" "bytes" $NETTX $TIME

      ### NOTE: found the bug below! Specific only for this machine
      #cut -d "," -f 9- $TMP.xentop.out > $TMP.xentop.out.tmp
      cut -d "," -f 11- $TMP.xentop.out > $TMP.xentop.out.tmp
      mv $TMP.xentop.out.tmp $TMP.xentop.out

      ## NOTE: debugging
      #echo
      #cat $TMP.xentop.out

      let I=I+1
   done

else
   echo
fi



#! /bin/sh

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


# Testing the VM monitoring.
# Instruction:
# - the vm_collector.sh needs to be copied to the physical (worker) node
# - change the LIST and DIR parameters for host names and location, respectively
# - change the KEY parameter for the public key access to the node.
#
# Usage: ./test_vm_collector.sh
# Output: xml string

## Please change the following parameters if needed:
# list of nodes
LIST="bscgrid21 bscgrid22"
# location of storing the vm_collector.sh script in the node
DIR=/opt/optimis/MonitoringInfrastructure
# public key to access the node
KEY=root_OPTIMIS

TMP=/tmp/$$
RESOURCETYPE=virtual
VMHOST=localhost

# unix timestamp
TIME=`date +%s`
echo "<?xml version=\"1.0\" encoding=\"utf-8\"?>" > $TMP.collector_output.xml
echo "<MonitoringResources>" >> $TMP.collector_output.xml

## go through each node and collect the xml data
for i in $LIST
do
    ssh -i $DIR/$KEY root@$i "$DIR/vm_collector.sh $TIME 2> /dev/null" >> $TMP.collector_output.xml
done

echo "</MonitoringResources>" >> $TMP.collector_output.xml

cat $TMP.collector_output.xml
rm -f $TMP.* 1>/dev/null 2>&1


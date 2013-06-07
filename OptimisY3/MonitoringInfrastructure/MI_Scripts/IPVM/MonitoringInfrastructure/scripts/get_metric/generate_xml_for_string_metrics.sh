#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 2.1
# Date: 2013-03-06
#
# Description:
# This script converts the output of check_optimis_xentop.sh into XML.
#
###

OPTIMISFILE=$1
OPTIMISHOST=$2
COLLECTORID=$3

cat $OPTIMISFILE | grep -ia "$OPTIMISHOST" | awk -F "|" -v collectorId="$COLLECTORID" '{printf "<monitoring_resource>\n<physical_resource_id>%s</physical_resource_id>\n<metric_name>%s</metric_name>\n<metric_value>%s</metric_value>\n<metric_unit>%s</metric_unit>\n<metric_timestamp>%s</metric_timestamp>\n<service_resource_id></service_resource_id>\n<virtual_resource_id></virtual_resource_id>\n<resource_type>energy</resource_type>\n<monitoring_information_collector_id>%s</monitoring_information_collector_id>\n</monitoring_resource>\n", $1, $2, $3, $4, $5, collectorId}'


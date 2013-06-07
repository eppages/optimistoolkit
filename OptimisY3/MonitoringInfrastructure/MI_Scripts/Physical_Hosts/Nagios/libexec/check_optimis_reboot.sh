#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 2.0
# Date: 2012-01-23
#
# Description:
# Print to stdout the last reboot time in Unix format (followed by uptime duration in brackets).
#
###

duration=$(last -x reboot | tr -s " " " " | cut -d " " -f 5- | head -n 1 | sed "s/ $//" | sed "s/ /_/g" | sed "s/^.*(//" |\
sed "s/)$//" | sed "s/+/:/" | awk -F ":" '{result=$1*24*60*60+$2*60*60+$3*60; printf "%s\n", result}')
timeto=$(date +%s)
timefrom=$(( $timeto - $duration ))
echo "$timefrom($duration)"


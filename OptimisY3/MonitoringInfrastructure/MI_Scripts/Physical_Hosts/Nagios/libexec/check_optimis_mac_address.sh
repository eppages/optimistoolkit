#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 1.0
# Date: 2011-11-28
#
# Description:
# Returns the MAC address of eth0.
#
###

echo $(/sbin/ifconfig eth0 | head -n 1 | tr -s " " " " | cut -d " " -f 5)

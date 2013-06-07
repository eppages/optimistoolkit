#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 1.0
# Date: 2012-01-19
#
# Description:
# Print to stdout the total disk space.
#
###

df -h --block-size=M | grep "/$" | tr -s " " " " | cut -d " " -f 2 | sed "s/M$/ MB/"


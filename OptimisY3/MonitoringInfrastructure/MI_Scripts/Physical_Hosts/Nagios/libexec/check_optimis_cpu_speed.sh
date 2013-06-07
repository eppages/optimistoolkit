#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 1.0
# Date: 2012-01-19
#
# Description:
# Print to stdout the CPU speed.
#
###

grep "cpu MHz" /proc/cpuinfo | head -n 1 | cut -d ":" -f 2 | sed "s/^ *//" | sed "s/$/ MHz/"


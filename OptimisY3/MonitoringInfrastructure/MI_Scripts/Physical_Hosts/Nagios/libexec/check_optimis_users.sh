#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 1.0
# Date: 2011-11-28
#
# Description:
# Returns the count of users logged in.
#
###

BASEDIR=`echo $0 | sed "s/\/check_optimis_users.sh//"`
echo `$BASEDIR/check_users -w 10000 -c 10000 | cut -d \| -f 2 | cut -d = -f 2 | cut -d \; -f 1` users

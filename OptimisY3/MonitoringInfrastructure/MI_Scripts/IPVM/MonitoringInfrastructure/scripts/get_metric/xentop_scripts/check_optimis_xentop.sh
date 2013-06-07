#!/bin/bash

###
#
# Author: Pierre Gilet
# Version: 3.1
# Date: 2013-03-06
# $Id$
#
# Description:
# Get info from virt-top running on a remote server (initially this was from xentop, hence the name of this script).
#
###

if [ ! $# -eq 3 ]
then
   echo "Usage: check_optimis_xentop.sh ssh-key-file ssh-host unix-timestamp"
   exit 1
else
   KEYFILE=$1
   # Server must be in (optimis1, optimis2, optimis3, optimis4, etc.)
   SERVER=$2
   UNIXTIMESTAMP=$3
fi

TMP=/tmp/$$
BASEDIR=`echo $0 | sed "s/\/check_optimis_xentop\.sh//"`
source $BASEDIR/check_optimis_xentop.properties

if [ $ENHANCED = Y ]
then
   # Code for enhanced environments (e.g. FLEXIANT ENHANCED)
   ssh -i $BASEDIR/$KEYFILE root@$SERVER "/opt/optimis/MonitoringInfrastructure/scripts/get_metric/xentop_scripts/convert_xentop_output_1.sh" > $TMP.xentop.out
else
   # Code for full OPTIMIS environments (e.g. ATOS, UMU, FLEXIANT OPTIMIS)
   ssh -i $BASEDIR/$KEYFILE root@$SERVER "rm -f /tmp/*.xentop.out; virt-top --script -n $ITERATIONS -d 1 --csv $TMP.xentop.out 2>/dev/null"
   scp -q -i $BASEDIR/$KEYFILE root@$SERVER:$TMP.xentop.out $TMP.xentop.out
   #ssh -i $BASEDIR/$KEYFILE root@$SERVER "rm $TMP.xentop.out"
fi

DOMAINCOUNT=$(tail -n 1 $TMP.xentop.out | cut -d "," -f 5)

if [ $DOMAINCOUNT -gt 0 ]
then
   tail -n 1 $TMP.xentop.out | cut -d "," -f ${DOMAINIDPOSITION}- > $TMP.xentop.out.tmp
   mv $TMP.xentop.out.tmp $TMP.xentop.out
   
   I=0
   FINALSTRING=""
   while [ $I -lt $DOMAINCOUNT ]; do
      # For some bizarre reason, the awk command is not able to round the
      # value of $4 with the format %.2f.
      # So, we use instead a workaround based on bc to round the value.
      VALUEVAR=""
      VALUEVAR=$(cut -d "," -f 4 $TMP.xentop.out)
      if [ ! $VALUEVAR = "" ]
      then
         VALUEVAR=`echo "scale=2; ${VALUEVAR}/1" | bc`
         FINALSTRING=${FINALSTRING}$(awk -F "," -v valueVar=$VALUEVAR '{printf("%s %s;", $2, valueVar)}' $TMP.xentop.out)
      fi
      cut -d "," -f 9- $TMP.xentop.out > $TMP.xentop.out.tmp
      mv $TMP.xentop.out.tmp $TMP.xentop.out
      let I=I+1 
   done
   FINALSTRING=$(echo $FINALSTRING | awk -v maxCharOutput=$MAXCHAROUTPUT '{sub(/;$/, "", $0); $0=substr($0, 1, maxCharOutput)} END {printf("%s", $0)}')
   echo ${SERVER}"|xentop_cpu|"${FINALSTRING}"||"${UNIXTIMESTAMP}
else
   echo "N/A"
fi

rm -f $TMP.*
exit 0


#/bin/bash

TMP=/tmp/$$

xentop -i 2 -f -b > $TMP.xentop.out

XENTOPOUT=$TMP.xentop.out

MATCHINGLINE=`grep -n NAME $XENTOPOUT | tail -n 1 | cut -d ":" -f 1`
LINECOUNT=`cat $XENTOPOUT | wc -l`
TAILCOUNT=$(expr $LINECOUNT - $MATCHINGLINE)

tail -n $TAILCOUNT $XENTOPOUT | tr -s " " " " | sed "s/^ //" | sed "s/ /,/g" |\
awk -F "," -v domainCount=$TAILCOUNT 'BEGIN {printf("x,x,x,x,%s,x,x,x,x,x,x,x,x,x,x,x,x,x,", domainCount)}
{printf("x,%s,x,%s,x,x,x,x,", $1, $4)} END {printf("\n")}' | sed "s/,$//"

rm -f $TMP.*


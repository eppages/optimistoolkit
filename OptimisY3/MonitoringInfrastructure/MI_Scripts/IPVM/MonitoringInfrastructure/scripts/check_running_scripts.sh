#! /bin/sh
# A script that runs some checks

# Check running scripts.

BASEDIR=`echo $0 | sed "s/\/check_running_scripts\.sh//"`
PYTHON="/usr/bin/python2.7"

SERVICE="sender.py receiver.py"
for i in $SERVICE
do
    pid=`ps aux | grep -v grep | grep -i $i | gawk '{print $2}'`
    if [ -z "$pid" ]
    then
        #echo "$i is not running"
        $PYTHON $BASEDIR/../rabbitmq/$i &
    fi
done

exit 0


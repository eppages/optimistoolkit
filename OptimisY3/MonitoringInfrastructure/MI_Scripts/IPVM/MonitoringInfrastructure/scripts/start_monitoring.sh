#! /bin/sh
BASEDIR=`echo $0 | sed "s/\/start_monitoring\.sh//"`
PYTHON="/usr/bin/python2.7"
$PYTHON $BASEDIR/../rabbitmq/sender.py &
$PYTHON $BASEDIR/../rabbitmq/receiver.py &


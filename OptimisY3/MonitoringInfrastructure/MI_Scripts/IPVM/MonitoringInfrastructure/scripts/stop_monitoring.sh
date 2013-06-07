#! /bin/sh
# Kill background processes
pid=`ps aux | grep -i sender.py | gawk '{print $2}'`
kill -9 $pid

pid=`ps aux | grep -i receiver.py | gawk '{print $2}'`
kill -9 $pid


#!/bin/bash

#Script for automatically generate the Subversion Changelog file after commit

MESSAGE=null #Message to add to commit log

if [[ $# == 1 ]]; then
  MESSAGE=$1 #Message between quotation marks
else
  echo "WRONG NUMBER OF ARGUMENTS!"
  echo "Usage:"
  echo "Execute the script from your root component folder"
  echo "  ./commitlog.sh <MESSAGE>"
  echo "Example:"
  echo "  ./commitlog.sh \"pom file modified\"" 
  exit 1
fi

DPID=$(svn commit -m "$MESSAGE")
if [ "${DPID}" = "" ]; then
echo "Commit done..." 
/usr/bin/svn2cl.sh -i --group-by-day -o Changelog.txt
echo "svn2cl done..." 
fi

echo ""
echo "Script complete!" 

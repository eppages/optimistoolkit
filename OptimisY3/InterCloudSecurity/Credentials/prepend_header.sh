#!/bin/bash
for f in `find ./ -type f -name *.java`
do
  echo "Prepending $f file..."
  # take action on each file. $f store current file name
  cat $1 $f > temp_file
  mv temp_file $f
done

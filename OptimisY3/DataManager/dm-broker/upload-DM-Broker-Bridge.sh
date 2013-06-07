#!/bin/bash

file=DM-Broker-Bridge
zipFolder=DM-Broker-Bridge/
remoteFolder=/var/www/optimis/
server=optimis.fusion-algorithms.com
username=root
zipfile=$file.zip

rm $zipfile
zip -r $zipfile $zipFolder
scp $zipfile  $username@$server:$remoteFolder
rm $zipfile

echo "wget http://optimis.fusion-algorithms.com/optimis/DM-Broker-Bridge.zip"

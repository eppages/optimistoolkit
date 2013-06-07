#!/bin/bash

file=optimis-dm-mount-vmimages
zipFolder=optimis-dm-mount-vmimages/
remoteFolder=/var/www/optimis/
server=optimis.fusion-algorithms.com
username=root
zipfile=$file.zip

rm $zipfile
zip -r $zipfile $zipFolder
scp $zipfile  $username@$server:$remoteFolder
rm $zipfile

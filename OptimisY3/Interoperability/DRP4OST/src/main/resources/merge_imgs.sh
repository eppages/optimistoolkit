#!/bin/bash

#**************************************************************
#Script for mounting a hvm image file
#**************************************************************

echo "GOT $0 $1 $2 $3 $4 $5"

#mount images
sudo $5"mount_qcow2.sh" $1 $2 
#MQCOW=$!
#echo "sudo ps aux | grep $MQCOW : "`sudo ps aux | grep $MQCOW`
#wait $MQCOW

echo "************************************************* last mount_qcow2.sh pid: $!"
#sleep 1

sudo $5"mount_iso.sh" $3 $4
#MISO=$!
#echo "sudo ps aux | grep $MISO: "`sudo ps aux | grep $MISO`
#wait $MISO
echo "************************************************* last mount_iso.sh pid: $!"
#sleep 1

#Script Copying from ISO TO QCOW2

#sudo ls -lah $2

echo "--------------------- EXECUTING sudo mkdir -p $2/mnt/context/"
#sudo mkdir -p $2"/mnt/context/"
#sudo ps aux | grep $!

#for bash "cp -Rf $4"/." $2"/mnt/context/") != sudo cp -Rf $4"/*" $2"/mnt/context/" !!!!!
echo "--------------------- EXECUTING sudo cp -Rf $4/. $2/mnt/context/"
sudo cp -Rf $4"/." $2"/mnt/context/"
#sudo ps aux | grep $!
#sleep 1

echo "--------------------- EXECUTING sudo sync"
sudo sync
#sudo ps aux | grep $!
#sleep 1

#umount images
sudo $5"umount_iso.sh" $4 
#UMISO=$!
#echo "sudo ps aux | grep $UMISO"`sudo ps aux | grep $UMISO`
#wait $UMISO
echo "************************************************* last umount_iso.sh pid: $!"
#sleep 1#

sudo $5"umount_qcow2.sh" $2 &
#UMQCOW=$!
#echo "sudo ps aux | grep $UMQCOW"`sudo ps aux | grep $UMQCOW`
#wait $UMQCOW
echo "************************************************* last umount_qcow2.sh pid: $!"
#sleep 1


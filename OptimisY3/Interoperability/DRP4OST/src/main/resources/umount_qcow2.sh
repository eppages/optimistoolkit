#!/bin/bash

#Script for unmounting a hvm images file

MOUNTPOINT=null #Mountpoint of image

if [ $# -eq 1 ]; then
  MOUNTPOINT=$1

  if [ -d "`pwd`/$MOUNTPOINT" ]; then
    MOUNTPOINT="`pwd`/$MOUNTPOINT"
  fi

  if [ -d $MOUNTPOINT ]; then
    if [ "`mount | grep \"$MOUNTPOINT \"`" == "" ]; then
      echo "ERROR: no device mounted at $MOUNTPOINT"
      exit
    else
      echo "Found mount point at: $MOUNTPOINT"
    fi
  else
    echo "ERROR: mount point does not exist"
    exit 1
  fi
else
  echo "ERROR: NOT ENOUGH ARGUMENTS"
  echo "Usage:"
  echo "  ./umount-hvm-img.sh <MOUNT_POINT>"
  echo "Example:"
  echo "  ./umount-hvm-img.sh /home/me/my-images/mount-point"
  exit 1
fi

NBDDEV=/dev/`mount | grep $MOUNTPOINT | cut -d ' ' -f 1 | cut -d 'p' -f 3 | cut -d '/' -f 2`
#Sergio edit...
#NBDDEV=/dev/`mount | grep $MOUNTPOINT | cut -d ' ' -f 1 | cut -d 'p' -f 1 | cut -d '/' -f 3`
echo "Removing mount point..."
sudo umount -v $MOUNTPOINT
echo "Removing partition map..."
sudo kpartx -dv $NBDDEV
echo "Disconnecting NDB device..."
qemu-nbd -d $NBDDEV
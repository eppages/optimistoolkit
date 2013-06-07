#!/bin/bash

#Script for unmounting a hvm images file

ISOMOUNTPOINT=null #Mountpoint of image

if [ $# -eq 1 ]; then
  ISOMOUNTPOINT=$1

  if [ -d "`pwd`/$ISOMOUNTPOINT" ]; then
    ISOMOUNTPOINT="`pwd`/$ISOMOUNTPOINT"
  fi

  if [ -d $ISOMOUNTPOINT ]; then
    if [ "`mount | grep \"$ISOMOUNTPOINT \"`" == "" ]; then
      echo "ERROR: no device mounted at $ISOMOUNTPOINT"
      exit
    else
      echo "Found mount point at: $ISOMOUNTPOINT"
    fi
  else
    echo "ERROR: mount point does not exist"
    exit 1
  fi
else
  echo "ERROR: NOT ENOUGH ARGUMENTS"
  echo "Usage:"
  echo "  ./umount-iso-img.sh <MOUNT_POINT>"
  echo "Example:"
  echo "  ./umount-iso-img.sh /home/me/my-images/mount-point"
  exit 1
fi

sudo umount -v $ISOMOUNTPOINT
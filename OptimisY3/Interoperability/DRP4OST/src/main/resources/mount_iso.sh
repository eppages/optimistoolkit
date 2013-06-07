#!/bin/bash

#Script for mounting an iso image file

IMAGENAME=null #Image name
MOUNTPOINT=null #Mountpoint of image


if [ "$#" -ge "2" ]; then

  IMAGENAME=$1
  if [ -f "`pwd`/$IMAGENAME" ]; then
    IMAGENAME="`pwd`/$IMAGENAME"
  fi

  MOUNTPOINT=$2
  if [ -d "`pwd`/$MOUNTPOINT" ]; then
    MOUNTPOINT="`pwd`/$MOUNTPOINT"
  fi

  if [ -f $IMAGENAME ]; then
    echo "Found image file at: $IMAGENAME"
  else
    echo "ERROR: image file not found"
    exit 1
  fi
  if [ -d $MOUNTPOINT ]; then
    echo "Found mount point at: $MOUNTPOINT"

    if [ "`find $MOUNTPOINT -type f | tail -n 1`" != "" ]; then
      echo "ERROR: mount point is not empty"
      exit 1
    fi

    if [ "`mount | grep \"$MOUNTPOINT \"`" != "" ]; then
      echo "ERROR: device already mounted at $MOUNTPOINT"
      exit 1
    fi
  else
    echo "ERROR: mount point does not exist"
    echo "  Create it using: \"mkdir -p $MOUNTPOINT\""
    exit 1
  fi
else
  echo "ERROR: NOT ENOUGH ARGUMENTS"
  echo "Usage:"
  echo "  ./mount-iso-img.sh <IMAGE_NAME> <MOUNT_POINT>"
  echo "Example:"
  echo "  ./mount-iso-img.sh /home/me/my-images/my-image.iso /home/me/my-images/mount-point"
  exit 1
fi


sudo mount -o loop -t iso9660 $IMAGENAME $MOUNTPOINT
if [ $? -ne 0 ]; then
  echo "ERROR: Failed to mount iso image $IMAGENAME"
else 
  echo "Image $IMAGENAME mounted at: $MOUNTPOINT" 
fi
#!/bin/bash

#**************************************************************
#Script for mounting a hvm image file
#**************************************************************

echo "GOT $1 $2 $3 $4"


IMAGENAME=null #Image name
MOUNTPOINT=null #Mountpoint of image
PARTITION=null #Partition in image


if [ "$#" -ge "2" ]; then

  IMAGENAME=$1
  if [ -f "`pwd`/$IMAGENAME" ]; then
    IMAGENAME="`pwd`/$IMAGENAME"
  fi

  MOUNTPOINT=$2
  if [ -d "`pwd`/$MOUNTPOINT" ]; then
    MOUNTPOINT="`pwd`/$MOUNTPOINT"
  fi

#  if [ "$#" -eq "2" ]; then
#    echo "Using default partition number \"1\""
#    PARTITION=1
#  else
#    PARTITION=$3
#  fi

  if [ -f $IMAGENAME ]; then
    echo "Found image file at: $IMAGENAME"
    if [ "`ps -A -o args | grep '$IMAGENAME' | grep 'qemu-nbd -c /dev/' | grep -v grep`" != ""  ]; then
      echo "ERROR: image file seems to be already mounted or in use by qemu-nbd"
      exit 1
    fi
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
  echo "  ./mount-hvm-img.sh <IMAGE_NAME> <MOUNT_POINT> [PARTITION_NUMBER default=1]"
  echo "Example:"
  echo "  ./mount-hvm-img.sh /home/me/my-images/my-image.raw.img /home/me/my-images/mount-point 1"
  exit 1
fi

#Sergio comenta...
LASTNBDNUM=`cat /proc/partitions | grep nbd | awk '{print $4}' | tail -n 1 | cut -c 4-5`

if [ "$LASTNBDNUM" == "" ]; then
  NBDNUM=0
else
  NBDNUM=$(expr $LASTNBDNUM + 1)
fi

if [ "$NBDNUM" -gt "15" ]; then
  echo "ERROR: No more Network block devices available"
  exit 1
fi

NBDDEV=/dev/nbd$NBDNUM

echo "Using first network block device: $NBDDEV"

qemu-nbd -c $NBDDEV $IMAGENAME
echo "Mapping partitions from $NBDDEV"
sudo kpartx -av $NBDDEV
echo "Mounting partition from $MAP"
#MAP="/dev/mapper/nbd"$NBDNUM"p"$PARTITION
MAP="/dev/nbd"$NBDNUM"p"$PARTITION
sudo mount $MAP $MOUNTPOINT
if [ $? -ne 0 ]; then
  echo "ERROR: Failed to mount image, trying to clean up..."
  sudo kpartx -dv $NBDDEV
  qemu-nbd -d $NBDDEV
else 
  echo "Image mounted at: $MOUNTPOINT" 
fi


#**************************************************************
#Script for mounting an iso image file
#**************************************************************

IMAGENAME=null #Image name
MOUNTPOINT=null #Mountpoint of image


if [ "$#" -ge "2" ]; then

  IMAGENAME=$3
  if [ -f "`pwd`/$IMAGENAME" ]; then
    IMAGENAME="`pwd`/$IMAGENAME"
  fi

  MOUNTPOINT=$4
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


#**************************************************************
#Script Copying from ISO TO QCOW2
#**************************************************************

echo "Gonna copy... \n sudo cp -Rf $ISOSOURCE $QCO2WDEST/mnt/context/"
sudo cp -Rf $ISOSOURCE $QCO2WDEST"/mnt/context/"
sudo sync


#**************************************************************
#Script for unmounting a ISO images file
#**************************************************************

MOUNTPOINT=null #Mountpoint of image

if [ $# -eq 1 ]; then
  MOUNTPOINT=$4

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
  echo "  ./umount-iso-img.sh <MOUNT_POINT>"
  echo "Example:"
  echo "  ./umount-iso-img.sh /home/me/my-images/mount-point"
  exit 1
fi

sudo umount -v $MOUNTPOINT


#**************************************************************
#Script for unmounting a QCOW2 images file
#**************************************************************

MOUNTPOINT=null #Mountpoint of image

if [ $# -eq 1 ]; then
  MOUNTPOINT=$2

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

#NBDDEV=/dev/`mount | grep $MOUNTPOINT | cut -d ' ' -f 1 | cut -d 'p' -f 3 | cut -d '/' -f 2`
#Sergio edit...
NBDDEV=/dev/`mount | grep $MOUNTPOINT | cut -d ' ' -f 1 | cut -d 'p' -f 1 | cut -d '/' -f 3`
echo "Removing mount point..."
sudo umount -v $MOUNTPOINT
echo "Removing partition map..."
sudo kpartx -dv $NBDDEV
echo "Disconnecting NDB device..."
qemu-nbd -d $NBDDEV
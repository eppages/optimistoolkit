#! /bin/sh
# Copyright 2012 University of Stuttgart
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# 
# This script compresses the qcow2 image. It assumes that the qcow2 image has 
# an ext4 filesystem on the primary partition.
# Required packages are: qemu-utils, zerofree
# Also required these programs: fsck, fdisk
# Author: Anthony Sulistio
# Usage: ./compress_qcow2_images.sh [directory]

prog=`which zerofree`
if [ -z "$prog" ]; then
    echo "Please install the 'zerofree' package before running this script."
    exit 1
fi

uid=`id -u`
if [ $uid -ne 0 ]; then
   echo "You must be root to run this script. Aborting...";
#   exit 1;
fi

dir="."
if [ -n "$1" ]; then
    dir=$1
    echo "Working directory =" $dir
#    exit 0
fi

# check if files exist
list=`ls $dir/*.qcow2`
status=`echo $?`
if [ $status -ne 0 ]; then
    exit 1   # if no such file or directory then exit
fi

modprobe nbd max_part=63
list=`ls $dir/*.qcow2`
MAX_NBD=16   # max num of /dev/nbd partitions
n=0
for i in $list; do
    echo "qemu-nbd -c /dev/nbd$n $i"
    qemu-nbd -c /dev/nbd$n $i
    fdisk -l /dev/nbd${n}
    echo
    
    # check the disk first
    echo "fsck.ext4 -f /dev/nbd${n}p1"
    fsck.ext4 -f /dev/nbd${n}p1
    echo
    
    # make zeros of the unused space in the image
    echo "zerofree -v /dev/nbd${n}p1"
    zerofree -v /dev/nbd${n}p1
    echo
    
    # check again
    echo "fsck.ext4 -f /dev/nbd${n}p1"
    fsck.ext4 -f /dev/nbd${n}p1
    
    # compressed the image
    echo "qemu-img convert -c -O qcow2 $i $i.tmp"
    qemu-img convert -c -O qcow2 $i $i.tmp
    
    # rename the compressed image
    mv -v $i $i.bak
    mv -v $i.tmp $i

    # disconnect the NBD partition
    qemu-nbd -d /dev/nbd${n}
    
    # increment n and reset n to 0 if it exists the max NBD partition
    n=`expr $n + 1`
    if [ $n -ge $MAX_NBD ]; then
        n=0
    fi
    
    echo "Done for" $i
    echo "-------------------------------------------------------------------"
    echo
done

echo "Compression of qcow2 images are done."
echo "Please remove the *.qcow2.bak files manually."
echo


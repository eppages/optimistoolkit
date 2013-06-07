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
# This script tests the qcow2 image whether it can run on OpenStack testbed
# Author: Anthony Sulistio

NAME="ics-$$"
OUT="/tmp/$NAME.out"

echo "Launch or start the virtual machine"
echo "> nova boot --image "ICS CentOS" --flavor m1.tiny $NAME > $OUT"
nova boot --image "ICS CentOS" --flavor m1.tiny $NAME > $OUT

############################################
echo
echo "> nova list"
nova list

echo
echo "> nova show $NAME"
nova show $NAME

echo
num=30
echo "Wait for $num seconds to launch the VM"
sleep $num

echo
echo "> nova list"
nova list

echo
echo "> nova show $NAME"
nova show $NAME

############################################
echo
num=60
echo "Wait for $num seconds to launch the VM"
sleep $num

#IP=`nova list | grep $NAME | gawk -F"=" '{print $2}' | gawk -F"|" '{print $1}'`
echo
echo "Connecting to $NAME. The password should be given to you beforehand."
echo "Try testing the network connection by using ping or wget. For example:"
echo "ping www.google.com"
echo "wget http://the.earth.li/~sgtatham/putty/latest/x86/putty.exe"
echo
echo "> nova ssh --private $NAME"
nova ssh --private $NAME

############################################
echo
echo 
echo "Remove the VM"
echo "> nova delete $NAME"
nova delete $NAME

echo
echo "> nova list"
nova list

echo
num=15
echo "Wait for $num seconds for the VM to be removed from the list"
sleep $num

echo
echo "> nova list"
nova list

echo
echo "> nova show $NAME"
nova show $NAME

echo
echo "--- Testing is done ---"
echo
rm -f $OUT


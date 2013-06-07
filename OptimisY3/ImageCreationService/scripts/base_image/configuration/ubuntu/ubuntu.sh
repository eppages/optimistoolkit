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
# Author: Anthony Sulistio
# This script provides an automatic installation of packages on an Ubuntu 12.04 LTS Server image. 
# Moreover, it configures the image with OpenNebula contextualization and DataManager scripts.

uid=`id -u`
if [ $uid -ne 0 ]; then
   echo "You must be root to run this script or use sudo. Aborting...";
   exit 1;
fi

# Ubuntu uses dash instead of bash to speed up desktop boot times.
# when you get the option select "no" to actually use bash instead of dash.
#dpkg-reconfigure dash 
cd /bin; rm -f sh; ln -s bash sh
cd -

apt-get update > /dev/null
apt-get -y upgrade
apt-get -y dist-upgrade

# install packages and dependencies for Data Manager and Secure Cloud
# libapt-pkg-libc6.10-6-4.8 for the IPS component is available only on Ubuntu 10.04 / Lucid
apt-get -y install build-essential sshfs wget curl unzip subversion htop \
    python-ldap python-urlgrabber python-iniparse python-numexpr python python-pyorbit \
    ruby gcc gfortran tcl tk openjdk-7-jdk openvpn openssh-server openssh-client \
    make gawk libsigsegv2 xfsprogs xfsdump lighttpd vim \
    tomcat6 tomcat6-admin tomcat6-common tomcat6-docs graphviz imagemagick

# disable the waiting for network!
mv -vf /etc/init/failsafe.conf /etc/init/failsafe.conf.bak
cp -vf failsafe.conf /etc/init/

# to remove existing init.d scripts
update-rc.d -f optimis-datamanager remove > /dev/null
update-rc.d -f context_network remove > /dev/null
update-rc.d -f context_application remove > /dev/null


# install OPTIMIS packages and other configuration files
# These packages are downloaded from the public optimis svn repository
optimis="http://pandora.atosorigin.es/svn/optimis-pub/OptimisY3"
svn checkout --no-auth-cache --non-interactive --username anonymous --password '' \
    $optimis/ContextualizationTools/trunk/VmContextualizer/runtime/templates/scripts/linux/ \
    $optimis/DataManager/dm-mount/PM-datamanager-mounting/ \
    $optimis/InterCloudSecurity/SecureCloud/Agents/Ubuntu12.04/

## install context files for OpenNebula
install -d /mnt/context
chmod 755 linux/*
cp -vf linux/context_* /etc/init.d/

# For context_network, need to use the modified /etc/init/networking.conf file
mv -vf /etc/init/networking.conf /etc/init/networking.conf.bak
cp -vf networking.conf /etc/init/
#update-rc.d context_network start 8 S .
update-rc.d context_application start 9 2 3 4 5 . stop 9 0 1 6 .


## install Data Manager component
cd PM-datamanager-mounting
chmod 755 *
./install-optimis-datamanager.sh
echo "Installation of OPTIMIS Data Manager is done."
cd ..

# set the correct init runtime for data manager
update-rc.d -f optimis-datamanager remove
update-rc.d optimis-datamanager start 90 3 5 . stop 60 0 1 2 6 .


## install the secure cloud package
chmod 755 Ubuntu12.04/*.*
echo "Installing Secure Cloud package ..."
ARCH=`uname -i`
if [ "$ARCH" = "x86_64" ]; then
    rm -vf Ubuntu12.04/*.i386.bin
    ./Ubuntu12.04/*64.bin
else
    rm -vf Ubuntu12.04/*64.bin
    ./Ubuntu12.04/*.bin
fi


## remove old archieved packages
apt-get autoremove > /dev/null
apt-get autoclean > /dev/null
apt-get clean > /dev/null



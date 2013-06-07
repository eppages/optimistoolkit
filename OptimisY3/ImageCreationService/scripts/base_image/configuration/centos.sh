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
# This script provides an automatic installation of packages on a CentOS 6.3 image.
# Moreover, it configures the image with OpenNebula contextualization and DataManager scripts.

## If the CentOS has no network connection, check the ifcfg-eth0 file and change the ONBOOT to yes.
## The /etc/sysconfig/network-scripts/ifcfg-eth0 file should contain the following lines:
#DEVICE=eth0
#BOOTPROTO=dhcp
#ONBOOT=yes

uid=`id -u`
if [ $uid -ne 0 ]; then
   echo "You must be root to run this script. Aborting...";
   exit 1;
fi

yum -y install wget unzip
rpm -Uvh http://download.fedoraproject.org/pub/epel/6/`uname -i `/epel-release-6-8.noarch.rpm

ARCH=`uname -i`
if [ "$ARCH" = "i386" ]; then
    ARCH="i686"
fi

## repositories for Data Manager package dependencies
pkg=`rpm -qa | grep rpmforge-release`
if [ -z $pkg ]; then
    wget http://pkgs.repoforge.org/rpmforge-release/rpmforge-release-0.5.2-2.el6.rf.$ARCH.rpm
    rpm --import http://apt.sw.be/RPM-GPG-KEY.dag.txt
    rpm -Ki rpmforge-release-0.5.2-2.el6.rf.*.rpm
fi

yum -y update
yum -y upgrade

# install packages and dependencies for Data Manager, IPS and Secure Cloud
yum -y install rubygems fuse-sshfs sshfs curl pyorbit subversion python-ldap \
    ruby htop gcc gcc-gfortran tcl tk python-urlgrabber python-iniparse python-numexpr \
    java-1.6.0-openjdk vim openssh-server openssh-clients \
    openvpn lighttpd man man-pages avahi avahi-dnsconfd dos2unix \
    tomcat6 tomcat6-webapps kernel-devel make gawk python graphviz ImageMagick

    
## Check the correct CentOS version
ver=6   
if [ `grep -c $ver /etc/redhat-release` -eq 0 ]; then
    ver=5   # grep returns 0 means OS uses older version
fi

# install OPTIMIS packages and other configuration files
# These packages are downloaded from the public optimis svn repository
optimis="http://pandora.atosorigin.es/svn/optimis-pub/OptimisY3"
svn checkout --no-auth-cache --non-interactive --username anonymous --password '' \
    $optimis/ContextualizationTools/trunk/VmContextualizer/runtime/templates/scripts/linux/ \
    $optimis/DataManager/dm-mount/PM-datamanager-mounting/ \
    $optimis/InterCloudSecurity/SecureCloud/Agents/CentOS6/

svn checkout --no-auth-cache --non-interactive --username anonymous --password '' \
    $optimis/InterCloudSecurity/IPS/Agents/CentOS6/  IPS   

## install context files for OpenNebula
install -d /mnt/context
chmod 755 linux/*
cp -vf linux/context_* /etc/init.d/

chkconfig --del context_network
chkconfig --add context_network 
chkconfig --level 2345 context_network on

chkconfig --del context_application
chkconfig --add context_application
chkconfig --level 2345 context_application on

chkconfig ntpd on
chkconfig tomcat6 on
chkconfig lighttpd on
chkconfig openvpn on

# change the group permission of tomcat
chown root:tomcat  /usr/share/tomcat6/

# NOTE: if want to increase the tomcat heap size
# $ vim /usr/sbin/tomcat6   
# line 29: JAVA_OPTS="$JAVA_OPTS -Xms512m -Xmx1024m"
#echo "JAVA_OPTS=\"$JAVA_OPTS -Xms512m -Xmx1024m\"  in /usr/sbin/tomcat6 line 29"

## install Data Manager component
cd PM-datamanager-mounting
chmod 755 *
./install-optimis-datamanager.sh
echo "Installation of OPTIMIS Data Manager is done."
cd ..


## install the secure cloud package
chmod 755 CentOS6/*.*
echo "Installing Secure Cloud package ..."
if [ "$ARCH" = "x86_64" ]; then    
    rpm -ivh IPS/*.x86_64.rpm
    ./CentOS6/*.x86_64.bin
else
    rm -vf CentOS6/*.x86_64.bin
    rm -vf IPS/*.x86_64.rpm    
    rpm -ivh IPS/*.rpm
    ./CentOS6/*.bin
fi


## remove old archieved packages
yum clean packages > /dev/null
yum clean all > /dev/null
chmod 755 /etc/init.d/*



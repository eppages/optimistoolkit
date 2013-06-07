#!/bin/bash

cp optimis-mount  /usr/bin/
cp optimis-umount /usr/bin
cp optimis-datamanager /etc/init.d/
chmod +x /etc/init.d/optimis-datamanager

if [ -f /etc/debian_version ]; then
    OS=Debian
    apt-get install sshfs
    apt-get install python
    update-rc.d optimis-datamanager defaults
elif [ -f /etc/redhat-release ]; then
    OS=Centos
    arch=`uname -i`
    CentosVersion=`cat /etc/redhat-release|awk '{ print $3;}'`
    CentosEL="el${CentosVersion:0:1}"
    wget http://packages.sw.be/rpmforge-release/rpmforge-release-0.5.2-2.$CentosEL.rf.$arch.rpm
    rpm --import http://apt.sw.be/RPM-GPG-KEY.dag.txt
    rpm -K rpmforge-release-0.5.2-2.*.rpm
    rpm -i rpmforge-release-0.5.2-2.*.rpm
    yum search sshfs
    yum install fuse-sshfs.$arch
    yum install python
    rm rpmforge-release-0.5.2-2.*.rpm
    chkconfig --add optimis-datamanager
    chkconfig --level 345 optimis-datamanager on
fi




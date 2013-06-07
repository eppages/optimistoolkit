#!/bin/bash

cp optimis-download-server /usr/bin/
cp optimis-download-manager /etc/init.d/

chmod +x /etc/init.d/optimis-download-manager
chmod +x /usr/bin/optimis-download-server

if [ -f /etc/debian_version ]; then
    OS=Debian
    apt-get update
    apt-get install python-setuptools -y
    apt-get install python-dev -y
    easy_install msgpack-python
    easy_install msgpack-rpc-python
    update-rc.d optimis-download-manager defaults
elif [ -f /etc/redhat-release ]; then
    OS=Centos
    # TODO
    chkconfig --add optimis-download-manager
    chkconfig --level 345 optimis-download-manager on
fi




#!/bin/bash

REMOTEHOST=10.157.128.20

curl $REMOTEHOST/nagios/xml/status2xml.cgi?"001+'count_of_users','cpu_average_load','disk_free_space','Downstream','free_memory','status','Upstream','cpu_speed','hardware_error'"


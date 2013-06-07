#!/bin/bash

REMOTEHOST=10.157.128.20

curl $REMOTEHOST/nagios/xml/status2xml.cgi?"004+'mac_address','fqdn','last_reboot','disk_total_space','total_memory','No_of_cores'"


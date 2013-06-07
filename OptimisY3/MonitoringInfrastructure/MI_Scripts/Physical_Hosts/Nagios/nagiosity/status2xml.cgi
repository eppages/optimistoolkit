#!/usr/bin/env python
"""

This takes the nagios realtime status data and outputs it as XML.

Arguments:
Collector Id - id number of the collector.
List of accepted services - tells which services must be parsed from the status file.

"""

__author__ =  'Gregorios Katsaros, Pierre Gilet'
__version__=  '1.3'

import re
import sys, os
import datetime
from configobj import ConfigObj

# Config local access control permission to enable the file to be readable by this script
config = ConfigObj(os.path.realpath(os.path.dirname(sys.argv[0]))+"/status2xml.properties")
status_file = config['status_file']

# Warning: the following tokens change depending on the version of Nagios 
hosttoken='hoststatus'
servicetoken='servicestatus'
programtoken='programstatus'

# List of services to parse from the status file
collectorid=sys.argv[1]

# List of services to parse from the status file
acceptedservices=sys.argv[2]

def GetDefinitions(filename,obj):
    """ Parse the status.dat file and extract matching object definitions """
    file=open(filename)
    content=file.read().replace("\t"," ")
    file.close
    pat=re.compile(obj +' \{([\S\s]*?)\}',re.DOTALL)
    finds=pat.findall(content)
    return finds

def GetDirective(item,directive):
    """ Parse an object definition, return the directives """
    pat=re.compile(' '+directive + '[\s= ]*([\S, ]*)\n')
    m=pat.search(item)
    if m:
        return m.group(1)

def xmlell(definition,directive):
    """ Returns directive='value' """
    return "%s" % (GetDirective(definition,directive).strip())

def main():
    """ Parse and output """
    print "Content-type: text/xml\n"
    output="<?xml version=\"1.0\"?>\n"
    output+="<MonitoringResources>\n"
    
    # Information about Nagios running state
    prog=GetDefinitions(status_file,programtoken)
    for progdef in prog:
        services=GetDefinitions(status_file,servicetoken)
        for servicedef in services:
            servicedesc=xmlell(servicedef,"service_description")
            if servicedesc in acceptedservices:
               temp=xmlell(servicedef,"plugin_output")
               value_unit=temp.split(" ")
               # Don't output anything if Nagios returned an error for this metric.
               if ('NRPE:' not in value_unit[0]) and ('Connection' not in value_unit[0]) and (value_unit[0] != 'N/A'):
                  output+="<monitoring_resource>\n"
                  output+="<physical_resource_id>"+xmlell(servicedef,"host_name")+"</physical_resource_id>\n"
                  output+="<metric_name>"+servicedesc+"</metric_name>\n"
                  output+="<metric_value>"+value_unit[0]+"</metric_value>\n"
                  try:
                     output+="<metric_unit>"+value_unit[1]+"</metric_unit>\n"
                  except IndexError:
                     output+="<metric_unit></metric_unit>\n"
                  output+="<metric_timestamp>"+xmlell(servicedef,"last_check") +"</metric_timestamp>\n"
                  output+="<service_resource_id></service_resource_id>\n"
                  output+="<virtual_resource_id></virtual_resource_id>\n"
                  output+="<resource_type>physical</resource_type>\n"
                  output+="<monitoring_information_collector_id>"+collectorid+"</monitoring_information_collector_id>\n"
                  output+="</monitoring_resource>\n"
    output+="</MonitoringResources>\n"
    print output

if __name__ == "__main__":
    sys.exit(main())


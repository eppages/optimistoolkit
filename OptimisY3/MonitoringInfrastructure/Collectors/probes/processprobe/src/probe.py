#!/usr/bin/env python
"""
Copyright (C) 2011 Umeå University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

"""

__author__ = "Daniel Esping"
__version__ = "0.1"
__copyright__ = "Copyright (c) 2011 Daniel Espling, Umeå University"
__license__ = "GNU v3"

import sys

from restclient import sendPost
from countprocesses import countRunningProcesses

#TODO: Read from context
def getServiceID():
    return "some.service.id" #<- Fake service ID

#TODO: Read from context
def getInstanceID():
    return "some.instance.id" #<- Fake instance ID

#TODO: Read from context
def getMonitoringEndpoint():
    return "http://localhost:8087" #<- Real endpoint for the service monitoring component

#TODO: Read from context
def getKPIName():
    return 'test.process.kpi' #<- KPI name

def measureAndPost(processName):
    serviceID = getServiceID()
    instanceID = getInstanceID()
    monitoringEndpoint = getMonitoringEndpoint()
    kpiName = getKPIName()
    value = countRunningProcesses(processName)

    print "sending value: ", value
    return sendPost(serviceID, instanceID, monitoringEndpoint, kpiName, value)

def main(args): 
    if len(args) < 2:
        print >> sys.stderr, "first parameter should be the process name to monitor"
        return -1
    else:       
        processName = args[1]
        try:
            res = measureAndPost(processName)
            print "Meaure and POST returned:", res
            return 0;
        except Exception, e:
            print e
            
    return -1

if __name__ == '__main__':
    result = main(sys.argv)
    sys.exit(result)

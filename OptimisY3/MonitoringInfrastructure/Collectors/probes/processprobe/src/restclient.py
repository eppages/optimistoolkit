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
import time

sys.path.append("python-rest-client")

from restful_lib import Connection, ConnectionError
from datetime import datetime

def sendPost(serviceId, instanceId, monitoringEndpoint, kpiName, value):
    timestamp = time.mktime(datetime.now().timetuple()) #UTC-Seconds
    timestamp = long(timestamp) 

    conn = Connection(monitoringEndpoint)
    response = conn.request_post("/data/" + serviceId , args={"serviceId":serviceId, "instanceid":instanceId, "kpiName":kpiName, "value":value, "timestamp":timestamp})
    print "Response: ", response

    status = response.get('headers').get('status')
    if status not in ["200", 200, "204", 204]:
        print >> sys.stderr, "Call failed, status:", status 
        return False

    print "Call successful"
    return True

def main(args): 
    if len(args) < 6:
        print >> sys.stderr, "usage is: ", args[0], "<service id> <instance id> <service monitoring hostname:port> <kpi_name> <value>"
        return -1
    else:       
        serviceId = args[1]
        instanceId = args[2]
        #TODO: Make parsing host and port more rubust
        monitoringEndpoint = args[3]
        kpiName = args[4]
        value = args[5]

	if not monitoringEndpoint.startswith('http://'):
	    monitoringEndpoint = ''.join(['http://', monitoringEndpoint])
	    print "New endpoint: ", monitoringEndpoint

        try:
            res = sendPost(serviceId, instanceId, monitoringEndpoint, kpiName, value)
            print "Call result:", res
            return 0;
        except Exception, e:
            print e
            
    return -1

if __name__ == '__main__':
    result = main(sys.argv)
    sys.exit(result)

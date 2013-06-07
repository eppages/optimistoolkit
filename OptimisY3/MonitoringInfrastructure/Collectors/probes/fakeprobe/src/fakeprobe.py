#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Copyright (C) 2011-2013 Umea University

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

def main(args): 
    if len(args) < 6:
        print >> sys.stderr, "Usage: ./fakeprobe.py <monitoringEndpoint> <serviceID> <instanceID> <kpiName> <value>"
        print >> sys.stderr, "For example: ./fakeprobe.py http://localhost:8087 myService instance-1 ThreadCount 30"
        return -1
    else:       
        
        monitoringEndpoint = args[1]
        serviceID = args[2]
        instanceID = args[3]
        kpiName = args[4]
        value = args[5]

        try:
            return sendPost(serviceID, instanceID, monitoringEndpoint, kpiName, value)
        except Exception, e:
            print e
            
    return -1

if __name__ == '__main__':
    result = main(sys.argv)
    sys.exit(result)

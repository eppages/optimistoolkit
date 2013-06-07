#!/usr/bin/python2.7

"""
Get metric value from the Racktivity sensor

This script queries a Racktivity sensor to get back some energy related metrics and output them in an XML structure.

Arguments:
GUID - tells which metric type is to be retrieved from the Racktivity sensor. Accepted values are:
* 6 for current (A),
* 7 for real power (W),
* 9 for real energy (kWh),
* 10 for apparent energy (kVAh),
* 15 for apparent power (VA),
* 16 for power factor (%).
RANGE - gives the range of ports for which the metric value is to be retrieved. Accepted values are: 1-8
BASEDIR - directory where the property file is located.
TIMESTAMP - timestamp (Unix time) used to flag the energy metrics.

"""
__author__ =  'Gregorios Katsaros, Pierre Gilet'
__version__=  '2.0'

import urllib2, urllib
from cookielib import CookieJar
from struct import unpack
import sys
from configobj import ConfigObj

# Needed to convert the raw result returned by the sensor
# into a value that makes sense for a human being.
def bin2int(data):
    nr = 0
    for idx, byte in enumerate(data):
        nr += ord(byte)*(256**idx)
    return nr

# Check input arg.
if len(sys.argv) != 5:
   print "Usage: " + sys.argv[0] + " <GUID> <RANGE> <BASEDIR> <TIMESTAMP>"
   sys.exit(1)
else:
   guid = int(sys.argv[1])
   port_range = int(sys.argv[2])
   timestamp = sys.argv[4]

if port_range <1 or port_range >8:
   print "Error: incorrect value for the argument RANGE (accepted values are: 1-8)"
   sys.exit(1)

config = ConfigObj(sys.argv[3]+"/racktivity.properties")

username = config['username']
password = config['password']
hostname = config['hostname']

# Set up some vars.
if guid == 6:
   scale=0.001
   metric_name='current'
   metric_unit='A'
elif guid == 7:
   scale=1
   metric_name='real_power'
   metric_unit='W'
elif guid == 9:
   scale=0.001
   metric_name='real_energy'
   metric_unit='kWh'
elif guid == 10:
   scale=0.001
   metric_name='apparent_energy'
   metric_unit='kVAh'
elif guid == 15:
   scale=1
   metric_name='apparent_power'
   metric_unit='VA'
elif guid == 16:
   scale=1
   metric_name='power_factor'
   metric_unit='%'
else:
   print "Error: incorrect argument value"
   sys.exit(1)

# Get the cookie.
cj = CookieJar()
opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(cj))
values = {'username':username, 'password':password}
data = urllib.urlencode(values)
response = opener.open('http://'+hostname+'/login.htm', data)

# Start building the XML returned at the end of the script.
output = ""

for current_index in range(1, port_range+1):
        try:
           # Get port name.
           out = opener.open('http://'+hostname+'/API.cgi?ADDR=P1&GUID=10034&TYPE=G&INDEX='+str(current_index)+'&COUNT=1')
           port_name = out.read()

           # Execute request to get the metric value from the sensor.
           out = opener.open('http://'+hostname+'/API.cgi?ADDR=P1&GUID='+str(guid)+'&TYPE=G&INDEX='+str(current_index)+'&COUNT=1')
           result = out.read()

           raw_result=''
           for array_index in range(1, len(result)):
                 raw_result+=result[array_index]

           final = bin2int(raw_result)*scale
        except:
           # We end up here when a problem has occurred while running the request (eg. a time out event).
           final=-1
        finally:
           output+=port_name+"|"+metric_name+"|"+str(final).replace(",", ".")+"|"+metric_unit+"|"+timestamp+"\n"

print output


"""
Copyright 2012 University of Stuttgart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This python code reads one message at a time from RabbitMQ's mi_queue buffer.
Then, it passes the message (which contains the location of XML file) to
monitoring_parser.py for further process (who writes the information to MySQL).
So, 1 message ==> 1 instance of monitoring_parser.py (running as a separate
process in the background)
Finally, it notifies Aggregator by sending the XML file via a POST method.
Usage: python receiver.py
"""

__author__ =  'Anthony Sulistio'
__version__=  '1.0'

#import urllib
import pycurl
import pika
import subprocess, os, sys

## important parameters
hostName = 'localhost'
url = "http://" + hostName + ":8080/Aggregator/Aggregator/realtime/monitoringresources/"
queueName = 'mi_queue'
monitoring_path="/opt/optimis/MonitoringInfrastructure"

# check if file exists
if os.path.exists(monitoring_path + "/rabbitmq/monitoring_parser.py") == False:
    print "receiver.py -- Error: monitoring_parser.py does not exist. Exit the program"
    sys.exit(0)

## get DB properties
db_properties = monitoring_path + "/share/database.properties"   # filename

print "Using the following parameters:"
print "* Aggregator URL = " + hostName
print "* database properties = " + db_properties
print 'To exit press CTRL+C or use the kill command'
print

try:
    f = open(db_properties, 'r')
    for line in f:
        list = line.split('=')
        for val in list:
            if val == "SQLHOST":
                #print "[" + list[1].strip() + "]"
                SQLHOST = list[1].strip()
            elif val == "SQLUSER":
                #print "[" + list[1].strip() + "]"
                SQLUSER = list[1].strip()
            elif val == "SQLPASSWORD":
                #print "[" + list[1].strip() + "]"
                SQLPASSWORD = list[1].strip()
            elif val == "SQLDATABASE":
                #print "[" + list[1].strip() + "]"
                SQLDATABASE = list[1].strip()
    f.close()
except IOError:
   print "receiver.py -- Error: " + db_properties + " does not exist."
   sys.exit(0)


## connection to RabbitMQ
connection = pika.BlockingConnection(pika.ConnectionParameters(host=hostName))
channel = connection.channel()
channel.queue_declare(queue=queueName)

#print
#print '[*] Waiting for messages. To exit press CTRL+C'

def callback(ch, method, properties, body):

    # check first if file exists or not
    if os.path.exists(body) == False:
        #print "receiver.py: Warning -- %r" % (body,) + " does not exist. So skipped."
        return

    # need to find out which resource type it belongs to
    if body.find('/physical/') >= 0:
        resourceType = "physical"
    elif body.find('/energy/') >= 0:
        resourceType = "energy"
    elif body.find('/virtual/') >= 0:
        resourceType = "virtual"
    elif body.find('/service/') >= 0:
        resourceType = "service"

    #print "[x] Received %r" % (body,)
    #print "-- " + resourceType

    # using POST upload the XML file to Aggregator
    #print "receiver.py -- Sending " + body + " via POST to " + url + resourceType
    curl = pycurl.Curl()
    curl.setopt(curl.POST, 1)
    curl.setopt(curl.URL, url + resourceType)
    curl.setopt(curl.CONNECTTIMEOUT, 5)  # in seconds
    curl.setopt(curl.TIMEOUT, 8)
    curl.setopt(curl.FAILONERROR, True)
    curl.setopt(curl.HTTPHEADER, ['Content-Type: text/plain'])
    #curl.setopt(curl.VERBOSE, True)   # debugging mode
    filesize = os.path.getsize(body)
    curl.setopt(curl.POSTFIELDSIZE, filesize)
    f = open(body, 'r')
    curl.setopt(curl.READFUNCTION, f.read)
    try:
        curl.perform()
    except pycurl.error, error:
        print 'receiver.py -- Error code: ', error[0]
        print 'receiver.py -- Error message: ', error[1]
    curl.close()
    f.close()
        
    # rename the file in case future messages contain the same filename
    filename = body + ".txt"
    os.rename(body, filename)
    #print "receiver.py -- Rename this file to " + filename

    # pass the body message to another python module
    val = "python2.7 " + monitoring_path + "/rabbitmq/monitoring_parser.py " + filename + " " + SQLHOST + " " + SQLUSER + " " + SQLPASSWORD + " " + SQLDATABASE + " &"
    #print "Send to " + val
    os.system(val);


channel.basic_consume(callback, queue=queueName, no_ack=True)
channel.start_consuming()


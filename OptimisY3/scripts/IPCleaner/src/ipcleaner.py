#!/usr/bin/python

import httplib
import MySQLdb as dbapi
import os
import sys

CO = dict()
VMM = dict()

try:
    for line in open(os.environ["OPTIMIS_HOME"]+"/etc/CloudOptimizer/config.properties"):
        ls = line.strip().split("=");
        if(len(ls)==2):
            CO[ls[0]]=ls[1]
    for line in open(os.environ["OPTIMIS_HOME"]+"/etc/VMManager/config.properties"):
        ls = line.strip().split("=");
        if(len(ls)==2):
            VMM[ls[0]]=ls[1]
     
except KeyError, e:
    print "Error: OPTIMIS_HOME environment variable is not set"
    sys.exit(-1)
except IOError, e:
    print "Error:", e.args[1]
    sys.exit(-1)

host = VMM["config.drp_host"]
port = VMM["config.drp_port"]
app = "DRP"
path = "compute"
url = "http://" + host + ":" + port + "/" + app + "/" + path

print "*** Going to delete VMs from", url, "***"

hc = httplib.HTTPConnection(host,port)
hc.request("GET", "/"+ app+"/"+path)
resp = hc.getresponse()
#print resp.status , resp.reason
rawxml = resp.read()
hc.close()
ids = rawxml.split("<list>"); 

if len(ids) <= 1:
    print "Nothing to delete"
else:
    for i in range(1, len(ids)):
        vmid = ids[i].split("</list>")[0]
        print "Deleting", vmid, "..."
        hc = httplib.HTTPConnection(host,port)
        hc.request("DELETE","/"+app+"/"+path+"/"+vmid)
        resp = hc.getresponse()
        resp.read()
        hc.close()
        
print "*** Going to cleanup Virtual Resources Database ***"

dbuser = CO["db.username"]
dbpwd = CO["db.password"]
dburl = CO[CO["db.location"]+".url"]

try:
    parts = dburl.replace("jdbc:mysql://", "").split(":");
    dbhost = parts[0]
    print "db host:",dbhost
    parts = parts[1].split("/");
    dbport = parts[0]
    dbname = parts[1]
    print "db port:",dbport
    print "db name:",dbname
    
    conn = dbapi.connect(host=dbhost, port=int(dbport), user=dbuser, passwd=dbpwd, db=dbname)
    
    
    cursor = conn.cursor()
    cursor.execute("DELETE FROM virtual_resource")
    cursor.close()
    conn.close()
    
    print "All data has been removed"
 
except dbapi.Error, e:
    print "Error %d: %s" % (e.args[0],e.args[1])

#!/usr/bin/env python

import os
import sys
import socket
import shutil
import ConfigParser

slaServerFiles = [
    "optimis-sla-server/conf/server.xml",
    "optimis-sla-server/webapps/optimis-sla/WEB-INF/classes/wsrf-engine.config",    
    "optimis-sla-server/webapps/optimis-sla/WEB-INF/classes/dbstorage.properties",
    "optimis-sla-server/webapps/optimis-sla/WEB-INF/classes/META-INF/persistence.xml",
    "optimis-sla-server/webapps/optimis-sla/WEB-INF/classes/log4j.properties",
    ]

# reading config file location
if len(sys.argv) == 2:
    configFile = sys.argv[1]
else:
    print "You have to specify (only) the configuration file location."
    sys.exit(1)

print "Using configuration file: " + configFile


# loading config file
config = ConfigParser.ConfigParser()
config.optionxform=str
config.read([configFile])

files = []
files += slaServerFiles

installdir=os.getcwd()

optimis_sla_parameters=config.options("optimis_sla_parameters")
print optimis_sla_parameters

for f in files:
    filename=installdir+"/"+f
    try:

        print "... processing %s" % filename

        #if not exists, make a copy of the original config file
        if not os.path.isfile(filename+"_origin"):
            print "    making backup %s" % filename+"_origin"
            shutil.copy(filename,filename+"_origin")

        file = open(filename+"_origin")
        lines=file.readlines()
        file.close()

        file = open(filename, 'w')

        for line in lines:
            for param in optimis_sla_parameters:
                key= "${"+param+"}"
                val= config.get("optimis_sla_parameters",param)
                #print "%s, %s" % (key, val)
                if(val=="hostname"):
                    #val=hostname
                    val=socket.getfqdn()
                if(val=="currentdir"):
                    #val=installdir
                    val=os.getcwd()
                line=line.replace(key,val)
                line=line.replace("${FILE_SEPARATOR}", "/")
                #line=line.replace("${USER_NAME}", xlogin) 
            file.write(line)
        file.close()

    except:
        print "Error processing", filename
        print sys.exc_info()

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

This python code reads a XML file (as an input) and prints SQL syntax to stdout.
It is valid only for the monitoring values of energy, physical, service and virtual.
Usage: python monitoring_parser.py filename.xml

This modified code was initially taken from 
http://code.activestate.com/recipes/65248-parsing-an-xml-file-with-xmlparsersexpat/
"""

__author__ = "Anthony Sulistio"
__version__ = "1.0"

import subprocess, os
import uuid
import xml.parsers.expat, sys
from datetime import datetime
from time import strftime


class XMLParser:

    # prepare for parsing
    def __init__(self, xml_file, fileObj):
        assert(xml_file != "")
        self.Parser = xml.parsers.expat.ParserCreate()

        self.xml_file = xml_file
        self.fileObj = fileObj
        self.Parser.CharacterDataHandler = self.handleCharData
        self.Parser.StartElementHandler = self.handleStartElement
        self.Parser.EndElementHandler = self.handleEndElement
        self.is_physical_resource_id, self.is_metric_name = 0, 0;
        self.is_metric_value, self.is_metric_unit, self.is_metric_timestamp = 0, 0, 0;
        self.is_service_resource_id, self.is_virtual_resource_id, self.is_resource_type = 0, 0, 0;
        self.is_mi_collector_id = 0;
        self.output = "";


    # parse the XML file
    def parse(self):
        try:
            self.Parser.ParseFile(open(self.xml_file, "r"))
        except:
            print "monitoring_parser.py -- ERROR: Can't open " + self.xml_file + " file. So skipped."
            sys.exit(0)


    # a function for the start element, e.g. <monitoring_resource> or <metric_name>        
    def handleStartElement(self, name, attrs): 
        if name == 'physical_resource_id':
            self.is_physical_resource_id = 1;
            self.physical_resource_id = "";
        elif name == 'metric_name':
            self.is_metric_name = 1;
            self.metric_name = "";
        elif name == 'metric_value':
            self.is_metric_value = 1;
            self.metric_value = "";
        elif name == 'metric_unit':
            self.is_metric_unit = 1;
            self.metric_unit = "";
        elif name == 'metric_timestamp':
            self.is_metric_timestamp = 1;
            self.metric_timestamp = "";
        elif name == 'service_resource_id':
            self.is_service_resource_id = 1;
            self.service_resource_id = "";
        elif name == 'virtual_resource_id':
            self.is_virtual_resource_id = 1;
            self.virtual_resource_id = "";
        elif name == 'resource_type':
            self.is_resource_type = 1;
            self.resource_type = "";
        elif name == 'monitoring_information_collector_id':
            self.is_mi_collector_id = 1;
            #self.resource_type = "";
        return


    # a function for the end element, e.g. </monitoring_resource> or </metric_name>        
    def handleEndElement(self, name): 
        if name == 'MonitoringResources':
            val = self.output + ";"     # put ";" at the end of SQL statement
            self.fileObj.write(val + '\n')
            #print val
        elif name == 'monitoring_resource':
            if len(self.output) > 0:
                val = self.output + "," # print values of previous <monitoring_resource>
                self.fileObj.write(val + '\n')
                #print val

            row_id = uuid.uuid4()    # random values
            SEP = "', '"
            self.output = "('" + str(row_id) + SEP + self.physical_resource_id \
                + SEP + self.virtual_resource_id \
                + SEP + self.resource_type + SEP + self.metric_name \
                + SEP + self.metric_value + SEP + self.metric_unit \
                + SEP + self.metric_timestamp + SEP + self.service_resource_id + "')";
        elif name == 'physical_resource_id':
            self.is_physical_resource_id = 0;
        elif name == 'metric_name':
            self.is_metric_name = 0;
        elif name == 'metric_value':
            self.is_metric_value = 0;
        elif name == 'metric_unit':
            self.is_metric_unit = 0;
        elif name == 'metric_timestamp':
            self.is_metric_timestamp = 0;
        elif name == 'service_resource_id':
            self.is_service_resource_id = 0;
        elif name == 'virtual_resource_id':
            self.is_virtual_resource_id = 0;
        elif name == 'resource_type':
            self.is_resource_type = 0;
        elif name == 'monitoring_information_collector_id':
            self.is_mi_collector_id = 0;
        return


    # a function to get the data inside an element
    def handleCharData(self, data): 
        if self.is_physical_resource_id == 1:
            self.physical_resource_id = data;
        elif self.is_metric_name == 1:
            self.metric_name = data;
        elif self.is_metric_value == 1:
            self.metric_value = data;
        elif self.is_metric_unit == 1:
            self.metric_unit = data;
        # need to convert to the ISO 8601 time format, e.g. 2007-03-04 20:32:17 
        elif self.is_metric_timestamp == 1:
            #val = datetime.fromtimestamp(long(data))
            #self.metric_timestamp = val.strftime("%Y-%m-%d %H:%M:%S")
            self.metric_timestamp = data
        elif self.is_service_resource_id == 1:
            self.service_resource_id = data;
        elif self.is_virtual_resource_id == 1:
            self.virtual_resource_id = data;
        # for monitoring_information_collector_id
        elif self.is_resource_type == 1:
            #if data == 'physical':
            #    self.resource_type = '001'; 
            #elif data == 'energy':
            #    self.resource_type = '003'; 
            #elif data == 'virtual':
            #    self.resource_type = '007'; 
            if data == 'service':
                self.resource_type = data;  # own ID, e.g. system-optimis-pm-allcores_instance-1
        elif self.is_mi_collector_id == 1:
            self.resource_type = data;
        return


# a function that prints the header of SQL syntax
def printHeader(filename, fileObj):
    header = "# Parsed from " + filename + "\n"
    #print header
    fileObj.write(header)

    val1 = "INSERT INTO `monitoring_resource_"
    val2 = "` (`row_id`, `physical_resource_id`, `virtual_resource_id`, `monitoring_information_collector_id`, `metric_name`, `metric_value`, `metric_unit`, `metric_timestamp`, `service_resource_id`) VALUES" 

    file = open(filename, 'r')
    for line in file.readlines():
        if line.find("<resource_type>physical") >= 0:
            val = val1 + "physical" + val2
            break
        elif line.find("<resource_type>energy") >= 0:
            val = val1 + "energy" + val2
            break
        elif line.find("<resource_type>virtual") >= 0:
            val = val1 + "virtual" + val2
            break
        elif line.find("<resource_type>service") >= 0:
            val = val1 + "service" + val2
            break
    
    #print val
    file.close()
    fileObj.write(val + "\n")

def main(): 
    #print sys.argv
    if len(sys.argv) < 2:
        print "Usage: python monitoring_parser.py filename.xml"
        sys.exit(0)

    # check if file exists
    xmlFile = sys.argv[1]
    if os.path.exists(xmlFile) == False:
        #print "monitoring_parser.py -- Warning: " + xmlFile + " does not exist. So skipped."
        return

    #filename = str(uuid.uuid4()) + ".sql"    # random values
    filename = "/tmp/" + str(os.getpid()) + ".sql"    # based on process ID
    #filename = "/tmp/" + xmlFile + ".sql"
    #print filename
    f = open(filename, 'w')  # open and write the sql file

    # if argv[1] contains *.xml value
    #for arg in sys.argv[1:]:
        #print "Arg: %s"%arg

    # parse the given XML file 
    p = XMLParser(xmlFile, f)
    printHeader(xmlFile, f)
    p.parse()

    # close the sql file
    f.close()

    # write the data into MySQL database
    if len(sys.argv) >= 5:
        SQLHOST = sys.argv[2]
        SQLUSER = sys.argv[3]
        SQLPASSWORD = sys.argv[4]
        SQLDATABASE = sys.argv[5]
        val = "mysql -h "+SQLHOST+" -u "+SQLUSER+" -p"+SQLPASSWORD+" -D "+SQLDATABASE+" < "+filename

        # Debugging
        #mycmd1="echo '" + val + "' >>/tmp/monitoring_parser.log" 
        #os.system(mycmd1)
        #mycmd2="cat " + filename + " >>/tmp/monitoring_parser.log"
        #os.system(mycmd2)

        subprocess.call(val, shell=True)  # write to MySQL database
        os.remove(filename)     # delete sql file
        os.remove(xmlFile)      # delete xml file

if __name__ == "__main__":
    try:
        main()
    except:
        #print "monitoring_parser.py -- Exits the program"
        sys.exit(0)


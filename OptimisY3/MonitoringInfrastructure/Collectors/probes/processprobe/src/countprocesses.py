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
import os
from subprocess import Popen, call, PIPE

def findProcesses(processName):
    try:
        process = Popen(["pgrep", processName], stdout=PIPE)
        output = process.communicate()[0]
        print "check_ps_cmd output: ", output
        return output       
    except Exception, e:
        print >>sys.stderr, "Execution failed:", e
        return None

def countRunningProcesses(processName):
    std_output = findProcesses(processName)
    if std_output:
        std_output = std_output.split('\n')
        counter = 0
        for curline in std_output:
            if curline.strip() != "":
                counter += 1
    return counter


def main(args): 
    if len(args) < 2:
        print >> sys.stderr, "first parameter should be the process name"
        return -1
    else:       
        processName = args[1]
        try:
            res = countRunningProcesses(processName)
            print "Found running processes:", res
            return 0;
        except Exception, e:
            print e
            
    return -1

if __name__ == '__main__':
    result = main(sys.argv)
    sys.exit(result)

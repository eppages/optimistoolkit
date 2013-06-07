'''
Created on Feb 21, 2013

Configure working directory before launching
@author: kon
'''
import os
import unittest
import functions_input as fi
import functions_results as fr
import mode as mode_

class TestWidget(unittest.TestCase):
    def __init__(self, methodName='runTest'):
        self.folder = ".." + os.sep + "testcase1x"
        self.value = 212.999
        unittest.TestCase.__init__(self, methodName)
        
    def setUp(self):
        in_folder = os.getcwd() + os.sep\
            + self.folder + os.sep
        inp = fi.readinput(in_folder)
        self.r1 = fr.get_best_result(inp, 
                                     mode_.ModeAllocation.MIXED,
                                     mode_.ModeFederation.CUSTOM,
                                     mode_.ModeCost.INCLUDE_FEDERATION_COST,
                                     mode_.ModeHostSort.COST)

    def tearDown(self):
        del self.r1
        del self.folder

    def testObjective(self):
        self.assertEqual(self.r1[0], self.value, 
                         "Computed:" + str(self.r1[0]) 
                         + "!=Expected:" + str(self.value))

if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test1.testObjective', 'Test2.testObjective']
    unittest.main()
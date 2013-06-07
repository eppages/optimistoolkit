'''
Created on Feb 21, 2013

Configure working directory before launching
@author: kon
'''
import os
import unittest
from testObjectiveWidget import TestWidget

def suite():
    suite = unittest.TestSuite()
    test_parameters = [("set1" + os.sep + "testcase1", 252.999),
                       ("set1" + os.sep + "testcase2", 242.999),
                       ("set2" + os.sep + "testcase2", 212.999),
                       ("set2" + os.sep + "testcase6", 232.999),
                       ("set3" + os.sep + "testcase1", 112.0),
                       ("set3" + os.sep + "testcase2", 92.0)]
    
    for (folder,value) in test_parameters:
        loadedtests = unittest.TestLoader().loadTestsFromTestCase(TestWidget)
        for t in loadedtests:
            t.folder = folder
            t.value = value
        suite.addTests(loadedtests)
    return suite

if __name__ == "__main__":
    unittest.TextTestRunner(verbosity=2).run(suite())
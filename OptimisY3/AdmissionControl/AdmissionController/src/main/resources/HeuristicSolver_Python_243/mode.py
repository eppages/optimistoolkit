#!/usr/bin/python
'''
Created on Dec 20, 2012

@author: Psychas
'''

class ModeAllocation(object):
    '''
    enum simulation
    '''
    (MIXED)=range(1)

class ModeFederation(object):
    '''
    enum simulation
    '''
    (CUSTOM)=range(1)
    
class ModeCost(object):
    '''
    enum simulation
    '''
    (INCLUDE_FEDERATION_COST, EXCLUDE_FEDERATION_COST)=range(2)
    
class ModeHostSort(object):
    '''
    enum simulation
    '''
    (RANDOM, COST)=range(2)
    
class ObjectType(object):
    '''
    enum simulation
    '''
    (NULL, HOST, CLOUD)=range(3)

class MyError(Exception):
    ''' example usage:
        try:
            if (mode == 0):
                return 'zero'
            elif (mode == 1):
                return 'one'
            else:
                raise MyError(mode)
        except MyError as e:
            print 'Invalid value for mode in foo: ', e.value
    '''
    def __init__(self, value):
        '''
        Constructor
        '''
        self.value = value
    def __str__(self):
        return repr(self.value)

if __name__ == '__main__':
    print('Mixed attribute rank value:', ModeAllocation.MIXED)

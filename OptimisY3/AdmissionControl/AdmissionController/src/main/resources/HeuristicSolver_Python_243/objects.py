#!/usr/bin/python
from __future__ import division
import mode as mode_
import math

'''
Created on Dec 21, 2012

@author: Psychas
'''
class Service:
    """ class of service offered by a service provider """

    def __init__(self):
        # components that consist the service
        self.dic_com = {}
        # money paid if service fails (hardcoded)
        self.penalty = 0
        # minimum probability that the service will be available 
        # (if service is not rejected)
        self.avail = 0
        # money received if service is successful
        self.cost = 0
        #ecology of service (positive impact measured in cost units)
        self.eco = 0
        # risk of service (negative impact measured in cost units)
        self.risk = 0
        # trust of service provider (positive impact measured in cost units)
        self.trust = 0
        # true if federation is allowed else false
        self.federation = False;
        self.federation_cost_factor = 0.8
        self.penalty_factor = 0.5
    
    def set_cost_penalty(self, cost):
        self.cost = cost
        self.penalty = self.penalty_factor*cost
        
    def federate_max(self,wc,wr):
        return self.penalty_factor*wr > self.federation_cost_factor*wc
    def get_eco(self, u, we):
        return we*u*self.eco
    def get_risk(self, u, wr):
        return wr*u*self.risk
    def get_trust(self, u, wt):
        return wt*u*self.trust
    def get_gain(self, av, p, fed_cost, cost_mode, wc, we, wr, wt):
        """ av:  availability of allocation of service
            p:   acceptance percent
            fed_cost: federation cost of partly hosted components
            ret: gain based on current availability
            ret type: 'float'
            complexity: O(1)
        """
        u = av #av or p?
        
        if (cost_mode == mode_.ModeCost.EXCLUDE_FEDERATION_COST):
            return wc*u*self.cost + we*u*self.eco - wr*self.penalty*(1-av) - wr*u*self.risk + wt*u*self.trust -\
                wc*self.federation_cost_factor*fed_cost
        elif (cost_mode == mode_.ModeCost.INCLUDE_FEDERATION_COST):
            #federation cost is the same as the gain we would have
            #if components were not federated (wc*(1-p)*self.cost) multiplied by factor
            return wc*u*self.cost + we*u*self.eco - wr*self.penalty*(1-av) - wr*u*self.risk + wt*u*self.trust -\
                wc*self.federation_cost_factor*fed_cost
        
    def get_cpus(self):
        """ ret: all cpus needed from all components
            ret type: 'int'
            complexity: O(components)
        """
        return sum([c.get_cpus(c.elastic_vms) for c in self.dic_com.values()])
    
    def get_comb(self, fed_mode, cost_mode, wc, we, wr, wt):
        """ fed_mode: federation mode (allow federation or not)
            cost_mode: cost mode (include federation cost or not,
                        when federation is allowed)
            ret:   possible allocations, that exceed a value of availability,
                   of all components with their gain, total cpus, accepted percent
                   name for each component, vms and cpus per vm for each component
            ret type: 'list' ('tuple'('list'('tuple'('str','int','int')),'int','int','float'))
                   [([(comp_name, vms, cpus/vm)], total_cpus, gain, p)]
            complexity: O(elastic_vms^components) in common cases far less (at most components^2)
        """
        def include(val, cname, c, comps, fed_cost, groups_in, groups_out, fed_allowed):
            ret = []
            
            basic_vm_num = c.basic_vms
            elastic_vm_num = c.elastic_vms
            vm_num = basic_vm_num + elastic_vm_num
            if vm_num == basic_vm_num:
                ret += get_comb_fed_allowed(val + [(cname, c, basic_vm_num)],
                                            comps, fed_cost, groups_in, groups_out)
            else:
                if self.federate_max(wc,wr):
                    allocate_num = c.elastic_vms
                else:
                    allocate_num = int(c.get_min_elastic())
                if c.self_antiaff == mode_.ObjectType.CLOUD:
                    ret += get_comb_fed_allowed(val + [(cname, c, basic_vm_num)],
                                                comps, fed_cost,
                                                groups_in, groups_out)
                elif c.self_antiaff == mode_.ObjectType.HOST:
                    antigrouph = c.group_antiaff_host
                    if antigrouph == None:
                        #if component does not have antiaffinity host group
                        c.group_antiaff_host = cname
                    for i in range(basic_vm_num-1, vm_num):
                        t = i+1
                        val_n = [(cname, c, 1)]
                        for _ in range(t-1):
                            val_n.append((cname, c, 1))
                        c.accepted_vms = t
                        if (t >= allocate_num + basic_vm_num):
                            ret += get_comb_fed_allowed(val + val_n, comps,
                                                        fed_cost, groups_in, groups_out)
                        elif fed_allowed:
                            ret += get_comb_fed_allowed(val + val_n, comps,
                                                        fed_cost,
                                                        groups_in, groups_out)

                else:
                    if self.federate_max(wc,wr):
                        allocate_num = c.elastic_vms
                    else:
                        allocate_num = int(c.get_min_elastic())
                    for i in range(basic_vm_num - 1, vm_num):
                        t = i + 1
                        if (t >= allocate_num + basic_vm_num):
                            ret += get_comb_fed_allowed(val + [(cname,c,t)], comps,
                                                        fed_cost, groups_in, groups_out)
                        elif fed_allowed:
                            ret += get_comb_fed_allowed(val + [(cname,c,t)], comps,
                                                        fed_cost, groups_in, groups_out)
            return ret

        def get_comb_fed_not_allowed(val, comps, groups_out):
            """ Method that returns combinations if federation is not allowed
                av:    current availability before adding components "comps"
                val:   allocation of components already examined
                comps: components that have not been examined yet
                ret:   possible allocations of components that involve "val" with
                       their gain, total cpus, total bandwidth,
                       vms and cpus per vm for each component, bandwidth for each component
                ret type: 'list' ('tuple'('list'('tuple'('str','int','int','int')),'int','int','int'))
                       [([(comp_name, vms, cpus/vm, net)], total_cpus, total_net, gain)]
            """
            # all components examined
            if (comps == []):
                #add all components to list, compute total cpus and gain
                cpus_net = []
                total_cpus = 0
                total_acc_vms = 0
                total_vms = 0
                av = 0
                d = set()
                count = 0
                for (cname, c, n) in val:
                    if c.self_antiaff == mode_.ObjectType.HOST:
                        if cname not in d:
                            total_vms += c.basic_vms + c.elastic_vms
                            count +=1
                            d.add(cname)
                            #accepted vms only assigned when self.antiaff = 1
                            av += (c.accepted_vms - c.basic_vms + 1)/(c.elastic_vms + 1)
                    else:
                        total_vms += c.basic_vms + c.elastic_vms
                        count +=1
                        av += (n - c.basic_vms + 1)/(c.elastic_vms + 1)
                    total_cpus += n*c.virtual_cpus
                    total_acc_vms += n
                    cpus_net.append((cname, n, c.virtual_cpus))
                av /= count
                gain = self.get_gain(av, total_acc_vms/total_vms, 0, cost_mode, wc, we, wr, wt)
                return [(cpus_net, total_cpus, gain, total_acc_vms/total_vms, av)]
            else:
                cname, c = comps[0]

                basic_vm_num = c.basic_vms
                elastic_vm_num = c.elastic_vms
                vm_num = basic_vm_num + elastic_vm_num
                ret = []
                
                antigroupc = c.group_antiaff_cloud
                if antigroupc != None:
                    # if antigroup has more than one value no allocation is possible
                    if antigroupc in groups_out: raise ValueError
                    # else indicate there is at least one value already
                    else: groups_out.add(antigroupc)
                    #no need to remove group
                
                if vm_num == basic_vm_num:
                    ret += get_comb_fed_not_allowed(av, val + [(cname, c, basic_vm_num)], 
                                                    comps[1:], groups_out)
                else:
                    # if self antiaffinity is on cloud level no allocation is possible
                    if c.self_antiaff == mode_.ObjectType.CLOUD:
                        raise ValueError
                    elif c.self_antiaff == mode_.ObjectType.HOST:
                        antigrouph = c.group_antiaff_host
                        if antigrouph == None:
                            #if component does not have antiaffinity host group
                            c.group_antiaff_host = cname
                        for i in range(basic_vm_num - 1, vm_num):
                            t = i+1
                            p1 = 100*(t-basic_vm_num)/elastic_vm_num
                            if (p1 >= c.availability):
                                c.accepted_vms = t
                                val_n = [(cname, c, basic_vm_num)]
                                for _ in range(t - basic_vm_num):
                                    val_n.append((cname, c, 1))
                                ret += get_comb_fed_not_allowed(val + val_n,
                                                                comps[1:], groups_out)
                    else:
                        for i in range(basic_vm_num - 1, vm_num):
                            t = i+1
                            p1 = 100*(t - basic_vm_num)/elastic_vm_num
                            p2 = av*p1
                            if (p1 >= c.availability) or (p2 >= self.avail):
                                ret += get_comb_fed_not_allowed(p2/100, val + [(cname, c, t)], 
                                                                comps[1:], groups_out)
                return ret
                
        def get_comb_fed_allowed(val, comps, fed_cost, groups_in, groups_out):
            """ TODO Method that returns combinations if federation is not allowed
                av:    current availability before adding components "comps"
                val:   allocation of components already examined
                comps: components that have not been examined yet
                groups-in:  set of group of components to be included
                groups-out: set of group of components not to be included
                ret:   possible allocations of components that involve "val" with
                       their gain, total cpus, total bandwidth,
                       vms and cpus per vm for each component, bandwidth for each component
                ret type: 'list' ('tuple'('list'('tuple'('str','int','int','int')),'int','int','int'))
                       [([(comp_name, vms, cpus/vm, net)], total_cpus, total_net, gain)]
            """
            if (comps == []):
                #add all components to list, compute total cpus and gain
                cpus_net = []
                total_cpus = 0
                total_acc_vms = 0
                total_vms = 0
                federate_all = self.federate_max(wc,wr)
                av = 0
                d = set()
                count = 0
                for (cname, c, n) in val:
                    if c.self_antiaff == mode_.ObjectType.HOST:
                        if cname not in d:
                            count += 1
                            total_vms += c.basic_vms + c.elastic_vms
                            if federate_all: fed_vms = c.basic_vms + c.elastic_vms - c.accepted_vms
                            else: fed_vms = max(0, c.get_min_elastic() + c.basic_vms - c.accepted_vms)
                            av += (fed_vms + c.accepted_vms - c.basic_vms + 1)/(c.elastic_vms + 1)
                            fed_cost_per_vm = self.cost/((c.basic_vms + c.elastic_vms)*len(self.dic_com))
                            fed_cost += fed_vms*fed_cost_per_vm
                            d.add(cname)
                    else:
                        count += 1
                        total_vms += c.basic_vms + c.elastic_vms
                        if federate_all: fed_vms = c.basic_vms + c.elastic_vms - n
                        else: fed_vms = max(0, c.get_min_elastic() + c.basic_vms - n)
                        av += (fed_vms + n - c.basic_vms + 1)/(c.elastic_vms + 1)
                        fed_cost_per_vm = self.cost/((c.basic_vms + c.elastic_vms)*len(self.dic_com))
                        fed_cost += fed_vms*fed_cost_per_vm
                    total_cpus += n*c.virtual_cpus
                    total_acc_vms += n
                    cpus_net.append((cname, n, c.virtual_cpus))
                av /= count
                gain = self.get_gain(av, total_acc_vms/total_vms, fed_cost, cost_mode, wc, we, wr, wt)
                return [(cpus_net, total_cpus, gain, total_acc_vms/total_vms, av)]
            else:
                cname, c = comps[0]
                
                group = c.group_aff_cloud
                if group == None: group = c.group_aff_host;
                antigroup = c.group_antiaff_cloud
                
                if (group in groups_out) or (antigroup in groups_out):
                    # components should not be included
                    # possible but that means that there is no valid allocation
                    # no combination included
                    if group in groups_in:
                        return []
                    else:
                        c.accepted_vms = 0
                        ret = get_comb_fed_allowed(val + [(cname,c,0)],
                                                   comps[1:], fed_cost, groups_in,
                                                   groups_out)
                else:
                    if group in groups_in:
                        #component must be included and no vms federated
                        ret = include(val, cname, c, comps[1:], fed_cost, groups_in, groups_out, False)
                    else:
                        ret = []
                        #group of component included for the first time
                        #add and remove group in group_in and antigroup in group_out
                        if group != None: groups_in.add(group)
                        if antigroup != None: groups_out.add(antigroup)
                        if group != None:
                            #component must be included and no vms federated
                            ret = include(val, cname, c, comps[1:], fed_cost, groups_in, groups_out, False)
                        else:
                            #component must be included and vms possibly federated
                            ret = include(val, cname, c, comps[1:], fed_cost, groups_in, groups_out, True)
                        if group in groups_in: groups_in.remove(group)
                        if antigroup in groups_out: groups_out.remove(antigroup)
                        #group of component not included for the first time
                        #add and remove group in group_out
                        if group != None: groups_out.add(group)
                        c.accepted_vms = 0
                        ret += get_comb_fed_allowed(val + [(cname,c,0)],
                                                    comps[1:], fed_cost, groups_in,
                                                    groups_out)
                        if group in groups_out: groups_out.remove(group)
                return ret
        try:
            if (fed_mode == mode_.ModeFederation.CUSTOM):
                if self.federation:
                    return get_comb_fed_allowed([], self.dic_com.items(), 0, set(), set())
                else:
                    return get_comb_fed_not_allowed([], self.dic_com.items(), set())
            else:
                raise mode_.MyError(fed_mode)
        #occurs when no allocation is possible
        except ValueError:
            return []

#----
class Component:
    """ class of component which is included in a service """

    def __init__(self):
        # minimum number of vms to offer basic availability
        self.basic_vms = 0
        # each additional vm up to the maximum possible add further availability
        self.elastic_vms = 0
        # number of cpus per vm of component
        self.virtual_cpus = 0
        # minimum availability of component
        self.availability = 0
        #
        self.group_aff_cloud = None
        #
        self.group_aff_host = None
        #
        self.group_antiaff_cloud = None
        #
        self.group_antiaff_host = None
        #
        self.self_antiaff = None

    def get_cpus(self,el):
        """ el: Number of elastic vms 
            ret: cpus when "el" elastic vms are used
            complexity: O(1)
        """
        return self.virtual_cpus*(el + self.basic_vms)

    def get_min_elastic(self):
        """ ret: minimum elastic vms needed for component's minimum availability
            complexity: O(1)
        """
        return math.ceil(self.elastic_vms*self.availability)
    
    def get_av(self, n):
        """ n: number of elastic vms used
            ret: availability depending on number of elastic vms
            complexity: O(?)"""
        pass

#----
class Host:
    """ class of physical hosts where services are deployed """
    
    def __init__(self):
        # number of total cpus
        self.maxcpus = 0
        # number of reserved cpus
        self.rescpus = 0
        # cost for turning on the host
        self.cost = 0
        # ecology percent C(reduces cost)
        self.eco = 0

    def get_avail_cpus(self):
        """ ret: number of available cpus 
            complexity: O(1)
        """
        #TODO make faster -> use avail_cpus variable
        return self.maxcpus - self.rescpus

    def get_cost(self, wc, we):
        """ cpus: number of utilized cpus
            ret: cost for using "cpus" number of cpus
            complexity: O(1)
        """
        if (self.rescpus == 0):
            return wc*self.cost - we*self.cost*self.eco
        else:
            return 0

#---
class Input:
    """Wrapping class of all objects"""
    def __init__(self):
        self.dic_hos = {}
        self.dic_ser = {}
        self.dic_comb = {}
        self.cost_weight = 0
        self.eco_weight = 0
        self.risk_weight = 0
        self.trust_weight = 0

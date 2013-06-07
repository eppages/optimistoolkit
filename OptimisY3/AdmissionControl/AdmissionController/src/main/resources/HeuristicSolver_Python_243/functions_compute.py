#!/usr/bin/python
import sys
import logging as log
import mode as mode_
import mode

def get_total_cpus (inp):
    """ ret: minimum of total cpus in all hosts
             and all cpus needed from all services
        complexity: O(hosts + services)
    """
    available_cpus = sum([h.get_avail_cpus() for h in inp.dic_hos.values()])
    needed_cpus = sum([s.get_cpus() for s in inp.dic_ser.values()])

    return min(available_cpus, needed_cpus)

def get_total_cost (inp):
    """ ret: total cost of using all cpus in all hosts
        complexity: O(hosts)
    """
    return sum([h.get_cost(h.get_avail_cpus()) for h in inp.dic_hos.values()])

def get_mincost_cpu (inp):#XXX
    """ ret: list of minimum costs for any number of cpus
             starting from 0
        complexity: O(hosts*total_cpus*total_bandwidth)
    """
    cp = get_total_cpus(inp)
    total_cost = get_total_cost(inp)

    best = [total_cost]*(cp + 1)
    best[0][0] = 0

    for h in inp.hosts():
        best_t = [list(gain_list) for gain_list in best]
        for hcpus in range(h.get_avail_cpus()):#host_cpus
            v = h.get_cost(hcpus + 1)
            for acpus in range(cp + 1):#all_cpus
                uc2 = max(0, acpus - hcpus - 1)
                if (best_t[acpus] > best[uc2] + v):
                    best_t[acpus] = best[uc2] + v
        best = [list(cost_list) for cost_list in best_t]

    return best

def get_maxgain_cpu (inp, mode_federation, mode_cost):#XXX
    """ ret: list of maximum gains for any number of cpus
             starting both from 0
        complexity = O(services*components^x*total_bandwidth*total_cpus)
    """
    cp = get_total_cpus(inp)

    best = [0]*(cp + 1)
    for s in inp.dic_ser.values():
        best_t = [list(gain_list) for gain_list in best]
        
        combs = s.get_comb(mode_federation, mode_cost, inp.cost_weight, inp.eco_weight, inp.risk_weight, inp.trust_weight)
        log.debug("Combinations: %s", combs)
        for (_dump_list, cpus, gain) in combs:
            for cps in range(cp + 1):
                if (cps >= cpus):
                    if (best_t[cps] < best[cps-cpus] + gain):
                        best_t[cps] = best[cps-cpus] + gain

        best = [list(gain_list) for gain_list in best_t]

    return (best)

def get_maxgain_cpu_found_alloc (inp, mode_allocation, mode_federation, mode_cost, host_sort):#XXX
    """ inp: input object (contains any information of problem's input)
        mode_allocation: mode for allocation
        mode_federation: mode for federation
        mode_cost: include federation cost or not
        host_sort: metric for host sorting
        ret: list of maximum gains for any number of cpus
             starting from 0 for which an allocation is found
             and list the respective cpus used per component
        WARNING best of solution is not guaranteed even if all 
        allocations are successfully found
        complexity: O(services*components^2*total_cpus*O(is_found_allocation))
    """
    cp = get_total_cpus(inp)

    best = [0] * (cp + 1)
    inds = [[]] * (cp + 1)
    for (sname,s) in inp.dic_ser.items():
        best_t = list(best)
        inds_t = list(inds)

        combs = s.get_comb(mode_federation, mode_cost, inp.cost_weight, inp.eco_weight, inp.risk_weight, inp.trust_weight)
        log.debug("Combinations: %s", combs)
        for (cp_list, cpus, gain, p, av) in combs:
            for i in range(cp + 1):
                if (i >= cpus):
                    if (best_t[i] < best[i-cpus] + gain):
                        if (is_found_allocation(inp, inds[i-cpus] + [(sname, cpus, gain, p, av, cp_list)], mode_allocation, host_sort)):
                            best_t[i] = best[i-cpus] + gain
                            inds_t[i] = inds[i-cpus] + [(sname, cpus, gain, p, av, cp_list)]
        best = list(best_t)
        inds = list(inds_t)

    return (best, inds)

def is_found_allocation (inp, serv_alloc, mode, host_sort):#XXX
    """ inp: input object (contains any information of problem's input)
        serv_alloc: list of allocations of components of each service
                    format: [ (service, 
                                [ (vms, cpus_per_vm) ]*comp,
                                     total_cpus, gain 
                              )
                            ]*serv
        general constraints: CPUS used in a HOST NO MORE than AVAILABLE
                             all CPUS of a VM on the SAME HOST

        mode: 0 constraints: MIXED constraints based on affinity rule
        host_sort: metric for host sorting
        ret: True or False depending if an allocation is found
        complexity: O(services*components^x*components*cpus*components*vms)
                    usually O(services*components^x*components*components*vms)
    """
    if (mode == mode_.ModeAllocation.MIXED):#XXX
        for (sname, _tot_cpus, _gain, _p, _av, comp) in serv_alloc:
            # Entry: {group:[(vms,cps_pervm)]}
            same_host_dic = {}
            # Entry: {group:{hosts}}
            diff_host_dic = {}
            # Identify the groups of components to go to the same host
            service = inp.dic_ser[sname]
            for (cname, vms, cps_pervm) in comp:
                component = service.dic_com[cname]
                group = component.group_aff_host
                if group in same_host_dic:
                    same_host_dic[group].append((cname, vms, cps_pervm))
                else:
                    same_host_dic[group] = [(cname, vms, cps_pervm)]
            for name, comp in same_host_dic.items():
                if name == None:
                    # like mode 0 individual components same cloud
                    found = is_found0(same_host_dic[name], get_hosts(inp, host_sort), service, diff_host_dic)
                    if not found: return False
                    # like mode 1 all components same host
                else:
                    found = is_found1(same_host_dic[name], get_hosts(inp, host_sort), service, diff_host_dic)
                    if not found: return False
        return True
    else:
        raise mode_.MyError(mode)

#TODO cost does not depend on host
def min_cost_alloc(inp, inds, mode, host_sort):#XXX
    """ inp: input object (contains any information of problem's input)
        inds:
        mode: 0 constraints: MIXED constraints based on affinity rule
        ret: minimum cost of allocations in "inds" and allocation
            first fit

        complexity: O(services*components^x*components*cpus*components*vms)
                    usually O(services*components^x*components*components*vms)        
    """
    if (mode == mode_.ModeAllocation.MIXED):#XXX
        cpus = get_hosts(inp, host_sort)
        al_pat = {}
        s_accepted = {}
        for (sname, _tot_cpus, gain, p, av, comp) in inds:
            # Entry: {group:[(vms,cps_pervm)]}
            same_host_dic = {}
            # Entry: {group:{hosts}}
            diff_host_dic = {}
            service = inp.dic_ser[sname]
            for (cname, vms, cps_pervm) in comp:
                component = service.dic_com[cname]
                group = component.group_aff_host
                if group in same_host_dic:
                    same_host_dic[group].append((cname, vms, cps_pervm))
                else:
                    same_host_dic[group] = [(cname, vms, cps_pervm)]
            log.debug("Dictionary of components in the same affinity group:%s", same_host_dic)
            for name, comps in same_host_dic.items():
                # individual components anywhere
                if name == None:
                    found = min_cost0(comps, cpus, gain, sname, service, diff_host_dic, al_pat, s_accepted)
                    if not found:
                        log.warn('Unexpected non found allocation(possible inconsistency min_cost_alloc, is_found_alloc)') 
                        return (sys.maxint, al_pat, s_accepted)
                # all components same host
                else:
                    found = min_cost1(comps, cpus, gain, sname, service, diff_host_dic, al_pat, s_accepted)
                    if not found:
                        log.warn('Unexpected non found allocation(possible inconsistency min_cost_alloc, is_found_alloc)') 
                        return (sys.maxint, al_pat, s_accepted)
            s_accepted[sname] = (av, p, gain, comp)
        cost = 0
        for ((hname,rem),(_,avail)) in zip(cpus, get_hosts(inp, host_sort)):
            #estimate cost only if host is used
            if avail!=rem: cost += inp.dic_hos[hname].get_cost(inp.cost_weight, inp.eco_weight)
        return (cost, al_pat, s_accepted)
    else:
        raise mode_.MyError(mode)

def get_hosts(inp, host_sort):
    if host_sort == mode_.ModeHostSort.COST:
        sorted_list = sorted([(hname,host) for (hname,host) in inp.dic_hos.items()],
            key = lambda h: h[1].get_cost(inp.cost_weight, inp.eco_weight))
        return [(hname, host.get_avail_cpus()) for (hname, host) in sorted_list]
    elif host_sort == mode.ModeHostSort.RANDOM:
        return [(hname, host.get_avail_cpus()) for (hname, host) in inp.dic_hos.items()]
    else:
        raise mode_.MyError(mode)

# mode 0 individual components same cloud
def is_found0(comps, cpus, service, diff_host_dic):
    #iterate over components
    for (cname, vms, cpus_pervm) in comps:
        comp = service.dic_com[cname]
        antigroup1 = comp.group_antiaff_host
        isin1 = antigroup1 in diff_host_dic
        if antigroup1 != None:
            if not isin1:
                diff_host_dic[antigroup1] = set()
        if (sum([x[1] // cpus_pervm for x in cpus]) >= vms):
            host_set = set()
            for _ in range(int(vms)):
                found = False
                for j,(hname,total_cpus) in enumerate(cpus):
                    if (total_cpus >= cpus_pervm):
                        if isin1:
                            if hname in diff_host_dic[antigroup1]:
                                continue
                        cpus[j] = (hname,total_cpus-cpus_pervm)
                        host_set.add(hname)
                        found = True
                        break
                if not found: return False
            if antigroup1 != None:
                diff_host_dic[antigroup1].update(host_set)
        else: return False
    return True

# mode 3 all components same host
def is_found1(comps, cpus, service, diff_host_dic):
    tot_cpus = sum([c[1]*c[2] for c in comps])
    if tot_cpus == 0: return True
    found = False
    for j,(hname,avail_cpus) in enumerate(cpus):
        if (avail_cpus >= tot_cpus):
            okhost=True
            #check if there is conflict
            for (cname, _vms, _cpus_pervm) in comps:
                comp = service.dic_com[cname]
                antigroup1 = comp.group_antiaff_host
                if antigroup1 != None:
                    if not (antigroup1 in diff_host_dic):
                        diff_host_dic[antigroup1] = set()
                    elif hname in diff_host_dic[antigroup1]:
                        okhost = False
                        break
            if not okhost:continue
            cpus[j] = (hname, avail_cpus - tot_cpus)
            found = True
            #update antiaffinity sets of components
            for (cname, _vms, _cpus_pervm) in comps:
                comp = service.dic_com[cname]
                antigroup1 = comp.group_antiaff_host
                if antigroup1 != None:
                    diff_host_dic[antigroup1].add(hname)
            break
    return found

# mode 0 individual components same cloud
def min_cost0(comps, cpus, gain, sname, service, diff_host_dic, al_pat, s_accepted):
    #iterate over components
    for (cname, vms, cpus_pervm) in comps:
        comp = service.dic_com[cname]
        antigroup1 = comp.group_antiaff_host
        isin1 = antigroup1 in diff_host_dic
        if antigroup1 != None:
            if not isin1:
                diff_host_dic[antigroup1] = set()
        if (sum([x[1] // cpus_pervm for x in cpus]) >= vms):
            host_set = set()
            for _ in range(vms):
                found = False
                for j,(hname,total_cpus) in enumerate(cpus):
                    if (total_cpus >= cpus_pervm):
                        if isin1:
                            if hname in diff_host_dic[antigroup1]:
                                continue
                        cpus[j] = (hname,total_cpus - cpus_pervm)
                        al_id = sname + ':' + cname + ':' + hname
                        if al_id in al_pat:
                            al_pat[al_id] += cpus_pervm
                        else:
                            al_pat[al_id] = cpus_pervm
                        host_set.add(hname)
                        found = True
                        break
                if not found: return False
            if antigroup1 != None:
                diff_host_dic[antigroup1].update(host_set)
        else: return False
    return True

# mode 3 all components same host
def min_cost1(comps, cpus, gain, sname, service, diff_host_dic, al_pat, s_accepted):
    tot_cpus = sum([c[1]*c[2] for c in comps])
    if tot_cpus == 0: return True
    found = False
    for j,(hname,total_cpus) in enumerate(cpus):
        if (total_cpus >= tot_cpus):
            okhost=True
            #check if there is conflict
            for (cname, _vms, _cpus_pervm) in comps:
                comp = service.dic_com[cname]
                antigroup1 = comp.group_antiaff_host
                if antigroup1 != None:
                    if not (antigroup1 in diff_host_dic):
                        diff_host_dic[antigroup1] = set()
                    elif hname in diff_host_dic[antigroup1]:
                        okhost = False
                        break
            if not okhost:continue
            cpus[j] = (hname, total_cpus - tot_cpus)
            for (cname, vms, cps_pervm) in comps:
                al_id = sname + ':' + cname + ':' + hname
                if al_id in al_pat:
                    al_pat[al_id] += cps_pervm*vms
                else:
                    al_pat[al_id] = cps_pervm*vms
            found = True
            #update antiaffinity sets of components
            for (cname, _vms, _cpus_pervm) in comps:
                comp = service.dic_com[cname]
                antigroup1 = comp.group_antiaff_host
                if antigroup1 != None:
                    diff_host_dic[antigroup1].add(hname)
            break
    return found

def unshared_copy (inList):
    ''' ret: returns copies of list where all references are also copied
    '''
    if isinstance (inList, list):
        return list (map (unshared_copy, inList) )
    return inList
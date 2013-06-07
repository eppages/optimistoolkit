#!/usr/bin/python
import functions_compute as fc
import logging as log

def get_best_result(inp, mode_alloc, mode_feder, mode_cost, host_sort):
    """ Get the best result
        maxgain_cpu_found_alloc
        ([gain]*cp+1,[[inds]]*cp+1)
        inds:[ [  (service name,
                        ([ (vms, cpus_per_vm, net) ]*comp,
                               total_cpus, total_net, gain)
                  )
               ]*serv
             ]*total_cpus
        complexity: O(services*components^x*total_cpus*O(is_found_allocation) + total_cpus * O(min_cost_alloc3)->
                    s^2*c^(2x+2)*total_cpus*vms + s*c^(x+2)*total_cpus*vms
    """
    def get_maxdif(mat1, mat2):
        log.debug('Computing maximum difference of gain and cost for every cpu number')
        log.debug('Total cpus: %s', len(mat1)-1)
        max_val = 0
        g_max = 0
        c_min = 0
        alloc = []
        services = []
        for (gain,inds) in zip(mat1,mat2):
            #find minimum cost
            cost, al, srv = fc.min_cost_alloc(inp, inds, mode_alloc, host_sort)
            if log.getLogger().isEnabledFor(log.DEBUG):
                log.debug('Gain: %s - Cost: %s - Difference: %s', gain, cost, gain - cost)
            if (gain-cost > max_val):
                max_val = gain - cost
                g_max,c_min = gain,cost
                alloc = list(al.items())
                services = list(srv.items())
        return (max_val, g_max, c_min, alloc, services)
    #find maximum gain
    maxgain_cpu_found_alloc = fc.get_maxgain_cpu_found_alloc(inp, mode_alloc, mode_feder, mode_cost, host_sort)
    return get_maxdif(maxgain_cpu_found_alloc[0], maxgain_cpu_found_alloc[1])

def get_best_result_bound(inp, mode_federation, mode_cost):
    """ Get a bound for the best result
        maxgain_cpunet, mincost_cpunet:
        ([[gain]*(cp+1)]*(bn+1),?)
        complexity: (h+s*c^x)*totalcpus*totalband + totalcpus*totalband
    """
    maxgain_cpunet = fc.get_maxgain_cpu(inp, mode_federation, mode_cost)
    mincost_cpunet = fc.get_mincost_cpu(inp)
    
    def get_maxdif_2D(mat1,mat2):
        """ Get the maximum difference of all matrices' positions
            get the values of gain and cost as well
        """
        log.debug('Computing maximum difference of gain and cost for every cpu number and bandwidth value')
        log.debug('Total bandwidth: %s', len(mat1))
        max_val = 0
        g_max = 0
        c_min = 0
        for (l1,l2) in zip(mat1, mat2):
            log.debug('**Next Bandwidth Value**')
            log.debug('Total cpus for bandwidth value: %s', len(l1))
            for (g,c) in zip(l1,l2):
                if log.getLogger().isEnabledFor(log.DEBUG):
                    log.debug('Gain: %s - Cost: %s - Difference: %s', g, c, g - c)
                if (g-c > max_val):
                    max_val = g-c
                    g_max = g
                    c_min = c
    
        return (max_val, g_max, c_min)

    return get_maxdif_2D(maxgain_cpunet, mincost_cpunet)

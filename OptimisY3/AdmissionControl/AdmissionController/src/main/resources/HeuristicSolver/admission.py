#!/usr/bin/python
from __future__ import division
import logging as log
import functions_input as fi
import functions_results as fr
import mode as mode_
import os
import time
import argparse
import cProfile

def main():

    if args.verbosity: loglevel = log.DEBUG
    else: loglevel = log.INFO
    modeFederation = mode_.ModeFederation.CUSTOM #not allowed could be set with a new cmd option
    modeAllocation = mode_.ModeAllocation.MIXED
    #if args.costfed: modeCost = mode_.ModeCost.INCLUDE_FEDERATION_COST
    #else: modeCost = mode_.ModeCost.EXCLUDE_FEDERATION_COST
    modeCost = mode_.ModeCost.INCLUDE_FEDERATION_COST

    folder = args.inputfolder
    find_bound = args.boundfind
    find_solution = not args.suppress
    metric = args.metric
    if metric == "random": host_sort = mode_.ModeHostSort.RANDOM
    else: host_sort = mode_.ModeHostSort.COST
    
    log.basicConfig(filename=args.logfile, format='%(asctime)s-[%(filename)s-%(lineno)d]-%(levelname)s:%(message)s', level=loglevel)
    #----
    log.info('New execution of admission control')
    log.info('Directory: %s', os.getcwd())
    in_folder = folder
    #----
    inp = fi.readinput(in_folder)

    try:
        t1 = time.clock()
        if find_solution: r1 = fr.get_best_result(inp, modeAllocation, modeFederation, modeCost, host_sort)
        t2 = time.clock()
        if find_solution: 
            print ('best solution found:', r1[0:3])
            log.info('best solution found:%s', r1[0:3])
        if find_solution: print (round(t2-t1, 4) )
        t3 = time.clock()
        if find_bound: r2 = fr.get_best_result_bound(inp, modeFederation, modeCost)
        t4 = time.clock()
        
        if find_bound: 
            print ('best solution bound:', r2)
            print (round(t4-t3, 4) )
        if find_solution: 
            print ('allocation pattern:', r1[3])
            print ('services acceptance rate, availability:', r1[4])
        if (args.outputfile!=None):
            f = open(args.outputfile, 'w')
            if find_solution: 
                print_output(f, inp, r1, modeCost)
            if find_bound: 
                print_output(f,r2)
            f.close()
    except mode_.MyError as e:
        print ('Invalid value for mode: ', e.value)
    
def print_output(f, inp, r1, cost_mode):
    qt = "\""
    seq = qt+"**"+qt
    f.write(seq + "," + qt + "Admission Control Result" + qt + "," + seq); f.write('\n')
    f.write('\n')
    for (sname, details) in r1[4]:
        f.write(qt + sname + qt + "," + str(details[1])); f.write('\n')
    f.write('\n')
    
    f.write(seq + "," + qt + "how many vms of service component i \
are allocated for service a in host j" + qt + "," + seq); f.write('\n')
    f.write('\n')
    for (name, cpus) in r1[3]:
        (sname, cname, hname)=name.split(':')
        vcpus = inp.dic_ser[sname].dic_com[cname].virtual_cpus
        f.write(qt + hname + qt + "," + qt + cname + qt + "," + qt + sname + qt + ","
                + str(cpus/vcpus)); f.write('\n')
    f.write('\n')

    f.write(seq + "," + qt + "number of accepted elastic vms for each service" + qt + "," + seq); f.write('\n')
    f.write('\n')
    for (sname, details) in r1[4]:
        s = inp.dic_ser[sname]
        d = {}
        for (cname, vms, _cpus_per_vm) in details[3]:
            if cname in d: d[cname] +=vms
            else: d[cname] = vms
        total_elastic = sum([max(0,vms - s.dic_com[cname].basic_vms) for (cname, vms) in d.items()])
        f.write(qt + sname + qt + "," + str(total_elastic)); f.write('\n')
    f.write('\n')

    f.write(seq + "," + qt + "number of accepted elastic vms per service component" + qt + "," + seq); f.write('\n')
    f.write('\n')
    for (sname, details) in r1[4]:
        s = inp.dic_ser[sname]
        d = {}
        for (cname, vms, _cpus_per_vm) in details[3]:
            if cname in d: d[cname] +=vms
            else: d[cname] = vms
        for (cname, vms) in d.items():
            elastic = max(0, vms - s.dic_com[cname].basic_vms)
            f.write(qt + cname + qt + "," + qt + sname + qt + "," + str(elastic)); f.write('\n')
    f.write('\n')
    
    f.write(seq + "," + qt + "number of vms for federation" + qt + "," + seq); f.write('\n')
    f.write('\n')
    for (sname, details) in r1[4]:
        s = inp.dic_ser[sname]
        d = {}
        for (cname, vms, _cpus_per_vm) in details[3]:
            if cname in d: d[cname] +=vms
            else: d[cname] = vms
        for (cname, vms) in d.items():
            federated = max(0,int(s.dic_com[cname].get_min_elastic()) + s.dic_com[cname].basic_vms - vms)
            f.write(qt + cname + qt + "," + qt + sname + qt + "," + str(federated)); f.write('\n')
    f.write('\n')
    
    f.write(seq + "," + qt + "Trust for new service" + qt + "," + seq); f.write('\n')
    f.write('\n')
    for (sname, details) in r1[4]:
        s = inp.dic_ser[sname]
        f.write(qt + sname + qt + "," + str(s.get_trust(details[0], inp.trust_weight))); f.write('\n')
    f.write('\n')
    
    f.write(seq + "," + qt + "Probability the service will fail" + qt + "," + seq); f.write('\n')
    f.write('\n')
    for (sname, details) in r1[4]:
        f.write(qt + sname + qt + "," + str(1.0-details[0])); f.write('\n')
    f.write('\n')
    
    f.write(seq + "," + qt + "Eco for new service" + qt + "," + seq); f.write('\n')
    f.write('\n')
    for (sname, details) in r1[4]:
        s = inp.dic_ser[sname]
        f.write(qt + sname + qt + "," + str(s.get_eco(details[0], inp.eco_weight))); f.write('\n')
    f.write('\n')
    
    f.write(seq + "," + qt + "cost of hosting the service" + qt + "," + seq); f.write('\n')
    f.write('\n')
    for (sname, details) in r1[4]:
        s = inp.dic_ser[sname]
        cost = details[2] - s.get_gain(details[0], details[1], 0, cost_mode,
                                       inp.cost_weight, inp.eco_weight,
                                       inp.risk_weight, inp.trust_weight)
        f.write(qt + sname + qt + "," + str(cost) ); f.write('\n')
    f.write('\n')

def print_output2(f,r2):
    f.write(str( ('best solution bound:', r2) ) ); f.write('\n')

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Admission control utility program")
    parser.add_argument("-b", "--boundfind", help="find a bound of best solution", action="store_true")
    parser.add_argument("-c", "--costfed", help="includes federation cost", action="store_true")
    parser.add_argument("-p", "--profile", help="print profiling information",
                        action="store_true")
    parser.add_argument("-s", "--suppress", help="suppress solution, useful when someone want to\
                        find only the bound or just generate inputs", action="store_true")
    parser.add_argument("-v", "--verbosity", help="increase output verbosity,\
                        output in admission.log file", action="store_true")
    parser.add_argument("-i", "--inputfolder", help="input folder which is a folder in parent\
                        directory (default gams)", default="gams")
    parser.add_argument("-l", "--logfile", help="log file\
                        directory (default admission.log)", default="admission.log")
    parser.add_argument("-o", "--outputfile", help="write result in specified file")
    parser.add_argument("-m", "--metric", help="metric for host sorting.\
                        Options: costturnon, costfull, average\
                        Default: average")
    args = parser.parse_args()
    if args.profile: cProfile.run('main()')
    else: main()

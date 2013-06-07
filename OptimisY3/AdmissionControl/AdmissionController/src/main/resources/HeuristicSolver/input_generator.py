#!/usr/bin/python
import os
def generate_input(in_folder):
    def check(bn, per):
        #bn = bin(n)[2:]
        st = '1'*per
        if bn.count('1') == per:
            ind = bn.find(st)
            if (ind >= 0) and (ind%per) == 0: return True
            else: return False
        else: return False
        
    comps = 4
    hosts = 5000
    nets = 2
    servs = 20
    
    availability_penalty = 100
    availability = 0.8
    basic_cost = 1000
    basic_net = 0
    basic_vms = 1
    cost_host_percpu = 5
    cost_host_turnon = 10
    elastic_net = 2
    elastic_vms = 2
    max_cpus = 16
    network_capacity = 1000
    res_cpus = 0
    virtual_cpus = 1
    
    same_net_per = 4
    same_host_per = 2
    
    if not os.path.exists(in_folder):
        os.makedirs(in_folder)
    
    f = open("%scomponents.csv" % in_folder, 'w')
    f.write("c1*c%s" % str(comps))
    f = open("%shosts.csv" % in_folder, 'w')
    f.write("h1*h%s" % str(hosts))
    f = open("%snetworks.csv" % in_folder, 'w')
    f.write("n1*n%s" % str(nets))
    f = open("%sservices.csv" % in_folder, 'w')
    f.write("s1*s%s" % str(servs))
    f = open("%scombinations.csv" % in_folder, 'w')
    f.write("t0*t%s" % str(2**comps-1))
    
    #
    f = open("%savailability_penalty.csv" % in_folder, 'w')
    for s in range(servs): f.write("s%s,%s\n" % (str(s+1), availability_penalty) )

    #
    f = open("%savailability.csv" % in_folder, 'w')
    for s in range(servs): f.write("s%s,%s\n" % (str(s+1), availability))
        
    #
    f = open("%sbasic_cost.csv" % in_folder, 'w')
    for s in range(servs): f.write("s%s,%s\n" % (str(s+1), basic_cost) )

    #
    f = open("%sbasic_net.csv" % in_folder, 'w')
    f.write("dummy")
    for s in range(servs): f.write(", s%s" % str(s+1) )
    f.write("\n")
    for c in range(comps):
        f.write("c%s" % str(c+1) )
        for _ in range(servs): f.write(", %s" % basic_net )
        f.write("\n")

    #
    f = open("%sbasic_vms.csv" % in_folder, 'w')
    f.write("dummy")
    for s in range(servs): f.write(", s%s" % str(s+1) )
    f.write("\n")
    for c in range(comps):
        f.write("c%s" % str(c+1) )
        for _ in range(servs): f.write(", %s" % basic_vms )
        f.write("\n")

    #
    f = open("%scombs.csv" % in_folder, 'w')
    f.write("dummy")
    for c in range(comps): f.write(", c%s" % str(c+1) )
    f.write("\n")
    #def write_comb(f, d, l, count):
    #    if d==0:
    #        f.write("t%s" % str(count) )
    #        for i in range(comps): f.write(", %s" % l[i] )
    #        f.write("\n")
    #    else:
    #        write_comb(f, d-1, l+[0], count)
    #        write_comb(f, d-1, l+[1], count+2**(d-1))
    #write_comb(f, comps, [], 0)
    for i in range(2**comps):
        f.write("t%s" % str(i) )
        for b in bin(i)[2:].rjust(comps,'0'): f.write(", %s" % b )
        f.write("\n")
        
    #   
    f = open("%scombs_rules.csv" % in_folder, 'w')
    f.write("dummy")
    for s in range(servs): f.write(", s%s" % str(s+1) )
    f.write("\n")
    for i in range(2**comps):
        f.write("t%s" % str(i) )
        sti = bin(i)[2:].rjust(comps,'0')
        if check(sti, same_host_per):
            for s in range(servs): f.write(", 2")
        elif check(sti, same_net_per):
            for s in range(servs): f.write(", 1")
        else:
            for s in range(servs): f.write(", 0")
        f.write("\n")
    
    #    
    f = open("%scost_host_percpu.csv" % in_folder, 'w')
    for h in range(hosts): f.write("h%s,%s\n" % (str(h+1), cost_host_percpu) )

    #
    f = open("%scost_host_turnon.csv" % in_folder, 'w')
    for h in range(hosts): f.write("h%s,%s\n" % (str(h+1), cost_host_turnon) )

    #
    f = open("%scost_weight.csv" % in_folder, 'w')
    f.write("1")
    
    #
    f = open("%seco_weight.csv" % in_folder, 'w')
    f.write("1")

    #
    f = open("%selastic_net.csv" % in_folder, 'w')
    f.write("dummy")
    for s in range(servs): f.write(", s%s" % str(s+1) )
    f.write("\n")
    for c in range(comps):
        f.write("c%s" % str(c+1) )
        for _ in range(servs): f.write(", %s" % elastic_net)
        f.write("\n")

    #
    f = open("%selastic_vms.csv" % in_folder, 'w')
    f.write("dummy")
    for s in range(servs): f.write(", s%s" % str(s+1) )
    f.write("\n")
    for c in range(comps):
        f.write("c%s" % str(c+1) )
        for _ in range(servs): f.write(", %s" % elastic_vms)
        f.write("\n")

    #
    f = open("%smax_cpus.csv" % in_folder, 'w')
    for h in range(hosts): f.write("h%s,%s\n" % (str(h+1), max_cpus) )

    #
    f = open("%snetwork_capacity.csv" % in_folder, 'w')
    for n in range(nets): f.write("n%s,%s\n" % (str(n+1), network_capacity) )

    #
    f = open("%snetwork_topology.csv" % in_folder, 'w')
    f.write("dummy")
    for h in range(hosts): f.write(", h%s" % str(h+1) )
    f.write("\n")
    for n in range(nets):
        f.write("n%s" % str(n+1) )
        for top in range(hosts):
            if (top+1)%nets == n: f.write(", yes")
            else: f.write(", no")
        f.write("\n")

    #
    f = open("%sres_cpus.csv" % in_folder, 'w')
    for h in range(hosts): f.write("h%s,%s\n" % (str(h+1), res_cpus) )

    #
    f = open("%strust.csv" % in_folder, 'w')
    for s in range(servs):
        f.write("s%s,1\n" % str(s+1) )

    #
    f = open("%strust_weight.csv" % in_folder, 'w')
    f.write("1")

    #
    f = open("%svirtual_cpus.csv" % in_folder, 'w')
    f.write("dummy")
    for s in range(servs): f.write(", s%s" % str(s+1) )
    f.write("\n")
    for c in range(comps):
        f.write("c%s" % str(c+1) )
        for _ in range(servs): f.write(", %s" % virtual_cpus)
        f.write("\n")

    #
    f = open("%svms.csv" % in_folder, 'w')
    f.write("dummy")
    for s in range(servs): f.write(", s%s" % str(s+1) )
    f.write("\n")
    for c in range(comps):
        f.write("c%s" % str(c+1) )
        for _ in range(servs): f.write(", yes")
        f.write("\n")


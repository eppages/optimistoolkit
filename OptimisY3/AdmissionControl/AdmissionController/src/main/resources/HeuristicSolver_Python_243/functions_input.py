#!/usr/bin/python
import mode as mode_
import objects as ob

def readinput(in_folder):
    inp = ob.Input()
    dic_hos = inp.dic_hos
    dic_ser = inp.dic_ser
    dic_comb = inp.dic_comb

    #
    f = open(in_folder + "combs.csv", 'r')
    line = f.readline()
    com = []
    for c in line.split(',')[1:]:
        c = c.strip()
        com.append(c)
    for line in f:
        lis = line.split(',')
        t = lis[0]
        comb = []
        for (inset,c) in zip(lis[1:],com):
            if int(inset): comb.append(c)
        if t not in dic_comb: dic_comb[t] = list(comb)
    f.close()

    # ~combs_rules
    f = open(in_folder + "affinity_rules.csv", 'r')
    line = f.readline()
    combs = []
    for comb in line.split(',')[1:]:
        comb = comb.strip()
        combs.append(comb)
    for line in f:
        lis = line.split(',')
        s = lis[0]
        if s not in dic_comb: dic_ser[s] = ob.Service()
        for (t,aff) in zip(combs,lis[1:]):
            #TODO handle the case of a component being in 2 different groups
            #merge groups (now the first group is assigned)
            if aff.strip() == '1':
                for c in dic_comb[t]:
                    if c not in dic_ser[s].dic_com:
                        dic_ser[s].dic_com[c] = ob.Component()
                    if dic_ser[s].dic_com[c].group_aff_cloud == None:
                        dic_ser[s].dic_com[c].group_aff_cloud = t
            elif aff.strip() == '2':
                for c in dic_comb[t]:
                    if c not in dic_ser[s].dic_com:
                        dic_ser[s].dic_com[c] = ob.Component()
                    if dic_ser[s].dic_com[c].group_aff_host == None:
                        dic_ser[s].dic_com[c].group_aff_host = t
    f.close()

    # 
    f = open(in_folder + "affinity_sc.csv", 'r')
    line = f.readline()
    ser = []
    for s in line.split(',')[1:]:
        s = s.strip()
        if s not in dic_ser: dic_ser[s] = ob.Service()
        ser.append(s)
    for line in f:
        lis = line.split(',')
        c = lis[0]
        for (aff,s) in zip(lis[1:],ser):
            #TODO handle the case of a component being in 2 different groups
            #merge groups (now the first group is assigned)
            if aff.strip() == '1':
                if c not in dic_ser[s].dic_com:
                    dic_ser[s].dic_com[c] = ob.Component()
                if dic_ser[s].dic_com[c].group_aff_cloud == None:
                    dic_ser[s].dic_com[c].group_aff_cloud = c
            elif aff.strip() == '2':
                if c not in dic_ser[s].dic_com:
                    dic_ser[s].dic_com[c] = ob.Component()
                if dic_ser[s].dic_com[c].group_aff_host == None:
                    dic_ser[s].dic_com[c].group_aff_host = c
    f.close()
    
    # combs_selfantirules
    f = open(in_folder + "anti_affinity_sc.csv", 'r')
    line = f.readline()
    ser = []
    for s in line.split(',')[1:]:
        s = s.strip()
        if s not in dic_ser: dic_ser[s] = ob.Service()
        ser.append(s)
    for line in f:
        lis = line.split(',')
        c = lis[0]
        for (aff,s) in zip(lis[1:],ser):
            if aff.strip() == '1':
                if c not in dic_ser[s].dic_com:
                    dic_ser[s].dic_com[c] = ob.Component()
                if dic_ser[s].dic_com[c].self_antiaff == None:
                    dic_ser[s].dic_com[c].self_antiaff = mode_.ObjectType.HOST
            elif aff.strip() == '2':
                if c not in dic_ser[s].dic_com:
                    dic_ser[s].dic_com[c] = ob.Component()
                if dic_ser[s].dic_com[c].self_antiaff == None:
                    dic_ser[s].dic_com[c].self_antiaff = mode_.ObjectType.CLOUD
            else:
                if c not in dic_ser[s].dic_com:
                    dic_ser[s].dic_com[c] = ob.Component()
                if dic_ser[s].dic_com[c].self_antiaff == None:
                    dic_ser[s].dic_com[c].self_antiaff = mode_.ObjectType.NULL
    f.close()

    # ~combs_antirules
    f = open(in_folder + "anti_affinity_rules.csv", 'r')
    line = f.readline()
    combs = []
    for comb in line.split(',')[1:]:
        comb = comb.strip()
        combs.append(comb)
    for line in f:
        lis = line.split(',')
        s = lis[0]
        if s not in dic_ser: dic_ser[s] = ob.Service()
        for (t,aff) in zip(combs,lis[1:]):
            #TODO handle the case of a component being in 2 different groups
            #merge groups (now the first group is assigned)
            if aff.strip() == '1':
                for c in dic_comb[t]:
                    if c not in dic_ser[s].dic_com:
                        dic_ser[s].dic_com[c] = ob.Component()
                    if dic_ser[s].dic_com[c].group_antiaff_host == None:
                        dic_ser[s].dic_com[c].group_antiaff_host = t
            elif aff.strip() == '2':
                for c in dic_comb[t]:
                    if c not in dic_ser[s].dic_com:
                        dic_ser[s].dic_com[c] = ob.Component()
                    if dic_ser[s].dic_com[c].group_antiaff_cloud == None:
                        dic_ser[s].dic_com[c].group_antiaff_cloud = t
    f.close()

    #
    f = open(in_folder + "availability_sc.csv", 'r')
    line = f.readline()
    ser = []
    for s in line.split(',')[1:]:
        s = s.strip()
        if s not in dic_ser: dic_ser[s] = ob.Service()
        ser.append(s)
    for line in f:
        lis = line.split(',')
        c = lis[0]
        for (av,s) in zip(lis[1:],ser):
            if c not in dic_ser[s].dic_com: dic_ser[s].dic_com[c] = ob.Component()
            dic_ser[s].dic_com[c].availability = float(av)
    f.close()

    # basic_vms
    f = open(in_folder + "basic.csv", 'r')
    line = f.readline()
    ser = []
    for s in line.split(',')[1:]:
        s2 = s.strip()
        if s2 not in dic_ser: dic_ser[s2] = ob.Service()
        ser.append(s2)
    for line in f:
        lis = line.split(',')
        c = lis[0]
        for (bv,s) in zip(lis[1:],ser):
            if c not in dic_ser[s].dic_com: dic_ser[s].dic_com[c] = ob.Component()
            dic_ser[s].dic_com[c].basic_vms = int(bv)
    f.close()

    # basic_cost
    f = open(in_folder + "basicCost.csv", 'r')
    for [s,c] in [line.split(',') for line in f]:
        if s not in dic_ser: dic_ser[s] = ob.Service()
        #dic_ser[s].cost = int(c)
        dic_ser[s].set_cost_penalty(int(c))
    f.close()

    #
    f = open(in_folder + "cost_weight.csv", 'r')
    inp.cost_weight = float(f.readline()) 
    f.close()

    #~federate
    f = open(in_folder + "doNotFederate.csv", 'r')
    for [s,fed] in [line.split(',') for line in f]:
        if (fed.strip() == "1") or (fed.strip() == "yes"):
            if s not in dic_ser: dic_ser[s] = ob.Service()
            dic_ser[s].federation = False
        else:
            if s not in dic_ser: dic_ser[s] = ob.Service()
            dic_ser[s].federation = True
            
    f.close()
    
    #
    f = open(in_folder + "eco_weight.csv", 'r')
    inp.eco_weight = float(f.readline())
    f.close()

    #
    f = open(in_folder + "eco.csv", 'r')
    for [s,eco] in [line.split(',') for line in f]:
        if s not in dic_ser: dic_ser[s] = ob.Service()
        dic_ser[s].eco = int(float(eco))
    f.close()

    #
    f = open(in_folder + "ecoHost.csv", 'r')
    for [h,eco] in [line.split(',') for line in f]:
        if h not in dic_hos: dic_hos[h] = ob.Host()
        dic_hos[h].eco = float(eco)
    f.close()
    
    # elastic_vms
    f = open(in_folder + "elastic.csv", 'r')
    line = f.readline()
    ser = []
    for s in line.split(',')[1:]:
        s = s.strip()
        if s not in dic_ser: dic_ser[s] = ob.Service()
        ser.append(s)
    for line in f:
        lis = line.split(',')
        c = lis[0]
        for (ev,s) in zip(lis[1:],ser):
            if c not in dic_ser[s].dic_com: dic_ser[s].dic_com[c] = ob.Component()
            dic_ser[s].dic_com[c].elastic_vms = int(ev)
            dic_ser[s].dic_com[c].elastic_vms -= dic_ser[s].dic_com[c].basic_vms
    f.close()

    # max_cpus
    f = open(in_folder + "maxCpus.csv", 'r')
    for [h,mc] in [line.split(',') for line in f]:
        if h not in dic_hos: dic_hos[h] = ob.Host()
        dic_hos[h].maxcpus = int(mc)
    f.close()
    
    # res_cpus
    f = open(in_folder + "resCpus.csv", 'r')
    for [h,rc] in [line.split(',') for line in f]:
        if h not in dic_hos: dic_hos[h] = ob.Host()
        dic_hos[h].rescpus = int(rc)
    f.close()
    
    #
    f = open(in_folder + "risk_weight.csv", 'r')
    inp.risk_weight = float(f.readline())
    f.close()

    #
    f = open(in_folder + "risk.csv", 'r')
    for [s,t] in [line.split(',') for line in f]:
        if s not in dic_ser: dic_ser[s] = ob.Service()
        dic_ser[s].risk = float(t)

    # virtual_cpus
    f = open(in_folder + "table.csv", 'r')
    line = f.readline()
    ser = []
    for s in line.split(',')[1:]:
        s2 = s.strip()
        if s2 not in dic_ser: dic_ser[s2] = ob.Service()
        ser.append(s2)
    for line in f:
        lis = line.split(',')
        c = lis[0]
        for (vc,s) in zip(lis[1:],ser):
            if c not in dic_ser[s].dic_com: dic_ser[s].dic_com[c] = ob.Component()
            dic_ser[s].dic_com[c].virtual_cpus = int(vc)
    f.close()
    #
    f = open(in_folder + "trust_weight.csv", 'r')
    inp.trust_weight = float(f.readline())
    f.close()

    #
    f = open(in_folder + "trust.csv", 'r')
    for [s,t] in [line.split(',') for line in f]:
        if s not in dic_ser: dic_ser[s] = ob.Service()
        dic_ser[s].trust = int(float(t))
    
    #
    f = open(in_folder + "vms.csv", 'r')
    line = f.readline()
    ser = []
    for s in line.split(',')[1:]:
        s2 = s.strip()
        if s2 not in dic_ser: dic_ser[s2] = ob.Service()
        ser.append(s2)
    for line in f:
        lis = line.split(',')
        c = lis[0]
        for (v,s) in zip(lis[1:],ser):
            assert (v.strip() == "no" or v.strip() == "yes")
            if c not in dic_ser[s].dic_com: dic_ser[s].dic_com[c] = ob.Component()
            if v.strip() == "no": del dic_ser[s].dic_com[c]
    f.close()
    
    return inp

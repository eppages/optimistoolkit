* Changes - new features
* 1. added anti-affinity rules
* 2. availability is handled on component level
* 3. instances of the same VM can be federated
* 4. cost of federating VMs is considered
* 5. added constraints for TREC factors
* 6  changed the eco-efficiency of the hosts
* 7. penalty is now a percentage of the revenue (gain)
* 8. fixed "division by zero" error for federation cost
* 9. removed non-existing components from results
* 10. fixed bug when sum of elastic VMs is zero
* 11. revisited affinity rules medium and high
* 12. fixed infeasibility when federation is not allowed

$offdigit

Set a 'services'  /
$include services.csv
/;

Set j 'hosts'  /
$include hosts.csv
/;

Parameter  maxCpus(j) 'Maximum number of cpus per ph'  /
$ondelim
$include maxCpus.csv
$offdelim
/;

display maxCpus;

Parameter  resCpus(j) 'Reserved number of cpus per ph' /
$ondelim
$include resCpus.csv
$offdelim
/;
display resCpus;

Set  joff(j) unoccupied hosts;
     joff(j)= yes$(resCpus(j)=0);
display joff;

Parameter  z(j)  cost of unoccupied hosts;
*     z("ph1")= 10;
*     z("ph2")= 10;
*     z("ph3")= 5;
*     z("ph4")= 10;
      z(j)=0;
display z;

*-----------------------------------------------------
* TREC Constraints - start
*-----------------------------------------------------

Parameter  eco_constraint(a)   'Constraint on eco'   /
$ondelim
$include eco_constraint.csv
$offdelim
/;

display eco_constraint;

Parameter  trust_constraint(a)   'Constraint on trust'   /
$ondelim
$include trust_constraint.csv
$offdelim
/;

display trust_constraint;

Parameter  risk_constraint(a)   'Constraint on risk'   /
$ondelim
$include risk_constraint.csv
$offdelim
/;

display risk_constraint;

Parameter  cost_constraint(a)   'Constraint on cost'   /
$ondelim
$include cost_constraint.csv
$offdelim
/;

display cost_constraint;

*-----------------------------------------------------
* TREC Constraints - end
*-----------------------------------------------------

Parameter  Gain(a)   'Gain for new service (cost)'   /
$ondelim
$include basicCost.csv
$offdelim
/;

display Gain;

Parameter  Cost_plus(a) 'Extra cost for each elastic vm'  /
$ondelim
$include extraCost.csv
$offdelim
/ ;

display Cost_plus;

Parameter  Trust(a)  'Trust for new service'  /
$ondelim
$include trust.csv
$offdelim
/ ;

display Trust;

Parameter  Risk(a)   'Probability the service will fail' /
$ondelim
$include risk.csv
$offdelim
/ ;

display Risk;

Parameter  Eco_s(a)    'Eco for new service'  /
$ondelim
$include eco.csv
$offdelim
/ ;

display Eco_s;

Parameter  Eco(j)   'Eco-efficiency of host'  /
$ondelim
$include ecoHost.csv
$offdelim
/ ;

display Eco;

Parameter eco_max;
eco_max=smax(j, Eco(j));

Eco(j)= Eco(j)/eco_max;

display Eco;


Parameter  Pav(a)    Penalty for availability;
Pav(a)=Gain(a)*0.5;

display Pav;

Parameter  fi(a)     'min probability app a is available'  /
$ondelim
$include availability.csv
$offdelim
/;

display fi;

Parameter  K         Big constant                                          /10/;
Parameter  e         Small constant                                     /0.001/;

Parameter  doNotFederate(a) 'Federation flag' /
$ondelim
$include doNotFederate.csv
$offdelim
/ ;

display doNotFederate;

Set  i     'service components i.e. vms'  /
$include components.csv
/;

display i;

Table vm(i,a)
$ondelim
$include vms.csv
$offdelim;

display vm;

Table  av(i,a)
$ondelim
$include availability_sc.csv
$offdelim;

display av;

*Affinity on service and service component level
* 0 = low
* 1 = medium
* 2 = high

Set combs_ 'possible combinations of scs' /
$include combs_.csv
/;

display combs_;

Table affinity_rules(a, combs_) 'affinity rules on service level'
$ondelim
$include affinity_rules.csv
$offdelim;

display affinity_rules;

Table anti_affinity_rules(a, combs_) 'anti-affinity rules on service level'
$ondelim
$include anti_affinity_rules.csv
$offdelim;

display anti_affinity_rules;

Table  combs(combs_, i) 'combinations of service components'
$ondelim
$include combs.csv
$offdelim;

display combs;

Table aff_sc(i,a)   'Affinity constraint on service component level'
$ondelim
$include affinity_sc.csv
$offdelim;

display aff_sc;

Table anti_aff_sc(i,a)   'Affinity constraint on service component level'
$ondelim
$include anti_affinity_sc.csv
$offdelim;

display anti_aff_sc;

Table elastic_vms_(i,a) 'Maximum number of elastic vms'
$ondelim
$include elastic.csv
$offdelim;

display elastic_vms_;

Table basic_vms(i,a) 'Minimum number of basic vms'
$ondelim
$include basic.csv
$offdelim;

display basic_vms;

Parameter  elastic_vms(i,a) Maximum elastic VMs per service;
           elastic_vms(i,a)= elastic_vms_(i,a)-basic_vms(i,a);

display elastic_vms;

Parameter  max_elastic_vms(a) Maximum elastic VMs per service;
           max_elastic_vms(a)= sum(i, elastic_vms(i,a));

Display max_elastic_vms;

Parameter  max_basic_vms(a) Maximum basic VMs per service;
           max_basic_vms(a)= sum(i, basic_vms(i,a));

Display max_basic_vms;

Parameter  max_vms(a) Maximum VMs per service;
           max_vms(a)= max_elastic_vms(a) + max_basic_vms(a);

Display max_vms;

Table cpu_num(i,a) 'number of basic virtual cpus needed per new VM'
$ondelim
$include table.csv
$offdelim;

display cpu_num;

Parameter  num_comps(a);
num_comps(a)= sum(i$vm(i,a), 1);

display num_comps;

Parameter  fed_cost_per_vm(i,a)  Cost for federating a vm;

fed_cost_per_vm(i,a)$vm(i,a)=0.8*Gain(a)/(num_comps(a)*(elastic_vms(i,a) + basic_vms(i,a)));

display fed_cost_per_vm;

Parameter we 'Eco-efficiency weight' /
$ondelim
$include eco_weight.csv
$offdelim
/;

display we;

Parameter wc 'Cost weight' /
$ondelim
$include cost_weight.csv
$offdelim
/;

display wc;

Parameter wt 'Trust weight' /
$ondelim
$include trust_weight.csv
$offdelim
/;

display wt;

Parameter wr 'Risk weight' /
$ondelim
$include risk_weight.csv
$offdelim
/;

display wr;

Binary variables         xboolean(i,a) has service component i of service a used all basic vms,
                         xfactor(a) Admission Control Result,
                         bool(j) is physical host j involved in the allocation,
                         involved_ph(j,i,a) is physical host j involved in the allocation of service component i of service a,
                         involved_ph_(j,a) is physical host j involved in the allocation of service a,
                         ph_bool(combs_,j,a) is physical host j involved in the allocation of the service components that belong to combination combs_,
                         joint_cdf_bool(a) is availability constraint for service satisfied or not,
                         cdf_sc_bool(i,a) is availability constaint for component satisfied or not,
                         fed_vms_bool(i,a) ;

Variables                objective, xfactor_(a), accepted_vms_percent(a), average_cdf(a), cost(a) cost of hosting the service;

                         objective.lo=0;

                         xfactor_.lo(a)=0;
                         xfactor_.up(a)=1;

                         accepted_vms_percent.lo(a)=0;
                         accepted_vms_percent.up(a)=1;

                         average_cdf.lo(a)=0;
                         average_cdf.up(a)=1;

Integer Variables        xx(j,i,a) how many vms of service component i are allocated for service a in host j,
                         cpu_res(j) residual number of virtual cpus per physical host,
                         accepted_elastic_vms(a) number of accepted elastic vms for each service,
                         accepted_elastic_vms_per_comp(i,a) number of accepted elastic vms per service component,
                         num_phs(i,a) number of physical hosts involved in the allocation of a service component,
                         num_vms(i,j,a) number of vms of component i of service a allocated on host j,
                         accepted_basic_vms(a) number of accepted basic vms for service a,
                         fed_vms(i,a) number of vms for federation,
                         fed_comps(a) number of federated components of a service;

                         num_phs.lo(i,a)=0;
                         num_phs.up(i,a)=sum(j,1);

                         xx.lo(j,i,a)=0;
                         xx.up(j,i,a)=K*max_vms(a);

                         cpu_res.lo(j)=0;
                         cpu_res.up(j)= maxCpus(j);

                         accepted_elastic_vms_per_comp.lo(i,a)=0;
                         accepted_elastic_vms_per_comp.up(i,a)=elastic_vms(i,a);

                         accepted_elastic_vms.lo(a)=0;
                         accepted_elastic_vms.up(a)=max_elastic_vms(a);

                         num_vms.lo(i,j,a)=0;
                         num_vms.up(i,j,a)=elastic_vms(i,a) + basic_vms(i,a);

                         accepted_basic_vms.lo(a)=0;
                         accepted_basic_vms.up(a)=sum(i, basic_vms(i,a));

                         fed_vms.lo(i,a)=0;
                         fed_vms.up(i,a)=elastic_vms(i,a) + basic_vms(i,a);

                         fed_comps.lo(a)=0;
                         fed_comps.up(a)=sum(i$vm(i,a), 1);

Nonnegative variables    joint_cdf(a) joint percent of cdfs if service is admitted,
                         cdf_sc(i,a)  percent of maximum possible elastic vms that were allocated;

                         cdf_sc.lo(i,a)=0;
                         cdf_sc.up(i,a)=1;

                         joint_cdf.lo(a)=0;
                         joint_cdf.up(a)=1;

Equations                obj, xbool_low(i,a), xbool_up(i,a), elastic_vms_eq(a), elastic_vms_per_comp_eq(i,a),

                         prob_availability_Eq, admittance(a),cpu_res_Eq(j), bool_low(j), bool_up(j), admittance_(a),

                         allocation(i,a), allocation_(i,a), cpu_res_threshold(j), cdf_sc_eq(i,a), joint_cdf_eq(a),

                         affinity_sc(i,a), affinity_sc_(i,a), involved_ph_low(j,i,a), involved_ph_up(j,i,a),

                         xbool_low_(i,a), xbool_up_(i,a), mediator(i,a), involved_ph_low_(j,a),involved_ph_up_(j,a),

                         low(combs_,j,a), up(combs_,j,a), affinity_rule(a, combs_), affinity_rule_(a, combs_),

                         anti_affinity_sc(i,j,a), anti_affinity_rule_(j, a, combs_), anti_affinity_rule(a, combs_),

                         anti_affinity_sc_(i,a), basic_vms_eq(a), accepted_vms_percent_eq(a), joint_cdf_low(a),

                         joint_cdf_up(a), cdf_sc_constraint(i,a), cdf_sc_low(i,a), cdf_sc_up(i,a), fed_vms_eq(a),

                         average_cdf_eq(a), average_cdf_fed_eq(a), eco_cons(a), trust_cons(a), risk_cons(a),

                         cost_cons(a), cost_eq(a), fed_vm_eq_plus(i,a), affinity_rule_plus(a, combs_), new(i,a),

                         fed_vms_low(i,a), fed_vms_up(i,a), cdf_sc_eq_plus(i,a);


* residual number of virtual cpus per physical host
cpu_res_Eq(j)..        cpu_res(j)=e=maxCpus(j) - resCpus(j) - sum((i,a)$vm(i,a),cpu_num(i,a)*xx(j,i,a));

* no overbooking is allowed
cpu_res_threshold(j).. cpu_res(j)=g=0;

* the sc's computing capacities are considered to be uniformally distributed in the interval [èi, èi + Îi]
*cdf_sc_eq(i,a)$vm(i,a)..   cdf_sc(i,a)*(elastic_vms(i,a)+1)=e=xboolean(i,a)*(sum(j, xx(j,i,a)) - basic_vms(i,a)+1);
*cdf_sc_eq(i,a)$vm(i,a)..   cdf_sc(i,a)*(elastic_vms(i,a)+1)=e=xboolean(i,a)*(sum(j, xx(j,i,a)) + fed_vms(i,a) - basic_vms(i,a)+1);
cdf_sc_eq(i,a)$(vm(i,a) and (1-doNotFederate(a)))..    cdf_sc(i,a)*(elastic_vms(i,a)+1)=e=sum(j, xx(j,i,a)) + fed_vms(i,a) - basic_vms(i,a)+1;
cdf_sc_eq_plus(i,a)$(vm(i,a) and doNotFederate(a))..   cdf_sc(i,a)*(elastic_vms(i,a)+1)=e=xboolean(i,a)*(sum(j, xx(j,i,a)) + fed_vms(i,a) - basic_vms(i,a)+1);

cdf_sc_constraint(i,a)$(vm(i,a) and ((anti_aff_sc(i,a) ne 2)))..  cdf_sc(i,a)=g=av(i,a)*xboolean(i,a);

* Boolean variables that signal if required availability is satisfied or not
cdf_sc_low(i,a)$vm(i,a)..   cdf_sc(i,a)-av(i,a)=g=K*(cdf_sc_bool(i,a)-1);
cdf_sc_up(i,a)$vm(i,a)..    cdf_sc(i,a)-av(i,a)=l=K*cdf_sc_bool(i,a)-e;

* When federation is not allowed fed_vms should be zero
fed_vms_eq(a)$(doNotFederate(a)).. sum(i,fed_vms(i,a))=e=0;

*fed_vm_eq_plus(i,a)$(vm(i,a) and ((anti_aff_sc(i,a) eq 2)))..  fed_vms(i,a)=e=elastic_vms(i,a)*xboolean(i,a);
fed_vm_eq_plus(i,a)$(vm(i,a) and (1-doNotFederate(a)) and ((anti_aff_sc(i,a) eq 2)))..   fed_vms(i,a)=g=xboolean(i,a);

*-----------------------------------------------------------------------------------------------------------------------
* the probability distributions of the sc's computing capacity are considered independent
joint_cdf_eq(a)..                               joint_cdf(a)=e= prod(i$vm(i,a), 1-(1-cdf_sc(i,a))*xboolean(i,a));
*average_cdf_eq(a)$doNotFederate(a)..            average_cdf(a)*sum(i$vm(i,a), xboolean(i,a))=e=sum(i$vm(i,a), cdf_sc(i,a));
average_cdf_eq(a)$doNotFederate(a)..            average_cdf(a)*sum(i$vm(i,a), 1)=e=xfactor(a)*sum(i$vm(i,a), cdf_sc(i,a));
average_cdf_fed_eq(a)$(1-doNotFederate(a))..    average_cdf(a)*sum(i$vm(i,a), 1)=e=sum(i$vm(i,a), cdf_sc(i,a));

* Probabilistic availability constraint for service a
*prob_availability_Eq(a)$(doNotFederate)..          joint_cdf(a)*xfactor_(a)=g=fi(a)*xfactor_(a);
prob_availability_Eq(a)$(doNotFederate(a) and 0)..  joint_cdf(a)*accepted_vms_percent(a)=g=fi(a)*accepted_vms_percent(a);

* Boolean variables that signal if required availability is satisfied or not
joint_cdf_low(a)$(1-doNotFederate(a))..   joint_cdf(a)-fi(a)=g=K*(joint_cdf_bool(a)-1);
joint_cdf_up(a)$(1-doNotFederate(a))..    joint_cdf(a)-fi(a)=l=K*joint_cdf_bool(a)-e;

fed_vms_low(i,a)$(1-doNotFederate(a))..   fed_vms(i,a)-1=g=K*max_vms(a)*(fed_vms_bool(i,a)-1);
fed_vms_up(i,a)$(1-doNotFederate(a))..    fed_vms(i,a)=l=K*max_vms(a)*fed_vms_bool(i,a);

*------------------------------------------------------------------------------------------------------------------------

* The number of the allocated vms for each service component should be >= basic vms
allocation(i,a)$vm(i,a)..     sum(j, xx(j,i,a))=g=xboolean(i,a)*basic_vms(i,a);

* The maximum number of allocated vms for each service component should be <= basic + elastic vms
allocation_(i,a)$vm(i,a)..    sum(j, xx(j,i,a))=l=xboolean(i,a)*(basic_vms(i,a) + elastic_vms(i,a));

* Total number of accepted basic vms per service
basic_vms_eq(a)..     accepted_basic_vms(a)=e=sum(i$vm(i,a), xboolean(i,a)*basic_vms(i,a));

* Total number of accepted elastic vms per service
elastic_vms_eq(a)..   accepted_elastic_vms(a)=e=sum(i$vm(i,a), xboolean(i,a)*(sum(j,xx(j,i,a))- basic_vms(i,a)));

* Total number of accepted elastic vms per service component
elastic_vms_per_comp_eq(i,a)$vm(i,a)..   accepted_elastic_vms_per_comp(i,a)=e=xboolean(i,a)*(sum(j, xx(j,i,a))- basic_vms(i,a));

* Boolean variables that signal if a service component is admitted or not
* When federation is allowed, then the acceptance of each service component is examined in isolation from the others.
xbool_low_(i,a)$((1-doNotFederate(a)) and vm(i,a))..   sum(j,xx(j,i,a))- basic_vms(i,a)=g=K*max_vms(a)*(xboolean(i,a)-1);
xbool_up_(i,a)$((1-doNotFederate(a)) and vm(i,a))..    sum(j,xx(j,i,a))=l=K*max_vms(a)*xboolean(i,a);

* When federation is not allowed in general by the SP, if one of the service components cannot be allocated
* then the whole service should not be admitted
xbool_low(i,a)$(doNotFederate(a) and vm(i,a))..      sum(j,xx(j,i,a))- basic_vms(i,a)=g=K*max_vms(a)*(xfactor(a)-1);
xbool_up(i,a)$(doNotFederate(a) and vm(i,a))..       sum(j,xx(j,i,a))=l=K*max_vms(a)*xfactor(a);

* Inequalities that constrain boolean variables booj(j) to encode whether a physical host j is involved in the allocation
* of a service or not
bool_low(j)..         sum((i,a),xx(j,i,a)$vm(i,a))-1=g=K*(maxCpus(j)-resCpus(j))*(bool(j)-1);
bool_up(j)..          sum((i,a),xx(j,i,a)$vm(i,a))=l=K*(maxCpus(j)-resCpus(j))*bool(j);

* Inequalities that constrain boolean variables involved_ph_(j,a) to encode whether a physical host is involved in the
* allocation of a specific service
involved_ph_low_(j,a)..  sum(i,xx(j,i,a)$vm(i,a))-1=g=K*max_vms(a)*(involved_ph_(j,a)-1);
involved_ph_up_(j,a)..   sum(i,xx(j,i,a)$vm(i,a))=l=K*max_vms(a)*involved_ph_(j,a);

* Inequalities that constrain boolean variables involved_ph(j,i,a) to encode whether a physical host is involved in the
* allocation of a specific service component of a specific service
involved_ph_low(j,i,a)$vm(i,a)..  xx(j,i,a)-1=g=K*max_vms(a)*(involved_ph(j,i,a)-1);
involved_ph_up(j,i,a)$vm(i,a)..   xx(j,i,a)=l=K*max_vms(a)*involved_ph(j,i,a);

* Inequalities that constrain boolean variables to encode whether a physical host is involved in the
* allocation of a specific combination of service components
low(combs_,j,a)$(affinity_rules(a, combs_) eq 2)..  sum(i$(combs(combs_, i) eq 1),xx(j,i,a))-1=g=K*max_vms(a)*(ph_bool(combs_,j,a)-1);
up(combs_,j,a)$(affinity_rules(a, combs_) eq 2)..   sum(i$(combs(combs_, i) eq 1),xx(j,i,a))=l=K*max_vms(a)*ph_bool(combs_,j,a);

* Boolean variables xfactor(a) signals if the entire service a is admitted or not.
* xfactor(a)=0: total or partial rejection of service a (if xfactor_(a)=0, then this signals complete rejection).
* xfactor(a)=1: full acceptance of service a
admittance(a)..      prod(i$vm(i,a), xboolean(i,a))=e=xfactor(a);

* xfactor_(a) indicates whether there is partial admittance of service a or not.
* xfactor_(a)>0: Partial admittance of service a.
* xfactor_(a)=0: Complete rejection of service a.
admittance_(a)..  xfactor_(a)*sum(i$vm(i,a), 1)=e=sum(i, xboolean(i,a)$vm(i,a));

accepted_vms_percent_eq(a)..   accepted_vms_percent(a)*max_vms(a)=e= accepted_elastic_vms(a) + accepted_basic_vms(a);

* -----------------------------------------------
* Affinity constraints on service component level
* -----------------------------------------------

* Low: No constraint is needed, this means that the instances can be distributed and federated with no restrictions

* Medium: the instances of the service component cannot be considered as candidates for federation.
* This means that in order for the service component to be accepted, all instances must allocated.
* Partial acceptance signals complete rejection of the service component.
affinity_sc_(i,a)$(((aff_sc(i,a) eq 1) or doNotfederate(a)) and vm(i,a))..  sum(j, xx(j,i,a))=e=sum(j, xx(j,i,a))*xboolean(i,a);

* High: the instances of the service component must be allocated on the same physical node
mediator(i,a)$vm(i,a)..                              num_phs(i,a)=e=sum(j,involved_ph(j,i,a));

affinity_sc(i,a)$((aff_sc(i,a) eq 2) and vm(i,a))..  num_phs(i,a)=e=xboolean(i,a);

* ------------------------------------
* Affinity constaints on service level
* ------------------------------------

* No constraint is needed for "Low" affinity, this means that the SCs can be distributed and federated with no restriction.

* Medium: the service components that are specified in the rule must be all accepted, otherwise all of them should be federated,
* given that federation is allowed. If federation is not allowed and the rule cannot be satisfied, then the entire service should
* be rejected.
affinity_rule_(a, combs_)$(affinity_rules(a, combs_) eq 1)..  sum(i$(combs(combs_, i) eq 1),xboolean(i,a))=e=sum(i,combs(combs_, i))*prod(i$(combs(combs_, i) eq 1), xboolean(i,a));

* High: the service components that are specified in the rule must be deployed in the same physical node or federated.
* If federation is not allowed and the rule cannot be satisfied then the entire service should be rejected.
affinity_rule(a, combs_)$(affinity_rules(a, combs_) eq 2)..       sum(j,ph_bool(combs_,j,a))=e=prod(i$(combs(combs_, i) eq 1),xboolean(i,a));

* Added to control the federation of the VMs: if the components are allocated (i.e. xboolean=1, then the federated VMs should be 0).
affinity_rule_plus(a, combs_)$(affinity_rules(a, combs_) ne 0)..  sum(i$(combs(combs_, i) eq 1),xboolean(i,a)*fed_vms(i,a))=e=0;

* -----------------------------------------
* Anti-affinity constaints on service level
* -----------------------------------------

* Low: no constraint is required

* Medium: the service components that are specified in the rule must be deployed in different physical nodes
anti_affinity_rule_(j, a, combs_)$(anti_affinity_rules(a, combs_) eq 1).. sum(i$(combs(combs_, i) eq 1),involved_ph(j,i,a))=l=1;

* High: the service components that are covered by the rule must be deployed in different clouds
anti_affinity_rule(a, combs_)$(anti_affinity_rules(a, combs_) eq 2)..     sum(i$(combs(combs_, i) eq 1),xboolean(i,a))=l=1;

* ----------------------------------------------------
* Anti-affinity constraints on service component level
* ----------------------------------------------------

* Low: no constraint is required

* Medium: the instances of the service component must be allocated on different physical nodes within the same cloud
anti_affinity_sc(i,j,a)$((anti_aff_sc(i,a) eq 1) and vm(i,a))..  xx(j,i,a)=l=1;

* High: the elastic instances of the service component must be allocated on different clouds
anti_affinity_sc_(i,a)$((anti_aff_sc(i,a) eq 2) and vm(i,a))..   sum(j, xx(j,i,a))=l=basic_vms(i,a);

* TREC Constraints
eco_cons(a)..    Eco_s(a) =g= eco_constraint(a)*xfactor(a);

risk_cons(a)..   Risk(a)*xfactor(a) =l= risk_constraint(a);

trust_cons(a)..  Trust(a) =g= trust_constraint(a)*xfactor(a);

cost_eq(a)..     cost(a)=e=sum(joff, involved_ph_(joff,a)*z(joff)) + Pav(a)*(1-average_cdf(a)) + sum(i,fed_cost_per_vm(i,a)*fed_vms(i,a));

cost_cons(a)..   cost(a)=l= cost_constraint(a);

new(i,a)$((1-doNotFederate(a)) and vm(i,a))..    cdf_sc(i,a)=g=av(i,a)*(1-xboolean(i,a))*fed_vms_bool(i,a);

* Objective function

*obj..                 objective=e= wc*sum(a, accepted_vms_percent(a)*Gain(a)) - wc*sum(joff, bool(joff)*z(joff)) - wr*sum(a,Pav(a)*(1-average_cdf(a)))+
*                                   we*sum(j, bool(j)*z(j)*Eco(j)) + we*sum(a, accepted_vms_percent(a)*Eco_s(a)) + wt*sum(a, accepted_vms_percent(a)*Trust(a)) -
*                                   wr*sum(a, accepted_vms_percent(a)*Risk(a)) - wc*sum((i,a),fed_cost_per_vm(i,a)*fed_vms(i,a));
*                                   wc*sum((i,a),fed_cost_per_vm(i,a)*(1-doNotFederate(a))*cdf_sc_bool(i,a)*(1-xboolean(i,a))*(1-(cdf_sc(i,a)-av(i,a))));
*                                   wc*sum((i,a),fed_cost_per_vm(i,a)*(1-doNotFederate(a))*cdf_sc_bool(i,a)*(1-xboolean(i,a))*(cdf_sc(i,a)-av(i,a)));

obj..                 objective=e= wc*sum(a, average_cdf(a)*Gain(a)) - wc*sum(joff, bool(joff)*z(joff)) - wr*sum(a,accepted_vms_percent(a)*Pav(a)*(1-average_cdf(a)))+
                                   we*sum(j, bool(j)*z(j)*Eco(j)) + we*sum(a, average_cdf(a)*Eco_s(a)) + wt*sum(a, average_cdf(a)*Trust(a)) -
                                   wr*sum(a, average_cdf(a)*Risk(a)) - wc*sum((i,a),fed_cost_per_vm(i,a)*fed_vms(i,a));
*                                   wc*sum((i,a),fed_cost_per_vm(i,a)*(1-doNotFederate(a))*cdf_sc_bool(i,a)*(1-xboolean(i,a))*(1-(cdf_sc(i,a)-av(i,a))));

*obj..                 objective=e= wc*sum(a, accepted_vms_percent(a)*Gain(a)) - wc*sum(joff, bool(joff)*z(joff)) - wr*sum(a,Pav*(1-joint_cdf(a)))+
*                                   we*sum(j, bool(j)*z(j)*Eco(j)) + we*sum(a, accepted_vms_percent(a)*Eco_s(a)) + wt*sum(a, accepted_vms_percent(a)*Trust(a)) -
*                                   wr*sum(a, accepted_vms_percent(a) *Risk(a));

* obj..               objective=e=  wc*sum(a, xfactor_(a)*Gain(a)) - wc*sum(joff, bool(joff)*z(joff)) - wr*sum(a,Pav*(1-joint_cdf(a)))+
*                                   we*sum(j, bool(j)*z(j)*Eco(j)) + we*sum(a, xfactor_(a)*Eco_s(a)) + wt*sum(a, xfactor_(a)*Trust(a)) -
*                                   wr*sum(a, xfactor_(a)*Risk(a));

Model optimis /all/;

Option minlp=baron;
Option optcr=0.001;

$onecho>baron.opt
*NLPSol=9
*LPSol=8
*workspace=64
$offecho

*Option minlp=coincouenne;
*Option minlp=coinbonmin;
*Option minlp=lindoglobal;
*Option minlp=sbb;

optimis.scaleopt=1;
*optimis.optfile=1;

Solve optimis using minlp maximizing objective;

* result file is result.csv, if /result.csv/ omitted the file will be result.put
file result /result.csv/;
* defines result as the current output file
put result;
* the resulting file is comma-delimited so that the parsing is easier
* if omitted the resulting file will have better looking format
result.pc=5;

* puts xfactor's related text i.e "Admission Control Result"
* '/'=new line
put '**'xfactor.ts'**' //;
* writes labels and variables values
loop(a, put a.tl, xfactor.l(a)/);

put /'**'xx.ts'**' //;
loop((j,i,a)$vm(i,a), put j.tl, i.tl, a.tl, xx.l(j,i,a)/);

put /'**'accepted_elastic_vms.ts'**' //;
loop(a, put a.tl, accepted_elastic_vms.l(a)/);

put /'**'accepted_elastic_vms_per_comp.ts'**' //;
loop((i,a)$vm(i,a), put i.tl, a.tl, accepted_elastic_vms_per_comp.l(i,a)/);

put /'**'fed_vms.ts'**' //;
loop((i,a)$vm(i,a), put i.tl, a.tl, fed_vms.l(i,a)/);

put /'**'Trust.ts'**' //;
loop(a, put a.tl, Trust(a)/);

put /'**'Risk.ts'**' //;
loop(a, put a.tl, Risk(a)/);

put /'**'Eco_s.ts'**' //;
loop(a, put a.tl, Eco_s(a)/);

put /'**'cost.ts'**' //;
loop(a, put a.tl, cost.l(a)/);


* closes the current output file
putclose

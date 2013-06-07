/**

Copyright 2013 ATOS SPAIN S.A. 

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.interopt.provider.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import eu.optimis.interopt.provider.ServiceComponent;
import eu.optimis.interopt.provider.ServiceInstantiationException;
import eu.optimis.interopt.provider.VMManagementSystemClient;
import eu.optimis.interopt.provider.VMProperties;
import eu.optimis.types.xmlbeans.servicemanifest.XmlBeanServiceManifestDocument;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class AmazonClient
        implements VMManagementSystemClient
{
    private static Logger log = Logger.getLogger( AmazonClient.class );

    private static final Map<InstanceType, Double> memMap;
    static {
        Map<InstanceType, Double> aMap = new EnumMap<InstanceType, Double>(InstanceType.class);
        aMap.put(InstanceType.T1Micro, 0.613);
        aMap.put(InstanceType.M1Small, 1.7);
        aMap.put(InstanceType.M1Medium, 3.75);
        aMap.put(InstanceType.M1Large, 7.5);
        aMap.put(InstanceType.M1Xlarge, 15.0);
        aMap.put(InstanceType.M32xlarge, 30.0);
        memMap = Collections.unmodifiableMap(aMap);
    }
    
    private static final Map<InstanceType, Integer> coresMap;
    static {
        Map<InstanceType, Integer> aMap = new EnumMap<InstanceType, Integer>(InstanceType.class);
        aMap.put(InstanceType.T1Micro, 1);
        aMap.put(InstanceType.M1Small, 1);
        aMap.put(InstanceType.M1Medium, 1);
        aMap.put(InstanceType.M1Large, 2);
        aMap.put(InstanceType.M1Xlarge, 4);
        aMap.put(InstanceType.M32xlarge, 8);
        coresMap = Collections.unmodifiableMap(aMap);
    }
    
    private static final Map<InstanceType, Set<String>> archMap;
    static {
        Map<InstanceType, Set<String>> aMap = new EnumMap<InstanceType, Set<String>>(InstanceType.class);
        Set<String> s1 = new HashSet<String>();
        s1.add("x86");
        s1.add("x64");
       
        Set<String> s2 = new HashSet<String>();
        s1.add("x64");
        
        aMap.put(InstanceType.T1Micro, s1);
        aMap.put(InstanceType.M1Small, s1);
        aMap.put(InstanceType.M1Medium, s1);
        aMap.put(InstanceType.M1Large, s2);
        aMap.put(InstanceType.M1Xlarge, s2);
        aMap.put(InstanceType.M32xlarge, s2);
        archMap = Collections.unmodifiableMap(aMap);
    }
    
    private static String SECURITY_GROUP = "optimis";
    private static int maxvms = 10;
    private String access_key;
    private String secret_key;
    private AvailabilityZone availabilityZone = null;
    
    public void setAuth(String auth_username, String password) {
        this.access_key = auth_username;
        this.secret_key = password;
    }

    @Override
    public void deployService(String service_id, List<ServiceComponent> serviceComponents, XmlBeanServiceManifestDocument manifest) throws ServiceInstantiationException {
        
        AmazonEC2 ec2 = getAmazonEC2Client();
        
        log.info("Deploying service...");
        if (isDeployed(service_id)) {
            throw new ServiceInstantiationException("This service is already deployed! "
                    + "Terminate it before deploying it again.", new java.lang.Throwable());
        }
        // Get the number of VMs to deploy
        int totalVms = 0;

        for (ServiceComponent sc : serviceComponents) {
            totalVms = totalVms + sc.getInstances();
        }

        // If sum < maxvms invoke createVM method as many times as needed
        if (totalVms > AmazonClient.maxvms)
        {
            throw new ServiceInstantiationException("Number of VMs to deploy exceeds the maximum", new java.lang.Throwable());
        }

        for (ServiceComponent sc : serviceComponents) {
            int numInstances = sc.getInstances();
            log.info("Number of vm instances to deploy: " +  numInstances);
            
            String imageId = sc.getImage();
            InstanceType type = selectInstanceType(sc);
            
            Placement placement = new Placement();
            placement.setAvailabilityZone(availabilityZone.getZoneName());
            RunInstancesRequest req = new RunInstancesRequest(imageId, numInstances, numInstances);
            ArrayList<String> securityGroups = new ArrayList<String>();
            securityGroups.add(SECURITY_GROUP);
            req.setSecurityGroupIds(securityGroups);
            req.setInstanceType(type);
            req.setPlacement(placement);
            //req.setMonitoring(true);
            try {
                RunInstancesResult res = ec2.runInstances(req);
                List<Instance> instances = res.getReservation().getInstances();
                log.info("Creating Tags...");
                for (Instance inst : instances) {
                    Tag tag = new Tag("serviceid", service_id);
                    List<Tag> tags = new ArrayList<Tag>();
                    tags.add(tag);
                    List<String> resources = new ArrayList<String>();
                    resources.add(inst.getInstanceId());
                    CreateTagsRequest req2 = new CreateTagsRequest(resources, tags);
                    ec2.createTags(req2);
                }
            } catch (AmazonServiceException e) {
                log.error("Service deployment has failed: ");
                log.error(printServiceException(e)); 
                throw new ServiceInstantiationException("Service deployment has failed: " + e.getMessage(),
                            new java.lang.Throwable());
            }
            log.info("Service Deployed successfully!");
        }
    }

    @Override
    public List<VMProperties> queryServiceProperties(String serviceId) throws UnknownServiceException {

        List<VMProperties> list = null;

        AmazonEC2 ec2 = getAmazonEC2Client();
        log.info("Querying service VMs...");

        List<String> sids = new ArrayList<String>();
        sids.add(serviceId);
        Filter filt = new Filter("tag:serviceid", sids);
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(filt);
        DescribeInstancesRequest req = new DescribeInstancesRequest();
        req.setFilters(filters);
        try {
            DescribeInstancesResult res = ec2.describeInstances(req);

            List<Instance> instances = new ArrayList<Instance>();
            for (Reservation r : res.getReservations()) {
                instances.addAll(r.getInstances());
            }
            log.info("#VMs found: " + instances.size());
            list = new ArrayList<VMProperties>();
            for (Instance inst : instances) {
            	String status = inst.getState().getName().toString();
            	if (!status.equals(InstanceStateName.Terminated.toString()))
            	{
            		VMProperties prop = new VMProperties();
                    prop.setId(inst.getInstanceId());
                    prop.setStatus(inst.getState().getName().toString());
                    prop.setHostname(inst.getPublicDnsName());
                    prop.setIp(inst.getPublicIpAddress());
                    prop.put(VMProperties.AWS_INSTANCE_TYPE, inst.getInstanceType());
                    list.add(prop);
            	}
                
            }
        } catch (AmazonServiceException e) {
            log.error("Service query has failed: ");
            log.error(printServiceException(e)); 
            throw new UnknownServiceException("Service query has failed: " + e.getMessage());
        }

        return list;
    }

    @Override
    public void terminate(String serviceId) throws UnknownServiceException {

        AmazonEC2 ec2 = getAmazonEC2Client();
        log.info("Terminating service...");
        
        List<VMProperties> vms = queryServiceProperties(serviceId);        
        List<String> instances = new ArrayList<String>();
        for (VMProperties vm : vms) {
            if (!(vm.getStatus().equals(InstanceStateName.Terminated.toString()) ||
                    vm.getStatus().equals(InstanceStateName.ShuttingDown.toString()))) 
            {
                instances.add(vm.getId());
                log.debug("Instance to stop: " + vm.getId());
            }
        }
        
        if (instances.size() == 0) 
        {
        	log.info("There are no instances to be terminated!");
        	return;
        }
        
        TerminateInstancesRequest req = new TerminateInstancesRequest(instances);
        try {
            ec2.terminateInstances(req);
        } catch (AmazonServiceException e) {
            log.error("Service termination has failed: ");
            log.error(printServiceException(e)); 
            throw new UnknownServiceException("Service termination has failed: " + e.getMessage());
        }
    }
    
    public boolean isDeployed(String serviceId) {
        List<VMProperties> vms = new ArrayList<VMProperties>();        
        try {
            vms = queryServiceProperties(serviceId);
        } catch (UnknownServiceException e) {
            return false;
        }
        for (VMProperties vm : vms) {
            //System.out.println(vm.getStatus() + " - " + InstanceStateName.Terminated.toString());
            if (!(vm.getStatus().equals(InstanceStateName.Terminated.toString()) ||
                    vm.getStatus().equals(InstanceStateName.ShuttingDown.toString()))) {
                return true;
            }
        }
        return false;
    }
    
    public InstanceType selectInstanceType(ServiceComponent sc) {
    
        double mem = sc.getMemory();
        int cores = sc.getCores();
        String arch = sc.getArchitecture();
        
        InstanceType res;
        
        if (isOfType(InstanceType.T1Micro, mem, cores, arch)) {
            res = InstanceType.T1Micro;
        } else if (isOfType(InstanceType.M1Small, mem, cores, arch)) {
            res = InstanceType.M1Small;
        } else if (isOfType(InstanceType.M1Medium, mem, cores, arch)) {
            res = InstanceType.M1Medium;
        } else if (isOfType(InstanceType.M1Large, mem, cores, arch)) {
            res = InstanceType.M1Large;
        } else if (isOfType(InstanceType.M1Xlarge, mem, cores, arch)) {
            res = InstanceType.M1Xlarge;
        } else {
            res = InstanceType.M32xlarge;
        }
        
        log.info("Selected instance type: " + res.toString());
        
        //return res;
        return InstanceType.T1Micro;// ONLY FOR TESTING!!!!!
    }
    
    private boolean isOfType(InstanceType type, double mem, Integer cores, String arch) {
        return (mem <= memMap.get(type) && cores <= coresMap.get(type) && archMap.get(type).contains(arch));
    }
    
    public AmazonEC2 getAmazonEC2Client() {

        log.info("Getting EC2 Client and selecting availability zone");

        AmazonEC2 ec2 = new  AmazonEC2Client(new BasicAWSCredentials(access_key, secret_key));
        List<AvailabilityZone> availabilityZones = describeAZs("ec2.eu-west-1.amazonaws.com", ec2);

        if (!availabilityZones.isEmpty()) {
            availabilityZone = availabilityZones.get(0);
            log.info("Selected AZ: " + availabilityZone.getRegionName() + " - " + availabilityZone.getZoneName());
        } else {
            log.info("No zone found available in EU region, trying on other regions");
            String[] endpoints = {"ec2.us-east-1.amazonaws.com", "ec2.us-west-2.amazonaws.com",
            "ec2.us-west-1.amazonaws.com","ec2.ap-southeast-1.amazonaws.com","ec2.ap-southeast-2.amazonaws.com",
            "ec2.ap-northeast-1.amazonaws.com","ec2.sa-east-1.amazonaws.com"};
            for (int i = 0;i < endpoints.length && availabilityZone != null;i++) {
                availabilityZones = describeAZs(endpoints[i], ec2);
                if (!availabilityZones.isEmpty()) {
                    availabilityZone = availabilityZones.get(0);
                    log.info("Selected AZ: " + availabilityZone.getRegionName() + " - " + availabilityZone.getZoneName());
                }
            }
        }

        return ec2;
    }
    
    public String describeImage(String imageId) {
        
        AmazonEC2 ec2 = getAmazonEC2Client();
        DescribeImagesRequest req = new DescribeImagesRequest();
        List<Filter> filters = new ArrayList<Filter>();
        List<String> imgs = new ArrayList<String>();
        imgs.add(imageId);
        filters.add(new Filter("image-id", imgs));
        req.setFilters(filters);
        
        DescribeImagesResult res = ec2.describeImages(req);
        return res.getImages().get(0).getName() + "----" + res.getImages().get(0).getDescription();
    }
    
    private List<AvailabilityZone> describeAZs(String endpoint, AmazonEC2 ec2) {
       
        ec2.setEndpoint(endpoint);

        List<Filter> filters = new LinkedList<Filter>();
        List<String> states = new LinkedList<String>();
        states.add("available");
        filters.add(new Filter("state", states));

        DescribeAvailabilityZonesRequest req = new DescribeAvailabilityZonesRequest();
        req.setFilters(filters);
        
        return (ArrayList <AvailabilityZone>) ec2.describeAvailabilityZones(req).getAvailabilityZones();    
    }
    
    private String printServiceException(AmazonServiceException se) {
        return se.getMessage()
                       + "(HTTP Code: " + se.getStatusCode() + " - AWS Code: " + se.getErrorCode()
                       + " - Error Type: " + se.getErrorType() + " )";
    }
    
}

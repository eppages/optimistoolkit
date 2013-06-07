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

package eu.optimis.monitoring.amazoncollector;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.Statistic;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Volume;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author A545568
 */
public class MeasurementsHelper {
   
    private static final String SERVICE_ID_TAG = "serviceid";
    private static final long HOUR_AND_HALF_AGO = 5400000; // 2 minutes
    private static final long PERIOD = 180000; // 3 minutes
    
    private String access_key;
    private String secret_key;
    

    public MeasurementsHelper(String access_key, String secret_key) {
        this.access_key = access_key;
        this.secret_key = secret_key;
    }
    
    private static final Map<InstanceType, Integer> memMap;
    static {
        Map<InstanceType, Integer> aMap = new EnumMap<InstanceType, Integer>(InstanceType.class);
        aMap.put(InstanceType.T1Micro, 613);
        aMap.put(InstanceType.M1Small, 1700);
        aMap.put(InstanceType.M1Medium, 3750);
        aMap.put(InstanceType.M1Large, 7500);
        aMap.put(InstanceType.M1Xlarge, 15000);
        aMap.put(InstanceType.M32xlarge, 30000);
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
    
    private static final Map<InstanceType, Integer> speedMap;
    static {
        Map<InstanceType, Integer> aMap = new EnumMap<InstanceType, Integer>(InstanceType.class);
        aMap.put(InstanceType.T1Micro, 1100);
        aMap.put(InstanceType.M1Small, 1100);
        aMap.put(InstanceType.M1Medium, 2200);
        aMap.put(InstanceType.M1Large, 4400);
        aMap.put(InstanceType.M1Xlarge, 8800);
        aMap.put(InstanceType.M32xlarge, 30800);
        speedMap = Collections.unmodifiableMap(aMap);
    }
    
    public List<Measurement> getMeasurements() {
        AmazonEC2 ec2 = getAmazonEC2Client();
        List<Measurement> measurements = new LinkedList<Measurement>();
        
        List<Filter> filters = new LinkedList<Filter>();
        List<String> tags = new LinkedList<String>();
        tags.add(SERVICE_ID_TAG);
        Filter f = new Filter("tag-key",tags);
        filters.add(f);
        DescribeInstancesRequest req = new DescribeInstancesRequest();
        req.setFilters(filters);
        
        DescribeInstancesResult res;
        try {
            res = ec2.describeInstances(req);
        } catch (AmazonServiceException se) {
            printServiceException(se);
            throw new RuntimeException("Exception while trying to get information about running instances");
        }
        List<Instance> instances = new ArrayList<Instance>();
        for (Reservation r : res.getReservations()) {
            instances.addAll(r.getInstances());
        }
        
        for (Instance i : instances) {
            String instance_id = i.getInstanceId();
            InstanceType type = InstanceType.fromValue(i.getInstanceType());
            String ami_id = i.getImageId();
            String architecture = i.getArchitecture();
            String state = i.getState().getName();
            
            List<String> volume_ids = new LinkedList<String>();
            for (InstanceBlockDeviceMapping mapping : i.getBlockDeviceMappings()) {
                volume_ids.add(mapping.getEbs().getVolumeId());
            }
            
            String service_id = "";
            List<Tag> itags = i.getTags();
            for (Tag t : itags) {
                if (t.getKey().equals(SERVICE_ID_TAG)) {
                    service_id = t.getValue();
                    break;
                }
            }
            
            try {
                measurements.add(new Measurement("machine_type",architecture,"",new Date(),instance_id, service_id));
                measurements.add(new Measurement("vm_status",state,"",new Date(),instance_id, service_id));
                
                Measurement m_mem = getTotalMemory(type, instance_id, service_id);
                Measurement m_cpu = getCPUSpeed(type, instance_id, service_id);
                Measurement m_cores = getNumVCores(type, instance_id, service_id);
                Measurement m_os = getOSRelease(ami_id, instance_id, service_id);
                Measurement m_disk = getTotalDiskSize(volume_ids, instance_id, service_id);
                Measurement m_memused = getCWMeasurement("MemoryUsed", "mem_used" ,true, instance_id, service_id);
                Measurement m_cpuutil = getCWMeasurement("CPUUtilization", "cpu_used" ,false, instance_id, service_id);
                Measurement m_nout = getCWMeasurement("NetworkOut", "network_out" ,false, instance_id, service_id);
                Measurement m_nin = getCWMeasurement("NetworkIn", "network_in" ,false, instance_id, service_id);
                
                if (m_mem != null) measurements.add(m_mem);
                if (m_cpu != null) measurements.add(m_cpu);
                if (m_cores != null) measurements.add(m_cores);
                if (m_os != null) measurements.add(m_os);
                if (m_disk != null) measurements.add(m_disk);
                if (m_memused != null) measurements.add(m_memused);
                if (m_cpuutil != null) measurements.add(m_cpuutil);
                if (m_nout != null) measurements.add(m_nout);
                if (m_nin != null) measurements.add(m_nin);
                
            } catch (AmazonServiceException se) {
                printServiceException(se);
                throw new RuntimeException("Exception while trying to retrieve some metrics");
            }
        }
        
        return measurements;
    }
    
    private Measurement getCWMeasurement(String metric_name, String optimis_metric_name, boolean custom, String instance_id, String service_id) {
        AmazonCloudWatch cw = getAmazonCloudWatchClient();
        String ns = custom ? "System/Linux" : "AWS/EC2";

        List<Dimension> dimensions = new LinkedList<Dimension>();
        Dimension dim = new Dimension();
        dim.setName("InstanceId");
        dim.setValue(instance_id);
        dimensions.add(dim);
        
        GetMetricStatisticsRequest req = new GetMetricStatisticsRequest();
        req.setStartTime(new Date(System.currentTimeMillis() - HOUR_AND_HALF_AGO - PERIOD));
        req.setEndTime(new Date(System.currentTimeMillis() - HOUR_AND_HALF_AGO));
        req.setMetricName(metric_name);
        req.setNamespace(ns);
        List<String> statistics = new LinkedList<String>();
        statistics.add(Statistic.Average.toString());
        req.setStatistics(statistics);
        req.setPeriod((int) PERIOD/1000);
        req.setDimensions(dimensions);
        
        GetMetricStatisticsResult res = cw.getMetricStatistics(req);
        if (res.getDatapoints().isEmpty()) return null;
        Datapoint dp = res.getDatapoints().get(0);
        return new Measurement(optimis_metric_name,dp.getAverage().toString(),dp.getUnit(),new Date(),instance_id, service_id);
    }
    
    private Measurement getTotalMemory(InstanceType type, String instance_id, String service_id) {
        return new Measurement("mem_total",memMap.get(type).toString(),"MB",new Date(),instance_id, service_id);       
    }
    
    private Measurement getCPUSpeed(InstanceType type, String instance_id, String service_id) {
        return new Measurement("cpu_speed",speedMap.get(type).toString(),"MHz",new Date(),instance_id, service_id);       
    }
    
    private Measurement getNumVCores(InstanceType type, String instance_id, String service_id) {
        return new Measurement("cpu_vnum",coresMap.get(type).toString(),"",new Date(),instance_id, service_id);       
    }
    
    private Measurement getOSRelease(String ami_id, String instance_id, String service_id) {
        AmazonEC2 ec2 = getAmazonEC2Client();
        DescribeImagesRequest req = new DescribeImagesRequest();
        List<String> ids = new LinkedList<String>();
        ids.add(ami_id);
        req.setImageIds(ids);
        DescribeImagesResult res = ec2.describeImages(req);
        String platform = res.getImages().get(0).getPlatform();
        if (platform == null || platform.isEmpty()) {
            platform = "linux";
        }
        return new Measurement("os_release",platform,"",new Date(),instance_id, service_id);
    }
    
    private Measurement getTotalDiskSize(List<String> volume_ids, String instance_id, String service_id) {
        AmazonEC2 ec2 = getAmazonEC2Client();
        DescribeVolumesRequest req = new DescribeVolumesRequest(volume_ids);
        DescribeVolumesResult res = ec2.describeVolumes(req);
        int total_size = 0;
        for (Volume vol : res.getVolumes()) {
            total_size += vol.getSize();
        }
        return new Measurement("disk_total",String.valueOf(total_size),"GB",new Date(),instance_id, service_id);
    }
           
    public AmazonEC2 getAmazonEC2Client() {
        AmazonEC2 ec2 = new  AmazonEC2Client(new BasicAWSCredentials(access_key, secret_key));
        ec2.setEndpoint("ec2.eu-west-1.amazonaws.com");
        return ec2;
    }
    
    public AmazonCloudWatch getAmazonCloudWatchClient() {
        AmazonCloudWatch cw = new  AmazonCloudWatchClient(new BasicAWSCredentials(access_key, secret_key));
        cw.setEndpoint("monitoring.eu-west-1.amazonaws.com");
        return cw;
    }
    
    private String printServiceException(AmazonServiceException se) {
        return se.getMessage()
                       + "(HTTP Code: " + se.getStatusCode() + " - AWS Code: " + se.getErrorCode()
                       + " - Error Type: " + se.getErrorType() + " )";
    }
}

# Amazon Monitoring Collector for OPTIMIS

AWSCollector.
1.0.0-SNAPSHOT.
20.02.2013.

## License and Copyright

Copyright [2013] [Oriol Collell Martin]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

## Introduction

The Amazon Collector component is part of the OPTIMIS Europeran research project (http://www.optimis-project.eu/) and it is used
by the Monitoring Infrastructure to gather monitoring measurements from OPTIMIS services running on Amazon.
Once this component is integrated with the Monitoring Infrastructure it could be
used to automatically gather monitoring data from Amazon in a similar way as it is done by other providers. These data could then be feed to the
Aggregator and stored in the Monitoring DB to reason about appropriate actions to take such as scale down or undeploy a service.

We have used the Java AWS SDK to implement this component. More information about the SDK can be found in http://aws.amazon.com/es/sdkforjava/

## Functions

This script is intended to be run periodically by a task scheduler such as *cron*. The script scans for all OPTIMIS services that are running on Amazon
an gets a set of monitoring statistics for each of them. It performs the following sequence of actions:
1.	Get all EC2 instances running OPTIMIS services.
2.	For each instance, get metric measurements from several AWS sources.
3.	Create an XML file containing these measurements.
4.	Send the XML file to the Aggregator.

Measurements are obtained from heterogeneous sources:
* From the type of EC2 instance
* From the attributes of the EC2 instance
* From the attributes of the AMI of the instance
* From AWS provided CloudWatch metrics
* From Custom CloudWatch metrics

Each measurement is rendered as the following piece of XML:
```
<monitoring_resource>
    <physical_resource_id>optimisaws001</physical_resource_id>
    <metric_name>machine_type</metric_name>
    <metric_value>i386</metric_value>
    <metric_unit />
    <metric_timestamp>Jan 30 15:00:16 CET 2013</metric_timestamp>
    <service_resource_id>d3f64f3f-c57b-460c-</service_resource_id>
    <virtual_resource_id>i-aa7dbde0</virtual_resource_id>
    <resource_type>virtual</resource_type>
    <monitoring_information_collector_id>aws001</monitoring_information_collector_id>
</monitoring_resource>
```

Once all measurements have been obtained, an XML document is generated containing information about them and it is sent to the Aggregator through its
WS interface.

## Using the Software
**IMPORTANT NOTE**: This software needs to point to an Aggreggator to be able to use it, therefore it cannot be executed as a standalone application, it
needs to be run as part of a full OPTMIS deployment

You need an AWS account and an Aggreggator to be able to use this software. The access and secret keys and the path to the Aggregator 
have to be specified in a configuration file with the follwing contents:
```
AWSAccessKey=<Your AWS Access Key here>
AWSSecretKey=<Your AWS Secret Key here>
AggregatorURL=http://localhost:8080/AggregatorTest/webresources/monitoringresources/virtual
```

The path of this file has to be passed as a paramter to the GUI application by using the "-c <path>" argument.

### Software Dependencies

This component has been written in Java, therefore it needs a JVM to run. We have used Maven to manage the dependencies and build process of the project,
therefore, you will need to have Maven installed too.

Additionally it needs an Aggregator component to run. Other dependencies with libraries external to OPTIMIS are described in the POM.


### Installation and Execution Instructions

This sceript can be installed by running the `mvn clean install assembly:single` command on the project root. This generates a ".jar" file with
all the compiled code and dependencies in the "target" directory. This jar file can then be executed by running `java -jar [jar_file] -c <conf_path> -i <collector_id>`
Both arguments are optional. The `-c <path>` arguments tells the script where to find the AWS keys and Aggregator URL. The `-i <collector_id>` argument
can be included to tell the script the ID value to use in the "monitoring_information_collector_id" attribute.

## Contributors
Oriol Collell Martin

## Contact Information and Website

http://www.optimis-project.eu/

We welcome your feedback, suggestions and contributions. Contact us
via email if you have questions, feedback, code submissions, 
and bug reports.

For general inquiries, see http://www.optimis-project.eu/contact

You can submit bug, patches, software contributions, and feature 
requests using Bugzilla.  
Access Bugzilla at: 
http://itforgebugzilla.atosresearch.eu/bugzilla/enter_bug.cgi?product=Optimis 

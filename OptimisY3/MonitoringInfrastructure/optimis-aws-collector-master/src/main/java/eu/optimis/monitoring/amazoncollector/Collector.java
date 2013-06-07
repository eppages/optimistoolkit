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

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author oriol.collell
 */
public class Collector {   
    
    private static final String DEFAULT_ID = "aws001";
    private static final String DEFAULT_PROPERTIES_PATH = "collector.properties";
    private static final String ID_OPT = "i";
    private static final String PATH_OPT = "c";
    private static final String AWS_ACCESS_PROP = "AWSAccessKey";
    private static final String AWS_SECRET_PROP = "AWSSecretKey";
    private static final String AGGREGATOR_PROP = "AggregatorURL";
        
    private String propertiesPath;
    private String accessKey;
    private String secretKey;
    private String aggregatorURL;
    
    public Collector() {
    }
    
    public static void main(String[] args) {
        
        Collector collector = new Collector();
        Options options = new Options();
        Option id = OptionBuilder.withArgName("id").hasArg().withDescription("use given collector ID").create(ID_OPT);
        Option conf = OptionBuilder.withArgName("configuration path").hasArg().withDescription("Configuration path").create(PATH_OPT);
        options.addOption(id);
        options.addOption(conf);
        
        CommandLineParser parser = new GnuParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        }
        catch (ParseException exp) {
            System.err.println("Incorrect parameters: " + exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("collector", options);
            System.exit(2); //Incorrect usage
        }
        
        if (line.hasOption(ID_OPT)) {
            String cid = line.getOptionValue(ID_OPT);
            Measurement.setDefCollectorId(cid);
            System.out.println("Using Collector ID: " + cid);
        } else {
            System.out.println("Using default Collector ID: " + DEFAULT_ID
                    + " (To use custom ID use -i argument)");
            Measurement.setDefCollectorId(DEFAULT_ID);
        }
        
        if (line.hasOption(PATH_OPT)) {
            collector.propertiesPath = line.getOptionValue(PATH_OPT);
            System.out.println("Using Properties file: " + collector.propertiesPath);
        } else {
            System.out.println("Using default Configuration file: " + DEFAULT_PROPERTIES_PATH
                    + " (To use custom path use -c argument)");
            collector.propertiesPath = DEFAULT_PROPERTIES_PATH;
        }
        
        collector.getProperties();
        
        MeasurementsHelper helper = new MeasurementsHelper(collector.accessKey, collector.secretKey);
        List<Measurement> measurements = helper.getMeasurements();
        
        String xmlData = XMLHelper.createDocument(measurements);
        RESTHelper rest = new RESTHelper(collector.aggregatorURL);
        rest.sendDocument(xmlData);
    }
    
    private void getProperties() {
        accessKey = getStringProperty(AWS_ACCESS_PROP);
        if (accessKey.isEmpty()) {
            throw new RuntimeException("No Access Key specified in the properties file");
        }
        
        secretKey = getStringProperty(AWS_SECRET_PROP);
        if (secretKey.isEmpty()) {
            throw new RuntimeException("No Secret Key specified in the properties file");
        }
        
        aggregatorURL = getStringProperty(AGGREGATOR_PROP);
        if (aggregatorURL.isEmpty()) {
            throw new RuntimeException("No Aggregator URL specified in the properties file");
        }
        
        /*System.out.println("ACCESS KEY: " + accessKey);
        System.out.println("SECRET KEY: " + secretKey);
        System.out.println("AGGREGATOR URL: " + aggregatorURL);*/
    }
    
    public String getStringProperty(String key) {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(propertiesPath);
            props.load(in);

            String defaultValue = props.getProperty(key);
            return System.getProperty(key, defaultValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

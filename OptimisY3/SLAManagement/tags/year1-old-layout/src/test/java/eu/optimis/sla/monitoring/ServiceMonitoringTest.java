package eu.optimis.sla.monitoring;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;

import eu.optimis.sla.accounting.ServiceMonitoring;
import eu.optimis.sla.serviceMonitoringTypes.SLASeriveMonitoringRecordType;

import junit.framework.TestCase;

public class ServiceMonitoringTest extends TestCase {
    
    private static final Logger log = Logger.getLogger(ServiceMonitoringTest.class);
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testServiceMonitoring() throws Exception {
        
        try {
            
            String service_ID = "DemoApp";
            
            Calendar start_time = Calendar.getInstance();
            
            Calendar end_time = Calendar.getInstance();
            
            SLASeriveMonitoringRecordType[] records = ServiceMonitoring.getMonitoringRecords( service_ID, start_time, end_time );
            assertNotNull( records );
            
            for ( int i = 0; i < records.length; i++ ) {
                log.info(records[i].xmlText(new XmlOptions().setSavePrettyPrint()));
            }
            
        } catch (Exception e) {
            log.error(e);
            fail("testServiceMonitoring failed: " + e.getMessage());
        }
    }
}

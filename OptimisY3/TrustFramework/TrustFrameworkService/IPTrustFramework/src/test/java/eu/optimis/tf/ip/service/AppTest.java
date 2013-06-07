/**

Copyright 2013 ATOS SPAIN S.A. and City University London

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
Juan Luis Prieto, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.optimis.tf.ip.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import eu.optimis.mi.monitoring_resources.MonitoringResourceDataset;
import eu.optimis.tf.ip.service.clients.MonitoringClient;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	Logger log = Logger.getLogger(this.getClass().getName());
	// String spId = "OPTIMUMWEB";
	// public void testAddSPInfo(){
	// TrecSPinfoDAO tspidao = new TrecSPinfoDAO();
	// try {
	// tspidao.addSP(spId, spId);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// public void testLegal(){
	// SPLegalAspects spla = new SPLegalAspects();
	// System.out.println(spla.calculateLegalAspects("ecd4ce61-b3bc-4a55-847b-0428531a2cd8"));
	// }

	/*
	public void testDates() throws ParseException {
		SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = dfm.getCalendar();
		cal.setTime(cal.getTime());
		Date now = dfm.parse(dfm.format(cal.getTime()));
		System.out.println(dfm.format(cal.getTime()));
		cal.add(Calendar.HOUR, -1);
		Date oneHourBack = cal.getTime();
		System.out.println(dfm.format(oneHourBack).toString());
		oneHourBack = dfm.parse(dfm.format(oneHourBack).toString());

		Date to = new Date((long)1342527341*1000);
		Date from = new Date((long)1342527233*1000);
		
		/*
		Date to = null;
		Date from = null;
		
        try 
        {
            from = dfm.parse("2012-10-01 00:00:00");
            to = dfm.parse("2012-12-31 00:00:00");
        }
        catch (ParseException e) 
        {
            e.printStackTrace();
        }
        
		
		System.out.println ("Date from: " + dfm.format(from.getTime()));
		System.out.println ("Date to: " + dfm.format(to.getTime()));
				
		MonitoringClient mc = new MonitoringClient();
		List<MonitoringResourceDataset> lmcds = mc.getMonitoringServiceInfo(
				"a4169454-a7bc-441c-b1b2-378ede095180", from, to);
		System.out.println(lmcds.size());
		for (MonitoringResourceDataset mcd : lmcds) {
			System.out.println(mcd.getMetric_name());
		}
	}
	*/
	
	public void testGregorianDates() throws ParseException {
		Calendar cal = new GregorianCalendar();
//		String formattedDate = formatter.format(timestamp.getTime());
		SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar cal = dfm.getCalendar();
		cal.setTime(cal.getTime());
		Date now = dfm.parse(dfm.format(cal.getTime()));
		//System.out.println(dfm.format(cal.getTime()));
		System.out.println(dfm.format(now));
		
		Date to = new Date(now.getTime());		
		//cal.add(Calendar.HOUR, -168);
		cal.add(Calendar.HOUR, -7200);
		//Date from = new Date(now.getTime());
		Date from = dfm.parse(dfm.format(cal.getTime()));
		System.out.println(dfm.format(from));
		
		Date oneHourBack = cal.getTime();
		//System.out.println(dfm.format(oneHourBack).toString());
		oneHourBack = dfm.parse(dfm.format(oneHourBack).toString());

		//Date to = new Date(1342527341);
		//Date from = new Date(1342527233);
		
		//System.out.println(to.toString());
		//System.out.println(to.getTime());
		//System.out.println(now.getTime());
		
		MonitoringClient mc = new MonitoringClient();
		//List<MonitoringResourceDataset> lmcds = mc.getMonitoringServiceInfo("403fbb4e-5a22-4604-a7c9-24f9a5538230", from, now);
		List<MonitoringResourceDataset> lmcds = mc.getMonitoringVirtualInfo("403fbb4e-5a22-4604-a7c9-24f9a5538230", from, now);
		System.out.println(lmcds.size());
		for (MonitoringResourceDataset mcd : lmcds) 
		{
			System.out.println(dfm.format(mcd.getMetric_timestamp()) + " - " + mcd.getVirtual_resource_id());
		}
	}
	
	
/*	
	public void testAllCalculation ()
	{
		PropertyConfigurator.configure(PropertiesUtils.getLogConfig());
		
		ArrayList<ServiceInfo> lstSI = new ArrayList<ServiceInfo>();
		log.info("Getting IP monitoring info");
		lstSI = getActiveServices();
		log.debug("Active Services "+lstSI.size());
		ArrayList<String> activeServices = getActiveServiceIds(lstSI);
		
		// Calculate trust parameters for every active service
		try
		{
			TrustIPOrchestrator tipo = new TrustIPOrchestrator(activeServices);
			tipo.calculateIP2SPParams();			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	public void testAllHMCalculation ()
	{
		ArrayList<ServiceInfo> lstSI = new ArrayList<ServiceInfo>();
		System.out.println("Getting IP monitoring info");
		lstSI = getActiveServices();
		System.out.println("Active Services "+lstSI.size());
		ArrayList<String> activeServices = getActiveServiceIds(lstSI);
				
		// Calculate forecast for HM for each service
		try
		{
			TrustHMOrchestrator thmo = new TrustHMOrchestrator(activeServices);
			thmo.calculateSP2IPparams();		
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}		
		
	}
	
	
	private ArrayList<ServiceInfo> getActiveServices() {
		TrecServiceInfoDAO tsidao = new TrecServiceInfoDAO();
		try {
			return (ArrayList<ServiceInfo>) tsidao.getActiveServices();
		} catch (Exception e) {
			System.out.println("Error getting active services");
			e.printStackTrace();
			return null;
		}
	}
	
	private ArrayList<String> getActiveServiceIds(
			ArrayList<ServiceInfo> ServiceList) {
		ArrayList<String> sidList = new ArrayList<String>();
		// TODO call to the DB
		try {
			for (ServiceInfo si : ServiceList) {
				sidList.add(si.getServiceId());
			}
			return sidList;
		} catch (Exception e) {
			
			return sidList;
		}
	}
	
	/*
	public void testSPCalculation()
	{		
		IP2SPFinalTrustCalculator spftc = new IP2SPFinalTrustCalculator();
		try 
		{
			spftc.calculateIPTrust("atos");				
			List<Double> trustResult = spftc.getServiceTrust("TSTtests6");
			System.out.println (trustResult.size());
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
		
	/*
	public void testDeployment()
	{		
		CommonIPAPI tfApi = new CommonIPAPI();
		String manifest = "";
		try
		{
			manifest = readFile ("D:/opt/Jorge.xml");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		System.out.println (manifest);
		
		System.out.println ("Result: " + tfApi.ServiceDeployed(manifest));
	}
	*/
	
	private static String readFile(String path) throws IOException 
	{
		FileInputStream stream = new FileInputStream(new File(path));
		try {
		  FileChannel fc = stream.getChannel();
		  MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		  /* Instead of using default, pass in a decoder. */
		  return Charset.defaultCharset().decode(bb).toString();
		}
		finally 
		{
			stream.close();
		}
	}
	
	/*
	public void testThread ()
	{
		TrustTimerIP ttip = TrustTimerIP.instance();
		ttip.subscribeAlert((double) 0.5, "TSTtests2", TrustTimerIP.SERVICE);	
		try
		{
			synchronized (this) {
			    this.wait(63000);
			}		
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		ttip.subscribeAlert((double) 4.5, "TSTtests5", TrustTimerIP.SERVICE);
		try
		{
			synchronized (this) {
			    this.wait(63000);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		ttip.unSubscribeAlert("TSTtests5", TrustTimerIP.SERVICE);
		ttip.unSubscribeAlert("TSTtests2", TrustTimerIP.SERVICE);
	}
	*/
}

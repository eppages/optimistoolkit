package eu.optimis.cbr.rest.utils;

/**
 * @author Pramod Pawar
 */
import eu.optimis.cbr.monitoring.clientlib.Actions;
import eu.optimis.cbr.monitoring.clientlib.BrokerVisualMonitor;
import eu.optimis.cbr.monitoring.clientlib.EndPointType;
import eu.optimis.cbr.monitoring.clientlib.IllegalCallParameter;
import eu.optimis.cbr.monitoring.clientlib.StatusCode;


public class IPRegistry {

	private static IPRegistry ipreg;
	private IPInfoList ipList;

	
	
	private IPRegistry(){
		this.ipList = new IPInfoList();
	}
	
	public static IPRegistry getSingletonObject(){
		
		if(ipreg ==null){
			ipreg = new IPRegistry();
		}

		return ipreg;
	}
	
	public IPRegistry(IPInfoList iplst){
		this.ipList = iplst;
	}
	
	public void setIPInfoList(IPInfoList iplst){
		this.ipList = iplst;
	}
	
	public IPInfoList getIPInfoList(){
		return this.ipList;
	}
	
}

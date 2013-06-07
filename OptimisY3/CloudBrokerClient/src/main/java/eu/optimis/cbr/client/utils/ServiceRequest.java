package eu.optimis.cbr.client.utils;

//import eu.optimis.do.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



/**
 * @author Pramod Pawar
 */

@XmlRootElement
public class ServiceRequest {
//	@XmlElement
	private String manifestAsString;
//	@XmlElement
	private String objective;
//	private Manifest manifest;
//	private Objective objective;
	
	
//	public ServiceRequest(Manifest manifest, Objective objective)
	
	public ServiceRequest(){
		
	}
	

	public ServiceRequest(String manifest, String objv)
	{
		//super();
		this.manifestAsString = manifest;
		this.objective = objv;
	}
	
	public String getManifest()
	{
		return this.manifestAsString;
	}

	public void setManifest(String manifest)
	{
		this.manifestAsString = manifest;
	}

	public String getObjective()
	{
		return this.objective;
	}
	public void setObjective(String objv)
	{
		this.objective = objv;
	}
	
}


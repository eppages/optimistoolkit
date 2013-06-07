package eu.optimis.cbr.rest.utils;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Pramod Pawar
 */

@XmlRootElement
public class IPInfo {

	private String UUID;
	private String ipName;
	private String ipAddress;
	private String ipType;
	
	public IPInfo(){
		
	}
	
	public IPInfo(String UUID, String ipName, String ipAddress, String ipType){
		this.UUID=UUID;
		this.ipName=ipName;
		this.ipAddress=ipAddress;
		this.ipType=ipType;
	}
	
	public String getUUID(){
		return this.UUID;
	}
	
	public void setUUID(String uuid){
		this.UUID=uuid;
	}
	
	public String getIPName(){
		return this.ipName;
	}
	
	public void setIPName(String ipname){
		this.ipName=ipname;
	}
	
	public String getIPAddress(){
		return this.ipAddress;
	}
	
	public void setIPAddress(String ipaddress){
		this.ipAddress=ipaddress;
	}
	
	public String getIPType(){
		return this.ipType;
	}
	
	public void setIPType(String iptype){
		this.ipType=iptype;
	}
	
}

package eu.optimis.cbr.client.utils;


/**
 * @author Pramod Pawar
 */


import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.optimis.cbr.client.utils.IPInfo;

import eu.optimis.ipdiscovery.datamodel.Provider;

@XmlRootElement
public class IPInfoList {
	@XmlElement
//	private ArrayList<IPInfo> ipinfo;
	private ArrayList<Provider> ipinfo;
	
	public  IPInfoList(){
		//this.ipinfo = new ArrayList<IPInfo>();
		this.ipinfo = new ArrayList<Provider>();
	}
	
//	public void setIPList(ArrayList<IPInfo> ipinfolist){
	public void setIPList(ArrayList<Provider> ipinfolist){
		this.ipinfo = ipinfolist;
	}
	
//	public ArrayList<IPInfo> getIPList(){
	public ArrayList<Provider> getIPList(){
		return this.ipinfo;
	}
	
}

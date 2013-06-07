/*
 * This file is part of the Venus-C project, released under GNU LGPL v3
 * (see LICENSE.txt file).
 *
 * Copyright Engineering Ingegneria Informatica S.p.a. 2011
 */
package net.emotivecloud.utils.oca;

/**
 * Class <code>OCANicWrapper</code> wrapper for part of the XML output
 * of the OCA info() method.
 *
 * @author <a href="mailto:saint@eng.it">Gian Uberto Lauri</a>
 * @version $Revision$
 */
public class OCANicWrapper {

	private String bridge;

	private String ip;

	private String mac;

	private String network;

	private String networkId;

	/**
	 * Get the <code>Bridge</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getBridge() {
		return bridge;
	}

	/**
	 * Set the <code>Bridge</code> value.
	 *
	 * @param newBridge The new Bridge value.
	 */
	public final void setBridge(final String newBridge) {
		this.bridge = newBridge;
	}

	/**
	 * Get the <code>Ip</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getIp() {
		return ip;
	}

	/**
	 * Set the <code>Ip</code> value.
	 *
	 * @param newIp The new Ip value.
	 */
	public final void setIp(final String newIp) {
		this.ip = newIp;
	}

	/**
	 * Get the <code>Mac</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getMac() {
		return mac;
	}

	/**
	 * Set the <code>Mac</code> value.
	 *
	 * @param newMac The new Mac value.
	 */
	public final void setMac(final String newMac) {
		this.mac = newMac;
	}

	/**
	 * Get the <code>Network</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getNetwork() {
		return network;
	}

	/**
	 * Set the <code>Network</code> value.
	 *
	 * @param newNetwork The new Network value.
	 */
	public final void setNetwork(final String newNetwork) {
		this.network = newNetwork;
	}

	/**
	 * Get the <code>NetworkId</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getNetworkId() {
		return networkId;
	}

	/**
	 * Set the <code>NetworkId</code> value.
	 *
	 * @param newNetworkId The new NetworkId value.
	 */
	public final void setNetworkId(final String newNetworkId) {
		this.networkId = newNetworkId;
	}

	/*====================================================================================
	 * Maria 08 giugno 2011
	 * variables definitions
	 * from xsd file
	 */
	private Integer Id;
	
	private Integer Uid;
	
	private String Name;

	private Integer Type;

	private Integer Public;
	
	private OCATemplateWrapper template;

	private OCALeaseWrapper leases;

	/*====================================================================================
	 * Maria 08 giugno 2011
	 * methos definition for Id and Uid network
	 */
	/**
	 * Get the <code>Uid</code> value.
	 *
	 * @return a <code>Integer</code> 
	 */
	public final Integer getUid() {
		return Uid;
	}

	/**
	 * Set the <code>Uid</code> value.
	 *
	 * @param Uid The new Uid value.
	 */
	public final void setUid(final Integer newUid) {
		this.Uid = newUid;
	}
	/**
	 * Get the <code>Id</code> value.
	 *
	 * @return a <code>Integer</code> 
	 */
	public final Integer getId() {
		return Id;
	}

	/**
	 * Set the <code>Id</code> value.
	 *
	 * @param Id The new Id value.
	 */
	public final void setId(final Integer newId) {
		this.Id = newId;
	}
	/**
	 * Get the <code>Name</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getName() {
		return Name;
	}

	/**
	 * Set the <code>Name</code> value.
	 *
	 * @param Name The new Name value.
	 */
	public final void setName(final String newName) {
		this.Name = newName;
	}

	/**
	 * Get the <code>Type</code> value.
	 *
	 * @return a <code>Integer</code> 
	 */
	public final Integer getType() {
		return Type;
	}

	/**
	 * Set the <code>Type</code> value.
	 *
	 * @param  The new Type value.
	 */
	public final void setType(final Integer newType) {
		this.Type = newType;
	}

	/**
	 * Get the <code>Public</code> value.
	 *
	 * @return a <code>Integer</code> 
	 */
	public final Integer getPublic() {
		return Public;
	}

	/**
	 * Set the <code>Public</code> value.
	 *
	 * @param  The new Public value.
	 */
	public final void setPublic(final Integer newPublic) {
		this.Public = newPublic;
	}

	/**
	 * Get the <code>Template</code> value.
	 *
	 * @return a <code>OCATemplateWrapper</code> 
	 */
	public final OCATemplateWrapper getTemplate() {
		return template;
	}

	/**
	 * Set the <code>Template</code> value.
	 *
	 * @param newTemplate The new Template value.
	 */
	public final void setTemplate(final OCATemplateWrapper newTemplate) {
		this.template = newTemplate;
	}

	/**
	 * Get the <code>Leases</code> value.
	 *
	 * @return a <code>OCALeaseWrapper</code> 
	 */
	public final OCALeaseWrapper getLeases() {
		return leases;
	}

	/**
	 * Set the <code>Leases</code> value.
	 *
	 * @param newLeases The new Leases value.
	 */
	public final void setLeases(final OCALeaseWrapper newLeases) {
		this.leases = newLeases;
	}
}

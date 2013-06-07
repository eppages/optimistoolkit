/*
 * This file is part of the Venus-C project, released under GNU LGPL v3
 * (see LICENSE.txt file).
 *
 * Copyright Engineering Ingegneria Informatica S.p.a. 2011
 */
package net.emotivecloud.utils.oca;

import java.util.Map;

/**
 * OCATemplateWrapper: a wrapper for OCA replies. Is an object
 * obtained by the "deserialization" of the XML returned by OCA API
 * methods.
 *
 *
 * Created: Thu May 19 12:31:32 2011
 *
 * @author <a href="mailto:saint@eng.it">Gian Uberto Lauri</a>
 * @version $Revision$
 */
public class OCATemplateWrapper {

	private int cpu;

	private int memory;

	private String name;

	private OCAOsWrapper os;

	private int vcpu;

	private String vmId;

	private Object context;

	private Map<String, OCANicWrapper> nics;

	private Map<String, OCADiskWrapper> disks;

	/**
	 * Creates a new instance of <code>OCATemplateWrapper</code> .
	 *
	 */
	public OCATemplateWrapper() {

	}

	/**
	 * Maria june 2011
	 */
	private int        bridge;
	private String     networkAddress;
	private String     networkSize;
	private int        type;
	
	/**
	 * End Modified
	 * Maria june 2011
	 */
	
	/**
	 * Get the <code>Cpu</code> value.
	 *
	 * @return an <code>int</code> 
	 */
	public final int getCpu() {
		return cpu;
	}

	/**
	 * Set the <code>Cpu</code> value.
	 *
	 * @param newCpu The new Cpu value.
	 */
	public final void setCpu(int newCpu) {
		this.cpu = newCpu;
	}

	/**
	 * Get the <code>OCADiskWrapper></code> value.
	 *
	 * @return a <code>Map<String</code> 
	 */
	public final Map<String, OCADiskWrapper> getDisks() {
		return disks;
	}

	/**
	 * Set the <code>OCADiskWrapper></code> value.
	 *
	 * @param newOCADiskWrapper> The new OCADiskWrapper> value.
	 */
	public final void setDisks(final Map<String, OCADiskWrapper> newDisks) {
		this.disks = newDisks;
	}

	/**
	 * Get the <code>Memory</code> value.
	 *
	 * @return an <code>int</code> 
	 */
	public final int getMemory() {
		return memory;
	}

	/**
	 * Set the <code>Memory</code> value.
	 *
	 * @param newMemory The new Memory value.
	 */
	public final void setMemory(final int newMemory) {
		this.memory = newMemory;
	}

	/**
	 * Get the <code>Name</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Set the <code>Name</code> value.
	 *
	 * @param newName The new Name value.
	 */
	public final void setName(final String newName) {
		this.name = newName;
	}

	/**
	 * Get the <code>OCANicWrapper></code> value.
	 *
	 * @return a <code>Map<String</code> 
	 */
	public final Map<String, OCANicWrapper> getNics() {
		return nics;
	}

	/**
	 * Set the <code>OCANicWrapper></code> value.
	 *
	 * @param newOCANicWrapper> The new OCANicWrapper> value.
	 */
	public final void setNics(final Map<String, OCANicWrapper> newNics) {
		this.nics = newNics;
	}

	/**
	 * Get the <code>Os</code> value.
	 *
	 * @return an <code>OCAOsWrapper</code> 
	 */
	public final OCAOsWrapper getOs() {
		return os;
	}

	/**
	 * Set the <code>Os</code> value.
	 *
	 * @param newOs The new Os value.
	 */
	public final void setOs(final OCAOsWrapper newOs) {
		this.os = newOs;
	}

	/**
	 * Get the <code>Vcpu</code> value.
	 *
	 * @return an <code>int</code> 
	 */
	public final int getVcpu() {
		return vcpu;
	}

	/**
	 * Set the <code>Vcpu</code> value.
	 *
	 * @param newVcpu The new Vcpu value.
	 */
	public final void setVcpu(final int newVcpu) {
		this.vcpu = newVcpu;
	}

	/**
	 * Get the <code>VmId</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getVmId() {
		return vmId;
	}

	/**
	 * Set the <code>VmId</code> value.
	 *
	 * @param newVmId The new VmId value.
	 */
	public final void setVmId(final String newVmId) {
		this.vmId = newVmId;
	}

	/**
	 * Get the <code>Context</code> value.
	 *
	 * @return an <code>Object</code> 
	 */
	public final Object getContext() {
		return context;
	}

	/**
	 * Set the <code>Context</code> value.
	 *
	 * @param newContext The new Context value.
	 */
	public final void setContext(final Object newContext) {
		this.context = newContext;
	}

	// 	 Maria june 2011
    
	/**
	 * get the <code>Bridge,networkAddress,networkSize,Type</code>
	 * value.
	 *
	 * @return an <code>int</code> the value of the bridge attribute
	 */
	public final int getBridge(){
		return bridge;
	}
	
	/**
	 * <code>setBridge</code> sets the new value for bridge
	 *
	 * @param newBridge an <code>int</code> the new value for bridge
	 */
	public final void setBridge(final int newBridge){
		this.bridge = newBridge;
	}
	
	/**
	 * <code>getNetworkAddress</code> gets the new value of networkAddress
	 *
	 * @return a <code>String</code> the value of networkAddress
	 */
	public final String getNetworkAddress(){
		return networkAddress;
	}

	/**
	 * <code>setNetworkAddress</code> sets the new value for networkAddress
	 *
	 * @param newAddress a <code>String</code> the new value for
	 * networkAddress
	 */
	public final void setNetworkAddress(final String newAddress){
		this.networkAddress = newAddress;
	}
	
	/**
	 * <code>getNetworkSize</code> get the networkSize attribute value
	 *
	 * @return a <code>String</code> the networkSize attribute value
	 */
	public final String getNetworkSize(){
		return networkSize;
	}

	/**
	 * <code>setNetworkSize</code> set the new value for the attribuge
	 * networkSize
	 *
	 * @param newSize a <code>String</code> the new value for the attribuge
	 * networkSize
	 */
	public final void setNetworkSize(final String newSize){
		this.networkSize = newSize;
	}
	
	/**
	 * <code>getType</code> get the type attribute value
	 *
	 * @return an <code>int</code> the type attribute value
	 */
	public final int getType(){
		return type;
	}
	
	/**
	 * <code>setType</code> set the new value for the attribuge type
	 *
	 * @param newType an <code>int</code> the new value for the attribuge
	 * type
	 */
	public final void setType(final int newType){
		this.type = newType;
	}
	

}

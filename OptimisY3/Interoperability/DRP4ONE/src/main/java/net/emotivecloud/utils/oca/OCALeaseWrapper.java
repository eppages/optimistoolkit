/*
 * This file is part of the Venus-C project, released under GNU LGPL v3
 * (see LICENSE.txt file).
 *
 * Copyright Engineering Ingegneria Informatica S.p.a. 2011
 */
package net.emotivecloud.utils.oca;

/**
 * Class  <code>OCALeaseWrapper</code>  wrapper  for part  of  the  XML
 * returned by OCA info() method.
 *
 * @author <a href="mailto:madigiro@eng.it">MariadGirolamo</a>
 * @version $Revision$
 */
public class OCALeaseWrapper {

	private String ip;

	private String mac;

	private int used;

	private int vid;

	/**
	 * Get the <code>ip</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getIp() {
		return ip;
	}

	/**
	 * Set the <code>ip</code> value.
	 *
	 * @param newIp The new ip value.
	 */
	public final void setIp(final String newIp) {
		this.ip = newIp;
	}

	/**
	 * Get the <code>mac</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getMac() {
		return mac;
	}

	public final void setMac(final String newMac) {
		this.ip = newMac;
	}
	
	/**
	 * Set the <code>used</code> value.
	 *
	 * @param newused The new used value.
	 */
	public final int getUsed() {
		return used;
	}
	
	public final void setUsed(final int newUsed) {
		this.used = newUsed;
	}

	/**
	 * Get the <code>Vid</code> value.
	 *
	 * @return a <code>int</code> 
	 */
	public final int getVid() {
		return vid;
	}
    
	public final void setVid(final int newvid) {
		this.vid = newvid;
	}
	
}

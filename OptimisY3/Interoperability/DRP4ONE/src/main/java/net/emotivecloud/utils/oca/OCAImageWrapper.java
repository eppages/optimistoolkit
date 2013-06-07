/*
 * This file is part of the Venus-C project, released under GNU LGPL v3
 * (see LICENSE.txt file).
 *
 * Copyright Engineering Ingegneria Informatica S.p.a. 2011
 */
package net.emotivecloud.utils.oca;

/**
 * Class  <code>OCAImageWrapper</code>  wrapper  for part  of  the  XML
 * returned by OCA info() method.
 *
 * @author <a href="mailto:madigiro@mail.eng.it">MariadGiro</a>
 * @version $Revision$
 */
public class OCAImageWrapper {

	private int 	id;
    private int 	uid;
    private String 	username;
    private String 	name;
    private int 	type;
    private int 	_public;
    private int 	persistent;
    private int 	regtime;
    private String 	source;
    private int  	state;
    private int		runningVms;
    //Object template;

	/**
	 * Get the <code>id image</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final int getId() {
		return id;
	}

	/**
	 * Set the <code>id</code> value.
	 *
	 * @param newId The new Id value.
	 */
	public final void setId(final int newId) {
		this.id = newId;
	}

	/**
	 * Get the <code>uid</code> value.
	 *
	 * @return a <code>int</code> 
	 */
	public final int getUid() {
		return uid;
	}

	/**
	 * Set the <code>uid</code> value.
	 *
	 * @param newUid The new uid value.
	 */
	public final void setUid(final int newUid) {
		this.uid = newUid;
	}

	/**
	 * Get the <code>username</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getUsername() {
		return username;
	}

	/**
	 * Set the <code>username</code> value.
	 *
	 * @param newUsername The new username value.
	 */
	public final void setUsername(final String newUsername) {
		this.username = newUsername;
	}

	/**
	 * Get the <code>name</code> value.
	 *
	 * @return an <code>string</code> 
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Set the <code>nanme</code> value.
	 *
	 * @param newName The new name value.
	 */
	public final void setName(final String newName) {
		this.name = newName;
	}

	/**
	 * Get the <code>type image</code> value.
	 *
	 * @return a <code>Int</code> 
	 */
	public final int getType() {
		return type;
	}

	/**
	 * Set the <code>type</code> value.
	 *
	 * @param newType The new Type value.
	 */
	public final void setType(final int newType) {
		this.type = newType;
	}

	/**
	 * Get the <code>_public image</code> value.
	 *
	 * @return a <code>Int</code> 
	 */
	public final int getPublic() {
		return _public;
	}

	/**
	 * Set the <code>_public</code> value.
	 *
	 * @param newPublic The new Public value.
	 */
	public final void setPublic(final int newPublic) {
		this._public = newPublic;
	}

	/**
	 * Get the <code>persistent image</code> value.
	 *
	 * @return a <code>Int</code> 
	 */
	public final int getPersistent() {
		return persistent;
	}

	/**
	 * Set the <code>persistent</code> value.
	 *
	 * @param newPersistent The new Persistent value.
	 */
	public final void setPersistent(final int newPersistent) {
		this.persistent = newPersistent;
	}

	/**
	 * Get the <code>regtime image</code> value.
	 *
	 * @return a <code>Int</code> 
	 */
	public final int getRegTime() {
		return regtime;
	}

	/**
	 * Set the <code>regtime</code> value.
	 *
	 * @param newRegtime The new Regtime value.
	 */
	public final void setRegtime(final int newRegtime) {
		this.regtime = newRegtime;
	}

	/**
	 * Set the <code>source</code> value.
	 *
	 * @param newSource The new source value.
	 */
	public final void setSource(final String newSource) {
		this.source = newSource;
	}

	/**
	 * Get the <code>source image</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getSource() {
		return source;
	}

	/**
	 * Get the <code>state image</code> value.
	 *
	 * @return a <code>Int</code> 
	 */
	public final int getState() {
		return state;
	}

	/**
	 * Set the <code>state</code> value.
	 *
	 * @param newState The new State value.
	 */
	public final void setState(final int newState) {
		this.state = newState;
	}

	/**
	 * Get the <code>runningVms image</code> value.
	 *
	 * @return a <code>Int</code> 
	 */
	public final int getRunningVms() {
		return runningVms;
	}

	/**
	 * Set the <code>state</code> value.
	 *
	 * @param newState The new State value.
	 */
	public final void setRunningVms(final int newRunningVms) {
		this.runningVms = newRunningVms;
	}
}

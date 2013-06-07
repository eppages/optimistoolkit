/*
 * This file is part of the Venus-C project, released under GNU LGPL v3
 * (see LICENSE.txt file).
 *
 * Copyright Engineering Ingegneria Informatica S.p.a. 2011
 */
package net.emotivecloud.utils.oca;

/**
 * Class <code>OCAOsWrapper</code> wrapper for part of the XML output
 * of the OCA info() method.
 *
 * @author <a href="mailto:saint@eng.it">Gian Uberto Lauri</a>
 * @version $Revision$
 */
public class OCAOsWrapper {

	private String arch;

	private String boot;

	private String bootloader;

	private String root;

	private String kernel;

	private String kernelCmd;

	private String initrd;

	/**
	 * Get the <code>Arch</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getArch() {
		return arch;
	}

	/**
	 * Set the <code>Arch</code> value.
	 *
	 * @param newArch The new Arch value.
	 */
	public final void setArch(final String newArch) {
		this.arch = newArch;
	}

	/**
	 * Get the <code>Boot</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getBoot() {
		return boot;
	}

	/**
	 * Set the <code>Boot</code> value.
	 *
	 * @param newBoot The new Boot value.
	 */
	public final void setBoot(final String newBoot) {
		this.boot = newBoot;
	}

	/**
	 * Get the <code>Bootloader</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getBootloader() {
		return bootloader;
	}

	/**
	 * Set the <code>Bootloader</code> value.
	 *
	 * @param newBootloader The new Bootloader value.
	 */
	public final void setBootloader(final String newBootloader) {
		this.bootloader = newBootloader;
	}

	/**
	 * Get the <code>Root</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getRoot() {
		return root;
	}

	/**
	 * Set the <code>Root</code> value.
	 *
	 * @param newRoot The new Root value.
	 */
	public final void setRoot(final String newRoot) {
		this.root = newRoot;
	}

	/**
	 * Get the <code>Kernel</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getKernel() {
		return kernel;
	}

	/**
	 * Set the <code>Kernel</code> value.
	 *
	 * @param newKernel The new Kernel value.
	 */
	public final void setKernel(final String newKernel) {
		this.kernel = newKernel;
	}

	/**
	 * Get the <code>KernelCmd</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getKernelCmd() {
		return kernelCmd;
	}

	/**
	 * Set the <code>KernelCmd</code> value.
	 *
	 * @param newKernelCmd The new KernelCmd value.
	 */
	public final void setKernelCmd(final String newKernelCmd) {
		this.kernelCmd = newKernelCmd;
	}

	/**
	 * Get the <code>Initrd</code> value.
	 *
	 * @return a <code>String</code> 
	 */
	public final String getInitrd() {
		return initrd;
	}

	/**
	 * Set the <code>Initrd</code> value.
	 *
	 * @param newInitrd The new Initrd value.
	 */
	public final void setInitrd(final String newInitrd) {
		this.initrd = newInitrd;
	}

}

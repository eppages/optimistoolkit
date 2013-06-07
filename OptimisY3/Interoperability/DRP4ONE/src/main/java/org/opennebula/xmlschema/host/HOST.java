/*
 * This file is part of the Venus-C project, released under GNU LGPL v3
 * (see LICENSE.txt file).
 *
 * Copyright Engineering Ingegneria Informatica S.p.a. 2011
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.24 at 02:43:59 PM CEST 
//


package org.opennebula.xmlschema.host;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="NAME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="STATE" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="IM_MAD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VM_MAD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TM_MAD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LAST_MON_TIME" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="CLUSTER" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HOST_SHARE">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="HID" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="DISK_USAGE" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="MEM_USAGE" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="CPU_USAGE" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="MAX_DISK" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="MAX_MEM" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="MAX_CPU" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="FREE_DISK" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="FREE_MEM" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="FREE_CPU" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="USED_DISK" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="USED_MEM" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="USED_CPU" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="RUNNING_VMS" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TEMPLATE" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "name",
    "state",
    "immad",
    "vmmad",
    "tmmad",
    "lastmontime",
    "cluster",
    "hostshare",
    "template"
})
@XmlRootElement(name = "HOST")
public class HOST {

    @XmlElement(name = "ID", required = true)
    protected BigInteger id;
    @XmlElement(name = "NAME", required = true)
    protected String name;
    @XmlElement(name = "STATE", required = true)
    protected BigInteger state;
    @XmlElement(name = "IM_MAD", required = true)
    protected String immad;
    @XmlElement(name = "VM_MAD", required = true)
    protected String vmmad;
    @XmlElement(name = "TM_MAD", required = true)
    protected String tmmad;
    @XmlElement(name = "LAST_MON_TIME", required = true)
    protected BigInteger lastmontime;
    @XmlElement(name = "CLUSTER", required = true)
    protected String cluster;
    @XmlElement(name = "HOST_SHARE", required = true)
    protected HOST.HOSTSHARE hostshare;
    @XmlElement(name = "TEMPLATE", required = true)
    protected Object template;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setID(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNAME() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNAME(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSTATE() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSTATE(BigInteger value) {
        this.state = value;
    }

    /**
     * Gets the value of the immad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMMAD() {
        return immad;
    }

    /**
     * Sets the value of the immad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMMAD(String value) {
        this.immad = value;
    }

    /**
     * Gets the value of the vmmad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVMMAD() {
        return vmmad;
    }

    /**
     * Sets the value of the vmmad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVMMAD(String value) {
        this.vmmad = value;
    }

    /**
     * Gets the value of the tmmad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTMMAD() {
        return tmmad;
    }

    /**
     * Sets the value of the tmmad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTMMAD(String value) {
        this.tmmad = value;
    }

    /**
     * Gets the value of the lastmontime property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLASTMONTIME() {
        return lastmontime;
    }

    /**
     * Sets the value of the lastmontime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLASTMONTIME(BigInteger value) {
        this.lastmontime = value;
    }

    /**
     * Gets the value of the cluster property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCLUSTER() {
        return cluster;
    }

    /**
     * Sets the value of the cluster property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCLUSTER(String value) {
        this.cluster = value;
    }

    /**
     * Gets the value of the hostshare property.
     * 
     * @return
     *     possible object is
     *     {@link HOST.HOSTSHARE }
     *     
     */
    public HOST.HOSTSHARE getHOSTSHARE() {
        return hostshare;
    }

    /**
     * Sets the value of the hostshare property.
     * 
     * @param value
     *     allowed object is
     *     {@link HOST.HOSTSHARE }
     *     
     */
    public void setHOSTSHARE(HOST.HOSTSHARE value) {
        this.hostshare = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getTEMPLATE() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setTEMPLATE(Object value) {
        this.template = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="HID" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="DISK_USAGE" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="MEM_USAGE" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="CPU_USAGE" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="MAX_DISK" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="MAX_MEM" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="MAX_CPU" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="FREE_DISK" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="FREE_MEM" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="FREE_CPU" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="USED_DISK" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="USED_MEM" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="USED_CPU" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="RUNNING_VMS" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "hid",
        "diskusage",
        "memusage",
        "cpuusage",
        "maxdisk",
        "maxmem",
        "maxcpu",
        "freedisk",
        "freemem",
        "freecpu",
        "useddisk",
        "usedmem",
        "usedcpu",
        "runningvms"
    })
    public static class HOSTSHARE {

        @XmlElement(name = "HID", required = true)
        protected BigInteger hid;
        @XmlElement(name = "DISK_USAGE", required = true)
        protected BigInteger diskusage;
        @XmlElement(name = "MEM_USAGE", required = true)
        protected BigInteger memusage;
        @XmlElement(name = "CPU_USAGE", required = true)
        protected BigInteger cpuusage;
        @XmlElement(name = "MAX_DISK", required = true)
        protected BigInteger maxdisk;
        @XmlElement(name = "MAX_MEM", required = true)
        protected BigInteger maxmem;
        @XmlElement(name = "MAX_CPU", required = true)
        protected BigInteger maxcpu;
        @XmlElement(name = "FREE_DISK", required = true)
        protected BigInteger freedisk;
        @XmlElement(name = "FREE_MEM", required = true)
        protected BigInteger freemem;
        @XmlElement(name = "FREE_CPU", required = true)
        protected BigInteger freecpu;
        @XmlElement(name = "USED_DISK", required = true)
        protected BigInteger useddisk;
        @XmlElement(name = "USED_MEM", required = true)
        protected BigInteger usedmem;
        @XmlElement(name = "USED_CPU", required = true)
        protected BigInteger usedcpu;
        @XmlElement(name = "RUNNING_VMS", required = true)
        protected BigInteger runningvms;

        /**
         * Gets the value of the hid property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getHID() {
            return hid;
        }

        /**
         * Sets the value of the hid property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setHID(BigInteger value) {
            this.hid = value;
        }

        /**
         * Gets the value of the diskusage property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getDISKUSAGE() {
            return diskusage;
        }

        /**
         * Sets the value of the diskusage property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setDISKUSAGE(BigInteger value) {
            this.diskusage = value;
        }

        /**
         * Gets the value of the memusage property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getMEMUSAGE() {
            return memusage;
        }

        /**
         * Sets the value of the memusage property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setMEMUSAGE(BigInteger value) {
            this.memusage = value;
        }

        /**
         * Gets the value of the cpuusage property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getCPUUSAGE() {
            return cpuusage;
        }

        /**
         * Sets the value of the cpuusage property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setCPUUSAGE(BigInteger value) {
            this.cpuusage = value;
        }

        /**
         * Gets the value of the maxdisk property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getMAXDISK() {
            return maxdisk;
        }

        /**
         * Sets the value of the maxdisk property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setMAXDISK(BigInteger value) {
            this.maxdisk = value;
        }

        /**
         * Gets the value of the maxmem property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getMAXMEM() {
            return maxmem;
        }

        /**
         * Sets the value of the maxmem property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setMAXMEM(BigInteger value) {
            this.maxmem = value;
        }

        /**
         * Gets the value of the maxcpu property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getMAXCPU() {
            return maxcpu;
        }

        /**
         * Sets the value of the maxcpu property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setMAXCPU(BigInteger value) {
            this.maxcpu = value;
        }

        /**
         * Gets the value of the freedisk property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getFREEDISK() {
            return freedisk;
        }

        /**
         * Sets the value of the freedisk property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setFREEDISK(BigInteger value) {
            this.freedisk = value;
        }

        /**
         * Gets the value of the freemem property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getFREEMEM() {
            return freemem;
        }

        /**
         * Sets the value of the freemem property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setFREEMEM(BigInteger value) {
            this.freemem = value;
        }

        /**
         * Gets the value of the freecpu property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getFREECPU() {
            return freecpu;
        }

        /**
         * Sets the value of the freecpu property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setFREECPU(BigInteger value) {
            this.freecpu = value;
        }

        /**
         * Gets the value of the useddisk property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getUSEDDISK() {
            return useddisk;
        }

        /**
         * Sets the value of the useddisk property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setUSEDDISK(BigInteger value) {
            this.useddisk = value;
        }

        /**
         * Gets the value of the usedmem property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getUSEDMEM() {
            return usedmem;
        }

        /**
         * Sets the value of the usedmem property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setUSEDMEM(BigInteger value) {
            this.usedmem = value;
        }

        /**
         * Gets the value of the usedcpu property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getUSEDCPU() {
            return usedcpu;
        }

        /**
         * Sets the value of the usedcpu property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setUSEDCPU(BigInteger value) {
            this.usedcpu = value;
        }

        /**
         * Gets the value of the runningvms property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getRUNNINGVMS() {
            return runningvms;
        }

        /**
         * Sets the value of the runningvms property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setRUNNINGVMS(BigInteger value) {
            this.runningvms = value;
        }

    }

}

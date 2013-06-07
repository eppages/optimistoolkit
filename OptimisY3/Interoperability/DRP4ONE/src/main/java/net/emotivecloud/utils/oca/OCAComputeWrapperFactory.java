/*
 * This file is part of the Venus-C project, released under GNU LGPL v3
 * (see LICENSE.txt file).
 *
 * Copyright Engineering Ingegneria Informatica S.p.a. 2011
 */
package net.emotivecloud.utils.oca;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import net.emotivecloud.scheduler.drp4one.DRPOneException;
import net.emotivecloud.scheduler.drp4one.StatusCodes;

import org.opennebula.xmlschema.compute.DiskType;
import org.opennebula.xmlschema.compute.NicType;
import org.opennebula.xmlschema.compute.OsType;
import org.opennebula.xmlschema.compute.TemplateType;
import org.opennebula.xmlschema.compute.VMSIMPLE;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * Class <code>OCAComputeWrapperFactory</code> Creates an
 * OCAConputeWrapper from the OCA VirtualMachine.info() output.
 *
 * @author <a href="mailto:saint@eng.it">Gian Uberto Lauri</a>
 * @version $Revision$
 */
public class OCAComputeWrapperFactory {

    private static JAXBContext jbc = null;
    private static Unmarshaller u = null;
    private static Logger log;
    static {
        log = Logger.getLogger(OCAComputeWrapperFactory.class.getName());
        try {
            jbc = JAXBContext.newInstance(VMSIMPLE.class);
            u = jbc.createUnmarshaller();
        } catch (JAXBException e) {
            log.severe(e.getMessage());
        }
    }

	/**
	 * <code>create</code> creates an OCAComputeWrapper instance from
	 * a VMSIMPLE object passed as parameter.
	 *
	 * @param vmSimple a <code>VMSIMPLE</code> the VMSIMPLE object
	 * passed as parameter, containing the information to create the
	 * new instance.
	 * @return an <code>OCAComputeWrapper</code> the new
	 * OCAComputeWrapper instance built from the contents of the
	 * VMPOOL object passed as parameter
	 */
	public static OCAComputeWrapper create (VMSIMPLE vmSimple) {
		OCAComputeWrapper rv = new OCAComputeWrapper();

		BigInteger tmp = vmSimple.getID();
		if( tmp != null )
			rv.setId( tmp.intValue() );

		tmp = vmSimple.getUID();
		if( tmp != null )
			rv.setUid( tmp.intValue() );

		rv.setName(vmSimple.getNAME());

		tmp = vmSimple.getLASTPOLL();
		if( tmp != null )
			rv.setLastPoll( tmp.intValue() );

		tmp = vmSimple.getSTATE();
		if( tmp != null )
			rv.setState( tmp.intValue() );

		tmp = vmSimple.getLCMSTATE();
		if( tmp != null )
			rv.setLcmState( tmp.intValue() );

		tmp = vmSimple.getSTIME();
		if( tmp != null )
			rv.setStime( tmp.intValue() );

		tmp = vmSimple.getETIME();
		if( tmp != null )
			rv.setEtime( tmp.intValue() );

		rv.setDeployId(vmSimple.getDEPLOYID());

		tmp = vmSimple.getMEMORY();
		if( tmp != null )
			rv.setMemory( tmp.intValue() );

		tmp = vmSimple.getCPU();
		if( tmp != null )
			rv.setCpu( tmp.intValue() );

		tmp = vmSimple.getNETTX();
		if( tmp != null )
			rv.setNetTx( tmp.intValue() );

		tmp = vmSimple.getNETRX();
		if( tmp != null )
			rv.setNetRx( tmp.intValue() );

		tmp = vmSimple.getLASTSEQ();
		if( tmp != null )
			rv.setLastSeq( tmp.intValue() );

		rv.setTemplate( templateHelper( vmSimple.getTEMPLATE() ) );

		rv.setHistory(vmSimple.getHISTORY());

		return rv;
	}

	// Almnost syntactic shugar, to prevent methods getting as long as
	// the Trans-Siberian railway :)
	// With default access because is used by OCAComputeListWrapperFactory

	/**
	 * <code>templateHelper</code> helper method that creates an
	 * OCATemplateWrapper instance from a TemplateType object
	 *
	 * @param templateType a <code>TemplateType</code> the
	 * TemplateType initial object
	 * @return an <code>OCATemplateWrapper</code> the OCATemplateWrapper
	 * instance built from a TemplateType object
	 */
	static OCATemplateWrapper templateHelper(TemplateType templateType) {

		OCATemplateWrapper newTemplate = new OCATemplateWrapper();

		if ( templateType == null )
			// This avoid one level of indentation of a block
			// statement that shoul be too long.
			// The Patiarchs would approve.
			return newTemplate;

		// assert templateType != null 

		// the contect pass throught uhharmed...
		newTemplate.setContext(templateType.getCONTEXT());

		BigInteger tmpNum = templateType.getCPU();
		if (tmpNum != null)
			newTemplate.setCpu( tmpNum.intValue() );


		String tmp = templateType.getMEMORY();

		if (tmp != null) // It's lile in python... indentation
			try {        // matters :)

				newTemplate.setMemory(Integer.parseInt(tmp));
			}
			catch(NumberFormatException nfe) {
				throw new DRPOneException("oned replied with an invalid memory size " 
										  + templateType.getMEMORY()
										  ,StatusCodes.ONE_FAILURE);
			}



		newTemplate.setName( templateType.getNAME() );

		newTemplate.setOs( osSubHelper(templateType.getOS()) );

		newTemplate.setDisks( disksSubHelper(templateType.getDISK()) );

		newTemplate.setNics( nicsSubHelper(templateType.getNIC()) );

		return newTemplate;

	}

	// An helper of temlateHelper
	// again, to prevent method bloating
	private static OCAOsWrapper osSubHelper(OsType osType) {
		OCAOsWrapper newOs = new OCAOsWrapper();

		if(osType != null) {
			newOs.setArch(osType.getARCH());
			newOs.setBoot(osType.getBOOT());
			newOs.setBootloader(osType.getBOOTLOADER());
			newOs.setInitrd(osType.getINITRD());
			newOs.setKernel(osType.getKERNEL());
			newOs.setKernelCmd(osType.getKERNELCMD());
			newOs.setRoot(osType.getROOT());
		}

		return newOs;
	}

	// Another helper of temlateHelper
	// again, to prevent method bloating
	private static Map<String, OCADiskWrapper> disksSubHelper(Collection<DiskType> templateDisks) {

		Map<String, OCADiskWrapper> diskMap = new HashMap<String, OCADiskWrapper>();

		if (templateDisks == null)
			// The other block is too long to use the
			// block statement, again we use a quick
			// bailout.
			return diskMap;

		int counter = 0;

		for(DiskType disk: templateDisks) {
			OCADiskWrapper newDisk = new OCADiskWrapper();

			newDisk.setReadonly(disk.getREADONLY());

			String tmp = disk.getSIZE();

			// If we don't suppy OpenNebula the disk size when we create the VM, we can't get
			// this information back
			if (tmp != null)
				try {
					newDisk.setSize(Long.parseLong(tmp));
				}
				catch(NumberFormatException nfe) {
					throw new DRPOneException("oned replied with an invalid disk size " 
											  + disk.getSIZE()
											  ,StatusCodes.ONE_FAILURE);
				}

			newDisk.setSource(disk.getSOURCE());
			newDisk.setTgarget(disk.getTARGET());
			newDisk.setType(disk.getTYPE());
			String tmpId = disk.getDISKID();

			if(tmpId == null)
				tmpId = "_AutoId_4Disk__" + counter;

			counter++;

			newDisk.setDiskId(tmpId);

			diskMap.put(tmpId, newDisk);

		}

		return diskMap;
	}

	// One more helper of temlateHelper.
	// Always to prevent method bloating
	private static Map<String, OCANicWrapper> nicsSubHelper(Collection<NicType> templateNics) {
		Map<String, OCANicWrapper> nicMap = new HashMap<String, OCANicWrapper>();

		if( templateNics == null )
			// This avoid one level of indentation of a block
			// statement that shoul be too long.
			// The Patiarchs would approve!
			return nicMap;

		int nicNumber=0;

		for(NicType nic: templateNics) {
			OCANicWrapper newNic = new OCANicWrapper();

			newNic.setBridge(nic.getBRIDGE());
			newNic.setIp(nic.getIP());
			newNic.setNetwork(nic.getNETWORK());

			String tmpId = nic.getNETWORKID();

			if (tmpId == null)
				tmpId = "_AutoId_4Nic__"+nicNumber;

			nicNumber++;

			newNic.setNetworkId(tmpId);

			nicMap.put(tmpId, newNic);
			tmpId=null;
		}
		return nicMap;
	}

	/**
	 * <code>parse</code> creates an OCAComputeWrapper from the XML
	 * contained in the String parameter
	 *
	 * @param xml a <code>String</code> the String parameter containing the
	 * XML to be parsed
	 * @return an <code>OCAComputeWrapper</code> the OCAComputeWrapper
	 * built from the XML contained in the String parameter
	 * @exception JAXBException when XML parsing fails, of course.
	 * @exception SAXException when XML parsing fails, of course.
	 */
	public static OCAComputeWrapper parse(String xml) throws JAXBException, SAXException {
		return parse(new ByteArrayInputStream(xml.getBytes()));
	}

	public static OCAComputeWrapper parse(InputStream is) throws JAXBException, SAXException {
		XMLReader reader = XMLReaderFactory.createXMLReader();

		NameSpaceFilter inputFilter = new NameSpaceFilter("http://opennebula.org/XMLSchema",true);
		inputFilter.setParent(reader);

		// kudos to kristofer http://stackoverflow.com/users/259485/kristofer
		// in http://stackoverflow.com/questions/277502/jaxb-how-to-ignore-namespace-during-unmarshalling-xml-document
		VMSIMPLE tmp = (VMSIMPLE) (
			(JAXBElement<?>) u.unmarshal(
				new SAXSource(inputFilter, new InputSource(is)),VMSIMPLE.class)
			).getValue(); 
		return create( 
			tmp);

	}


}

/*
 * This file is part of the Venus-C project, released under GNU LGPL v3
 * (see LICENSE.txt file).
 *
 * Copyright Engineering Ingegneria Informatica S.p.a. 2011
 */
package net.emotivecloud.utils.oca;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.opennebula.xmlschema.compute.VMCOMPLEX;
import org.opennebula.xmlschema.compute.VMPOOL;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * class <class>OCAComputeListWrapperFactory</class> Creates an
 * OCAConputeListWrapper from the OCA VirtualMachinePool.info()
 * output.
 *
 *
 * Created: Tue May 24 16:22:22 2011
 *
 * @author <a href="mailto:saint@eng.it">Gian Uberto Lauri</a>
 * @version $Revision$
 */
public class OCAComputeListWrapperFactory {

    private static JAXBContext jbc = null;
    private static Unmarshaller u = null;
    private static Logger log;
    static {
        log = Logger.getLogger(OCAComputeListWrapperFactory.class.getName());
        try {
            jbc = JAXBContext.newInstance(VMPOOL.class);
            u = jbc.createUnmarshaller();
        } catch (JAXBException e) {
            log.severe(e.getMessage());
        }
    }

	/**
	 * <code>parseList</code> creates an OCAComputeListWrapper
	 * instance from the contents of the String passed as parameter
	 *
	 * @param xml a <code>String</code> the String containing the XML
	 * to be "deserialized"
	 * @return an <code>OCAComputeListWrapper</code> the new
	 * OCAComputeListWrapper instance build from the contents of the
	 * xml parameter
	 * @exception SAXException when XML parsing fails, of course.
	 * @exception JAXBException when XML parsing fails, of course.
	 */
	public static OCAComputeListWrapper parseList(String xml) throws SAXException, JAXBException {
		return parseList(new ByteArrayInputStream(xml.getBytes()));
	}

	/**
	 * <code>parseList</code> creates an OCAComputeListWrapper instance
	 * from the contents of the ByteArrayInputStream passed as parameter
	 *
	 * @param is a <code>ByteArrayInputStream</code> the
	 * ByteArrayInputStream containing the XML to be "deserialized"
	 * @return an <code>OCAComputeListWrapper</code> the new
	 * OCAComputeListWrapper instance build from the contents of the
	 * ByteArrayInputStream parameter
	 * @exception SAXException when XML parsing fails, of course.
	 * @exception JAXBException when XML parsing fails, of course.
	 */
	private static OCAComputeListWrapper parseList(
			ByteArrayInputStream is) throws SAXException, JAXBException {
		XMLReader reader = XMLReaderFactory.createXMLReader();

		NameSpaceFilter inputFilter = new NameSpaceFilter("http://opennebula.org/XMLSchema",true);
		inputFilter.setParent(reader);

		// kudos to kristofer http://stackoverflow.com/users/259485/kristofer
		// in http://stackoverflow.com/questions/277502/jaxb-how-to-ignore-namespace-during-unmarshalling-xml-document
		VMPOOL tmp = (VMPOOL) (
				(JAXBElement<?>) u.unmarshal(
				new SAXSource(inputFilter, new InputSource(is)),VMPOOL.class)
				).getValue(); 
		return createList( 
			tmp);
	}

	/**
	 * <code>createList</code> creates an OCAComputeListWrapper instance
	 * from the contents of the VMPOOL passed as parameter
	 *
	 * @param tmp a <code>VMPOOL</code> the VMPOOL containing the data for
	 * the OCAComputeListWrapper creation
	 * @return an <code>OCAComputeListWrapper</code> the new
	 * OCAComputeListWrapper instance build from the contents of the VMPOOL
	 * object passed as parameter
	 */
	public static OCAComputeListWrapper createList(VMPOOL tmp) {
		List<VMCOMPLEX> list = tmp.getVM();
		OCAComputeListWrapper rv = new OCAComputeListWrapper(list.size());
		
		for(VMCOMPLEX vm: list) {
			rv.add(create(vm));
		}
		return rv;
	}

	/**
	 * <code>create</code> creates an OCAComputeListWrapper instance from
	 * the contents of the VMCOMPLEX passed as parameter
	 *
	 * @param vmComplex a <code>VMCOMPLEX</code> the VMCOMPLEX containing
	 * the data for the OCAComputeListWrapper creation
	 * @return an <code>OCAComplexComputeWrapper</code> the new
	 * OCAComputeListWrapper instance build from the contents of the
	 * VMCOMPLEX object passed as parameter
	 */
	public static OCAComplexComputeWrapper create (VMCOMPLEX vmComplex) {
		OCAComplexComputeWrapper rv = new OCAComplexComputeWrapper();

		BigInteger tmp = vmComplex.getID();
		if( tmp != null )
			rv.setId( tmp.intValue() );

		tmp = vmComplex.getUID();
		if( tmp != null )
			rv.setUid( tmp.intValue() );

		rv.setUserName(vmComplex.getUSERNAME());
		rv.setName(vmComplex.getNAME());

		tmp = vmComplex.getLASTPOLL();
		if( tmp != null )
			rv.setLastPoll( tmp.intValue() );

		tmp = vmComplex.getSTATE();
		if( tmp != null )
			rv.setState( tmp.intValue() );

		tmp = vmComplex.getLCMSTATE();
		if( tmp != null )
			rv.setLcmState( tmp.intValue() );

		tmp = vmComplex.getSTIME();
		if( tmp != null )
			rv.setStime( tmp.intValue() );

		tmp = vmComplex.getETIME();
		if( tmp != null )
			rv.setEtime( tmp.intValue() );

		rv.setDeployId(vmComplex.getDEPLOYID());

		tmp = vmComplex.getMEMORY();
		if( tmp != null )
			rv.setMemory( tmp.intValue() );

		tmp = vmComplex.getCPU();
		if( tmp != null )
			rv.setCpu( tmp.intValue() );

		tmp = vmComplex.getNETTX();
		if( tmp != null )
			rv.setNetTx( tmp.intValue() );

		tmp = vmComplex.getNETRX();
		if( tmp != null )
			rv.setNetRx( tmp.intValue() );

		tmp = vmComplex.getLASTSEQ();
		if( tmp != null )
			rv.setLastSeq( tmp.intValue() );

		rv.setTemplate( OCAComputeWrapperFactory.templateHelper( vmComplex.getTEMPLATE() ) );

		rv.setHistory(vmComplex.getHISTORY());

		return rv;
	}

}

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
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.opennebula.xmlschema.vnet_1.LEASE;
import org.opennebula.xmlschema.vnet_1.TemplateTypeNetwork;
import org.opennebula.xmlschema.vnet_1.VNET;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * Class <code>OCANicWrapperFactory</code> Creates an OCANicWrapper
 * from the OCA VirtualNetwork.info() output.
 *
 * @author <a href="mailto:madigiro@mail.eng.it">Maria di Girolamo</a>
 * @version $Revision$
 */
public class OCANicWrapperFactory {

	private static JAXBContext jbc = null;
    private static Unmarshaller u = null;
    private static Logger log;
    static {
        log = Logger.getLogger(OCANicWrapperFactory.class.getName());
        try {
            jbc = JAXBContext.newInstance(VNET.class);
            u = jbc.createUnmarshaller();
        } catch (JAXBException e) {
            log.severe(e.getMessage());
        }
    }

	/**
	 * <code>create</code> creates a OCANicWrapper instance with the
	 * data containet in a VNET object
	 *
	 * @param vNet a <code>VNET</code> the VNET with the data used to
	 * create the object
	 * @return an <code>OCANicWrapper</code> the newly created
	 * object obtainde from the data of the VNET object.
	 */
	public static OCANicWrapper create (VNET vNet) {
		OCANicWrapper rv = new OCANicWrapper();

		BigInteger tmp = vNet.getID();

		if( tmp != null )
			rv.setId( tmp.intValue() );

		tmp = vNet.getUID();
		if( tmp != null )
			rv.setUid( tmp.intValue() );

		rv.setName( (vNet.getNAME()) );

		tmp = vNet.getTYPE();
		if( tmp != null )
			rv.setType( tmp.intValue() );

		rv.setBridge( vNet.getBRIDGE() );

		rv.setPublic(vNet.getPUBLIC().intValue());
		rv.setTemplate( templateNetHelper(vNet.getTEMPLATE()));
		rv.setLeases(leasesHelper(vNet.getLEASES()));
		return rv;
	}



	/**
	 * <code>leasesHelper</code> create an OCALeaseWrapper from a LEASE
	 * object
	 *
	 * @param leaseType a <code>LEASE</code> the LEASE with the data used
	 * to create the object
	 * @return an <code>OCALeaseWrapper</code> the OCALeaseWrapper built
	 * with the data from the LEASE
	 */
	static OCALeaseWrapper leasesHelper (LEASE leaseType) {
		OCALeaseWrapper newlease = new OCALeaseWrapper();

		if ( leaseType == null )
			return newlease;

		newlease.setIp( leaseType.getIP() );

		newlease.setMac(leaseType.getMAC());

		BigInteger tmpNum = leaseType.getUSED();
		if (tmpNum != null)
			newlease.setUsed( tmpNum.intValue() );

		tmpNum = leaseType.getVID();
		if (tmpNum != null)
			newlease.setVid( tmpNum.intValue() );

		return newlease;
	}

	/**
	 * <code>templateNetHelper</code> create an OCATemplateWrapper from a
	 * TemplateTypeNetwork object
	 *
	 * @param templateTypeNet a <code>TemplateTypeNetwork</code> the
	 * TemplateTypeNetwork with the data used to create the object
	 * @return an <code>OCATemplateWrapper</code> the OCATemplateWrapper
	 * built with the data from the TemplateTypeNetwork
	 */
	static OCATemplateWrapper templateNetHelper(TemplateTypeNetwork templateTypeNet) {

		OCATemplateWrapper newTemplateNet = new OCATemplateWrapper();

		if ( templateTypeNet == null )
			return newTemplateNet;

		newTemplateNet.setContext(templateTypeNet.getCONTEXT());

		BigInteger tmpNum = templateTypeNet.getBRIDGE();
		if (tmpNum != null)
			newTemplateNet.setBridge( tmpNum.intValue() );


		newTemplateNet.setName(templateTypeNet.getNAME());

		newTemplateNet.setNetworkAddress(templateTypeNet.getNETWORKADDRESS());

		newTemplateNet.setNetworkSize(templateTypeNet.getNETWORKSIZE());

		tmpNum = templateTypeNet.getTYPE();

		if (tmpNum != null)
			newTemplateNet.setType( tmpNum.intValue() );

		return newTemplateNet;

	}


	/**
	 * <code>parseNet</code> creates a OCANicWrapper instance with the data
	 * read from an InputStream
	 *
	 * @param xml a <code>String</code> a String containing the XML
	 * @return an <code>OCANicWrapper</code> the newly created "serialized
	 * form" of the XML held by the String
	 * @exception JAXBException when XML parsing fails, of course.
	 * @exception SAXException when XML parsing fails, of course.
	 */
	public static OCANicWrapper parseNet(String xml) throws JAXBException, SAXException {
		return parseNet(new ByteArrayInputStream(xml.getBytes()));
	}

	/**
	 * <code>parseNet</code> creates a OCANicWrapper instance with the data
	 * read from an InputStream
	 *
	 * @param is an <code>InputStream</code> an InputStream containing the
	 * XML to be read
	 * @return an <code>OCANicWrapper</code> the newly created "serialized
	 * form" of the XML held by the InputStream
	 * @exception JAXBException when XML parsing fails, of course.
	 * @exception SAXException when XML parsing fails, of course.
	 */
	public static OCANicWrapper parseNet(InputStream is) throws JAXBException, SAXException {
		XMLReader reader = XMLReaderFactory.createXMLReader();

		NameSpaceFilter inputFilter = new NameSpaceFilter("http://opennebula.org/XMLSchema",true);
		inputFilter.setParent(reader);
		VNET tmp = (VNET) (
			(JAXBElement<?>) u.unmarshal(
				new SAXSource(inputFilter, new InputSource(is)),VNET.class)
			).getValue(); 
		return create( 
			tmp);
		
	}



}

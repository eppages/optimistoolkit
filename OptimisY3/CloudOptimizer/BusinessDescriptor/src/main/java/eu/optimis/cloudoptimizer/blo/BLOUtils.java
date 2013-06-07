/**
 * Copyright (C) 2010-2012 Barcelona Supercomputing Center
 *
 * This library is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public 
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.optimis.cloudoptimizer.blo;

import eu.optimis.schemas.trec.blo.BusinessDescription;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import eu.optimis.schemas.trec.blo.ObjectiveType;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class BLOUtils {
    private static JAXBContext context;
    private static Unmarshaller unmarshaller;
    private static Marshaller marshaller;
    static {
        try {
            context = JAXBContext.newInstance(BusinessDescription.class);
            unmarshaller = context.createUnmarshaller();
            marshaller = context.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );            
        } catch(Exception ex) {
            Logger.getLogger(BLOUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    public static BusinessDescription read(InputStream is) throws BLOException {
        try {
            BusinessDescription bd = (BusinessDescription) unmarshaller.unmarshal(is);
            validate(bd);
            return bd;
        } catch (JAXBException ex) {
            throw new BLOException(null,ex);
        }
    }
    
    public static String toString(BusinessDescription bd) {
        StringWriter sw = new StringWriter();
        try {
            marshaller.marshal(bd, sw);
        } catch (JAXBException ex) {
            Logger.getLogger(BLOUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return sw.toString();
    }
    
    public static void validate(BusinessDescription bd) throws BLOException {
        BLOErrorHandler eh = null;
        try {
            JAXBSource source = new JAXBSource(context, bd);
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                    new StreamSource(BLOUtils.class.getClassLoader().getResourceAsStream("policy.xsd")));            
            Validator v = schema.newValidator();
            eh = new BLOErrorHandler();
            v.setErrorHandler(eh);
            v.validate(source);     
            if(eh.hasErrors()) {
                throw new BLOException(eh);
            }
        } catch (IOException ex) {
            throw new BLOException(eh,ex);
        } catch (JAXBException ex) {
            throw new BLOException(eh,ex);
        } catch (SAXException ex) {
            throw new BLOException(eh,ex);
        }
    }
    
    
    protected static class BLOErrorHandler implements ErrorHandler {
        protected List<SAXParseException> warnings = new ArrayList<SAXParseException>();
        protected List<SAXParseException> errors = new ArrayList<SAXParseException>();
        protected List<SAXParseException> fatalErrors = new ArrayList<SAXParseException>();
        
        public void warning(SAXParseException exception) throws SAXException {
            warnings.add(exception);
        }

        public void error(SAXParseException exception) throws SAXException {
            errors.add(exception);
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            fatalErrors.add(exception);
        }
        
        public boolean hasErrors() {
            return !(warnings.isEmpty() && errors.isEmpty() && fatalErrors.isEmpty());
        }
        
    }
}

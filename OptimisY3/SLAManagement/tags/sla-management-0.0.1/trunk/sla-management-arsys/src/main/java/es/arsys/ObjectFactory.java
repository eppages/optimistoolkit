
package es.arsys;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.arsys package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.arsys
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreateVM }
     * 
     */
    public CreateVM createCreateVM() {
        return new CreateVM();
    }

    /**
     * Create an instance of {@link TerminateServiceResponse }
     * 
     */
    public TerminateServiceResponse createTerminateServiceResponse() {
        return new TerminateServiceResponse();
    }

    /**
     * Create an instance of {@link CreateVMResponse }
     * 
     */
    public CreateVMResponse createCreateVMResponse() {
        return new CreateVMResponse();
    }

    /**
     * Create an instance of {@link GetServiceData }
     * 
     */
    public GetServiceData createGetServiceData() {
        return new GetServiceData();
    }

    /**
     * Create an instance of {@link GetServiceDataResponse }
     * 
     */
    public GetServiceDataResponse createGetServiceDataResponse() {
        return new GetServiceDataResponse();
    }

    /**
     * Create an instance of {@link TerminateService }
     * 
     */
    public TerminateService createTerminateService() {
        return new TerminateService();
    }

}

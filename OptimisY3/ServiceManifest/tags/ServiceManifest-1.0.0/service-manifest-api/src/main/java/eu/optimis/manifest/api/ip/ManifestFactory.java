package eu.optimis.manifest.api.ip;

import eu.optimis.schemas.optimis.JaxBServiceManifest;
import eu.optimis.types.xmlbeans.servicemanifest.ServiceManifestDocument;

/**
 * @author arumpl
 */
public interface ManifestFactory {

    /**
     * @param manifestAsXmlBeans the XmlBeans representation of an SP service manifest
     * @return the interface to a service manifest with only the rights of an IP
     */
    public Manifest newInstance(ServiceManifestDocument manifestAsXmlBeans);

    /**
     * @param manifestAsJaxB the JaxB representation of an SP service manifest
     * @return the interface to a service manifest with only the rights of an IP
     */
    public Manifest newInstance(JaxBServiceManifest manifestAsJaxB);

    /**
     * @param manifestAsString the JaxB representation of an SP service manifest
     * @return the interface to a service manifest with only the rights of an IP
     */
    public Manifest newInstance(String manifestAsString);
}

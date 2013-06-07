package eu.optimis.manifest.api.sp;


/**
 * @author arumpl
 */
public interface ManifestFactory {

    /**
     * Creates a new Service Manifest based on the "ServiceManifestDocument.vm" template.
     * The loaded template will provide default values for all required sections.
     *
     * @param serviceId   The id of the service
     * @param componentId The id of the initial component. (e.g. JBOSS, MYSQL, etc)
     * @return the Interface for manipulating a full service manifest document
     *         with the access rights of a Service Provider.
     * @see Manifest
     */
    Manifest newInstance(String serviceId, String componentId);

}

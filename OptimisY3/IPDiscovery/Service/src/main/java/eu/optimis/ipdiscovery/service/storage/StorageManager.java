package eu.optimis.ipdiscovery.service.storage;

import java.io.IOException;
import java.util.Set;

import eu.optimis.ipdiscovery.datamodel.Provider;

/**
 * Storage interface for Provider objects
 * @author Daniel Espling
 *
 */
public interface StorageManager {

    boolean storeData(Provider provider) throws IOException;
	Provider getData(String ipId) throws IOException;
	Set<Provider> getAllData() throws IOException;
	boolean removeData(String ipId) throws IOException;
	int removeAllData() throws IOException;
	void shutdown();

}

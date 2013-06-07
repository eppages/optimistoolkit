package eu.optimis.ipdiscovery.service.storage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.optimis.ipdiscovery.datamodel.Provider;

/**
 * 
 * In-memory implementation of the StorageManager interface. This is good for
 * testing, but is not suitable for "real" use.
 * 
 * Throws away entries above 1,000 to prevent out-of-memory.
 * 
 * @author Daniel Espling <espling@cs.umu.se>
 */
public class InMemoryStorageManager implements StorageManager {

	private final int MAXRECORDS = 1000;
	private final Logger log = Logger.getLogger(InMemoryStorageManager.class
			.getName());

	/**
	 * Custom Map implementation that removes oldest records when reaching
	 * MAXRECORDS
	 */
	private Map<String, Provider> providerMap = new LinkedHashMap<String, Provider>(
			MAXRECORDS, .75F, true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(
				Map.Entry<String, Provider> eldest) {
			return size() > MAXRECORDS;
		}
	};

	@Override
	public synchronized Provider getData(String ipId) {

		if (providerMap.containsKey(ipId)) {
			return providerMap.get(ipId);
		} else {
			log.debug("Found no data for ipId: "
				+ ipId);
			return null;
		}
	}

	@Override
	public synchronized Set<Provider> getAllData() {
        log.debug("Got call to getAllData, returning: " +
	            providerMap.size() + " entries");
		return new HashSet<Provider>(providerMap.values());
	}

	@Override
	public synchronized boolean removeData(String ipId) {
		Iterator<String> it = providerMap.keySet().iterator();
		String next = null;

		while (it.hasNext()) {
			next = it.next();
			System.out.println(next);

			if (ipId.equals(next)) {
				it.remove();
				log.debug("Removed IP:" + ipId);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public synchronized int removeAllData() {
		int mapSize = providerMap.size();
		providerMap.clear();
		return mapSize;
	}

	@Override
    public synchronized boolean storeData(Provider provider) {
		providerMap.put(provider.getIdentifier(), provider);
        return true;
	}

	@Override
	public void shutdown() {
		providerMap.clear();
		providerMap = null;
		log.debug("Shut down InMemoryStorageManager.");
	}
}

package eu.optimis.sla;

import java.util.Properties;

import org.apache.log4j.Logger;

public class ComponentConfigurationProvider {
	
	private static Logger log = Logger.getLogger(ComponentConfigurationProvider.class);
	
	private static final String BUNDLE_NAME = "/component-connection.properties"; //$NON-NLS-1$

	private ComponentConfigurationProvider() {
	}

	public static String getString(String key) {
		try {
			Properties RESOURCE_BUNDLE = new Properties();
			//
			//
			//
			RESOURCE_BUNDLE.load(ComponentConfigurationProvider.class.getResource(BUNDLE_NAME).openStream());
//			RESOURCE_BUNDLE.load(Messages.class.getResourceAsStream(BUNDLE_NAME));
			
			return RESOURCE_BUNDLE.getProperty(key);
		} catch (Exception e) {
			return '!' + key + '!';
		}
	}
	
	public static boolean getBoolean(String key, boolean defaultValue) {
		try {
			Properties RESOURCE_BUNDLE = new Properties();
			RESOURCE_BUNDLE.load(ComponentConfigurationProvider.class.getResource(BUNDLE_NAME).openStream());
//			RESOURCE_BUNDLE.load(Messages.class.getResourceAsStream(BUNDLE_NAME));
		
			return Boolean.parseBoolean(RESOURCE_BUNDLE.getProperty(key, Boolean.valueOf(defaultValue).toString()));
		} catch (Exception e) {
			log.error( "failed to retrive property !" + key + "!");
			return defaultValue;
		}
	}
}

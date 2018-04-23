package abapspace.gui;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Logger;

public class GUIMMain {

    private static final String PROPERTY_RESOURCE_BUNDLE_MESSAGES = "abapspace.core.messages.messages";

    private static final String SYSTEM_PROPERTY_KEY_LOG4J2_CONFIG_FILE = "log4j2.configurationFile";
    private static final String SYSTEM_PROPERTY_VALUE_LOG4J2_CONFIG_FILE = "abapspace/core/log/log4j2.xml";

    private Logger log;
    private ResourceBundle messages;
    private Locale locale;

    public GUIMMain() {

	// set system properties
	this.setSystemProperties();

    }

    private ResourceBundle getMessages(Locale locale) throws MissingResourceException {

	ResourceBundle locResourceBundle = ResourceBundle.getBundle(PROPERTY_RESOURCE_BUNDLE_MESSAGES, locale);

	return locResourceBundle;

    }

    private void setSystemProperties() {

	Properties locSystemProperties = System.getProperties();

	locSystemProperties.setProperty(SYSTEM_PROPERTY_KEY_LOG4J2_CONFIG_FILE,
		SYSTEM_PROPERTY_VALUE_LOG4J2_CONFIG_FILE);

    }

}

package org.fl.util.response;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MessagesResourcesManager {

    private final Map<Locale, ResourceBundle> resourceBundles;
    private final Class<?> targetPackageClass;
    private final String baseName;
    private final Logger logger;
    
	public MessagesResourcesManager(Class<?> c, String shortName, Logger l) {
		
		targetPackageClass = c;
        baseName = targetPackageClass.getPackage().getName() + "." + shortName;
        resourceBundles = new ConcurrentHashMap<>();
        logger = l;
	}


    public final String getMessage(Locale locale, String key, Object... args) {
        ResourceBundle rb = getResourceBundle(locale);
        if (rb != null) {
            try {
                return MessageFormat.format(rb.getString(key), args);
            } catch (MissingResourceException e) {
                logger.log(Level.WARNING, "Missing message resource for key "+ key + " in " + baseName, e);
            } catch (NullPointerException e) {
                logger.log(Level.WARNING, "Null key", e);
            }
        } else {
            logger.warning("Resource bundle not found for " + baseName);
        }

        String message = key;
        if (args != null && args.length > 0) {
            message += ":\n    " + Arrays.stream(args).map(Object::toString).collect(Collectors.joining("\n    "));
        }
        return message;
    }
    
    public ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundle result = resourceBundles.get(locale);
        if (result == null) {
            result = loadResources(locale);
            if (result != null) {
                resourceBundles.put(locale, result);
            }
        }
        return result;
    }
    
    private ResourceBundle loadResources(Locale locale) {
        try {
            return ResourceBundle.getBundle(baseName, locale, targetPackageClass.getClassLoader());
        } catch (MissingResourceException e) {
            logger.log(Level.SEVERE, "Missing resource bundle for locale " + locale.getDisplayName(), e);
            return null;
        }
    }
    
}

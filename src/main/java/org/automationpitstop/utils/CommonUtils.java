package org.automationpitstop.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.automationpitstop.common.ReporterException;

public class CommonUtils {

	private static Logger logger = Logger.getLogger(CommonUtils.class.getName());
	
	public static String getOverriddenProperty(String propertyName, Map<String, String> propertyMap) {
		//Check if property variable is overridden by system property or env. variables
		String propertyValue = resolveVariable(propertyName);
		
		//If not overridden then retrieve the value from default map
		if (StringUtils.isBlank(propertyValue)) {
			return (String)propertyMap.get(propertyName);
		}
		return propertyValue;
	}

	public static String resolveVariable(String variableName) {
		if (StringUtils.isBlank(variableName)) {
			return null;
		}
		String propertyValue = System.getProperty(variableName); //property can be set by -DKey=value and pass as java parameters
		
		if(StringUtils.isNotBlank(propertyValue)) {
			logger.info(String.format("Parameter '%s' is overridden to '%s' by system property", variableName,propertyValue));
		}
		else { //If no property is set then look into environment variable
			propertyValue = System.getenv(variableName); //Env variable can be set by setting system variables
			if(StringUtils.isNotBlank(propertyValue)) {
				logger.info(String.format("Parameter '%s' is overridden to '%s' by system enviroment", variableName,propertyValue));
			}
		}
		return propertyValue;
	}
	
	public static Map<String, String> createContextFromPropeties(String propertyFileName) throws ReporterException {
		logger.info("Creating context from properties file : " + propertyFileName);
		Properties propertiesObj = loadPropertiestFromFile(propertyFileName);
		Map<String,String> contextMap = new HashMap<>();
		for(String propName : propertiesObj.stringPropertyNames()) {
			contextMap.put(propName, propertiesObj.getProperty(propName));
		}
		return contextMap;
	}

	public static Properties loadPropertiestFromFile(String propertyFileName) throws ReporterException {
		Properties appProps = new Properties(); 
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try(InputStream resourceStream = loader.getResourceAsStream(propertyFileName)) {
			appProps.load(resourceStream);
		} catch (Exception e) {
			throw new ReporterException(e.getMessage());
		}
		return appProps;
	}
}

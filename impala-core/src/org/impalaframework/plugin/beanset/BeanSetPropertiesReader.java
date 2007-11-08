package org.impalaframework.plugin.beanset;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

public class BeanSetPropertiesReader {

	private String DEFAULT_BEANSET_PROPERTIES_FILE = "beanset.properties";

	private String ALL_BEANSETS = "all_beans";

	final Logger logger = LoggerFactory.getLogger(BeanSetPropertiesReader.class);

	/**
	 * Reads module specification specified in the following format: "null:
	 * bean1, bean2; mock: bean3" will output a set of Properties where the
	 * spring context files for the beansets bean1 and bean2 are loaded from the
	 * file beanset_null.properties and the context files for authorisation are
	 * loaded from beanset_mock.properties. Uses beanset.properties as the
	 * default module specification
	 */
	public Properties readBeanSetSpec(ClassLoader classLoader, String definition) {
		Assert.notNull(classLoader);
		Assert.notNull(definition);

		BeanSetMapReader reader = new BeanSetMapReader();
		final Map<String, Set<String>> spec = reader.readBeanSetSpec(definition);

		return readBeanSetSpec(classLoader, spec);
	}

	public Properties readBeanSetSpec(ClassLoader classLoader, final Map<String, Set<String>> spec) {
		
		Properties defaultProps = readProperties(classLoader, DEFAULT_BEANSET_PROPERTIES_FILE);

		final Set<String> keySet = spec.keySet();
		for (String fileName : keySet) {

			String propertyFileFullName = propertyFileFullName(fileName);
			Properties overrides = readProperties(classLoader, propertyFileFullName);

			final Set<String> set = spec.get(fileName);

			if (set.size() == 1 && ALL_BEANSETS.equals(set.iterator().next())) {
				readAllBeanSets(defaultProps, overrides, propertyFileFullName);
			}
			else {
				readSelectedBeanSets(defaultProps, overrides, propertyFileFullName, set);
			}

		}
		return defaultProps;
	}

	private void readAllBeanSets(Properties defaultProps, Properties overrides, String propertyFileFullName) {

		Set<Object> propertyList = overrides.keySet();

		for (Object moduleKey : propertyList) {
			String moduleName = moduleKey.toString().trim();
			String moduleFile = overrides.getProperty(moduleName);

			applyBeanSetFile(defaultProps, moduleName, moduleFile, propertyFileFullName);

		}
	}

	private void readSelectedBeanSets(Properties defaultProps, Properties overrides, String propertyFileFullName,
			Set<String> set) {
		for (String moduleName : set) {
			String moduleFile = overrides.getProperty(moduleName);
			applyBeanSetFile(defaultProps, moduleName, moduleFile, propertyFileFullName);
		}
	}

	private void applyBeanSetFile(Properties defaultProps, String moduleName, String moduleFile,
			String propertyFileFullName) {
		if (moduleFile == null) {
			logger.warn("Unable to find application context file name for module '{}' in module properties file {}", moduleName
					, propertyFileFullName);
		}
		else {
			if (logger.isDebugEnabled()) {
				String existingValue = defaultProps.getProperty(moduleName);
				logger.debug("Overridding module file for module " + moduleName + " with " + moduleFile + ", loaded from "
						+ propertyFileFullName + ". Previous value: " + existingValue);
			}
			defaultProps.setProperty(moduleName, moduleFile);
		}
	}

	private String propertyFileFullName(String propertyFileName) {
		return "beanset_" + propertyFileName + ".properties";
	}

	protected Properties readProperties(ClassLoader classLoader, String fileName) {
		Properties properties = new Properties();
		try {
			properties.load(classLoader.getResourceAsStream(fileName));
		}
		catch (Exception e) {
			throw new FatalBeanException("Unable to load module definition file " + fileName + " on classpath.");
		}
		return properties;
	}

}

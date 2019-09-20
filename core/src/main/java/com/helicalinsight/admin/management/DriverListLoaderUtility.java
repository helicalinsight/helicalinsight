/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.admin.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.AvailablePluginClassesRepository;
import com.helicalinsight.efw.framework.ParentLastClassLoader;
import com.helicalinsight.efw.framework.Plugin;
import com.helicalinsight.efw.framework.PluginEntry;
import com.helicalinsight.efw.framework.PluginUtils;

/**
 * Date of Creation:09-03-2018
 * 
 * @author Rajesh This Utility class contains the functionalities like loading
 *         all the driver classes from DriverPath / PluginPath using regex and
 *         all the core classes that is com.helicalinsight.*.classes.
 */
public class DriverListLoaderUtility {
	/**
	 * provides all list of classes from plugin/driver path.
	 * 
	 * @return
	 */
	public static List<String> getPluginClasses() {

		List<String> pluginClasses = new ArrayList<>();
		Plugin plugin = null;
		AvailablePluginClassesRepository repository = AvailablePluginClassesRepository.getInstance();
		Map<String, Plugin> repositories = repository.getRepository();

		for (Map.Entry<String, Plugin> entry : repositories.entrySet()) {
			plugin = entry.getValue();
			pluginClasses.addAll(plugin.getClasses());

		}

		return pluginClasses;

	}

	public static List<String> getPluginClassesForEntry(Map.Entry<String, PluginEntry> entry) {

		PluginEntry pluginEntry = entry.getValue();
		ParentLastClassLoader parentLastClassLoader = pluginEntry.getParentLastClassLoader();
		return parentLastClassLoader.getRegistry();
	}

	/**
	 * Provides all the core classes that is the classes that is loaded by the
	 * application at the time of startup
	 * 
	 * @return
	 */
	public static List<String> getCoreClasses() {
		List<String> coreClasses = new ArrayList<>();

		ClassLoader classloader = DriverListLoaderUtility.class.getClassLoader();

		try {
			coreClasses = PluginUtils.getLoadedClasses(classloader);
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			throw new EfwServiceException("Couldn't retrieve the classes list from JVM", ex);
		}
		return coreClasses;
	}

	/**
	 * Filters all the classes from based on provided regex.
	 * 
	 * @param regex
	 * @return
	 */
	public static List<String> getRegexClasses(String regex) {

		List<String> loadedClass = getAllClasses();
		List<String> matchedClass = new ArrayList<>();
		for (String clazz : loadedClass) {
			if (clazz.matches(regex)) {
				matchedClass.add(clazz);
			}
		}

		return matchedClass;
	}

	/**
	 * Returns all the list of classes by adding core classes and plugin
	 * classes.
	 * 
	 * @return
	 */
	private static List<String> getAllClasses() {
		List<String> loadedClass = getCoreClasses();
		loadedClass.addAll(getPluginClasses());
		return loadedClass;
	}

	/**
	 * Filters all the classes that are exact math with the regex.
	 * 
	 * @param exactMatch
	 * @return
	 */
	public static List<String> getSpecificClasses(String exactMatch) {
		List<String> loadedClass = getAllClasses();
		List<String> matchedClass = new ArrayList<>();
		for (String clazz : loadedClass) {
			if (clazz.equals(exactMatch)) {
				matchedClass.add(clazz);
			}
		}

		return matchedClass;
	}

	public static Map<String, Boolean> getPropertiesFile(List<String> driveList) {
		List<String> loadedClass = getAllClasses();
		Map<String, Boolean> matchedClass = new HashMap<String, Boolean>();
		for (String clazz : driveList) {
			matchedClass.put(clazz, loadedClass.contains(clazz));

		}

		return matchedClass;
	}

}

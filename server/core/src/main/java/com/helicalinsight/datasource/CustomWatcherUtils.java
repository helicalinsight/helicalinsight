package com.helicalinsight.datasource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.*;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CustomWatcherUtils
 * <p> This class provides the utilities like scan(), deRegister(),update(),
 *  closeClassFromRegistry(class) this class is useful for updating or
 *  modify the PluginRegistry. </p>       
 *        
 *   @author Rajesh       
 */
public class CustomWatcherUtils {

	private static Logger logger = LoggerFactory.getLogger(CustomWatcherUtils.class);
	static final AvailablePluginClassesRepository REPOSITORY = AvailablePluginClassesRepository.getInstance();
	static PluginsRegistry registry = PluginsRegistry.getInstance();
	private final static PluginFinder pluginFinder = new PluginFinder();

	/**
	 * scan()
	 * <p>
	 * This method will provide all the requested jars and their status.Returns the
	 * info in JSONArray.
	 * </p>
	 * 
	 * @return JSONArray
	 */
	public static JsonArray scan() {

		JsonArray dataArray = new JsonArray();
		JsonArray pluginArray = jarsFromRepository();
		if (!pluginArray.isEmpty()) {
			JsonObject data = new JsonObject();

			data.add("plugins", pluginArray);
			dataArray.add(data);
		}

		return dataArray;
	}

	/**
	 * deRegister(String jarName)
	 * <p>
	 * This method will de-registers a specific jar from the PluginRegistry.
	 * </p>
	 * 
	 * @param jarName name of the jar
	 * @return successful message, if jar path is null it {@returns "The requested
	 *         jar is not present in the plugin/driver folder."} if plugin not found
	 *         in PluginsRegistry it {@returns "There are no requested drivers in
	 *         the registry please update"}
	 */
	public static String deRegister(String jarName) {

		String jarPath = getActualPathForDriver(jarName);
		if (jarPath == null) {
			return "The requested jar is not present in the plugin/driver folder.";
		}
		Map<String, PluginEntry> enabledPlugins = registry.getEnabledPlugins();
		if (enabledPlugins.isEmpty()) {
			return "There are no requested drivers in the registry please update";
		}
		File file = new File(jarPath);
		if (file.isDirectory()) {
			File[] folder = file.listFiles();
			for (File aFile : folder) {
				closeEntryPointClass(aFile);
			}
		} else {
			closeEntryPointClass(file);
		}

		pluginFinder.loadRepository();
		return "De-Registered successfully";
	}

	/**
	 * update()
	 * <p>
	 * This method will de-register and then registers all the jars from the
	 * PluginRegistry.
	 * </p>
	 * 
	 * @throws java.net.MalformedURLException
	 * @throws java.io.UnsupportedEncodingException
	 *                                              <p>
	 */
	public static void update() throws MalformedURLException, UnsupportedEncodingException {

		registry.closeAllClassLoaders();
		pluginFinder.loadRepository();

	}

	/**
	 * closeClassFromRegistry(String aClass) 
	 * <p>This method will de-register a jar by given class. </p> 
	 *  @param aClass          plugin class name      
	 *                           
	 */
	public static void closeClassFromRegistry(String aClass) {
		Map<String, PluginEntry> enabledPlugins = registry.getEnabledPlugins();
		for (Map.Entry<String, PluginEntry> entry : enabledPlugins.entrySet()) {
			List<String> entryPointClasses = entry.getValue().getPlugin().getEntryPointClasses();
			for (String entryPoint : entryPointClasses) {
				if (entryPoint.equalsIgnoreCase(aClass)) {
					registry.deRegisterAndClosePlugin(entryPoint);
					break;
				}
			}
		}
	}

	/**
	 * checkRepository(List<String> names)
	 * <p>This method will check whether a provided entryPointClass is present
	 *  in the Repository or not.</p>         
	 * @param names            plugin class names
	 * @return {@code true} if class name of plugin found, otherwise {@code false}
	 */
	public static boolean checkRepository(List<String> names) {
		Plugin plugin = null;
		boolean contains = false;
		Map<String, Plugin> repository = REPOSITORY.getRepository();
		logger.debug("before Repository  :" + repository);

		for (Map.Entry<String, Plugin> entry : repository.entrySet()) {
			plugin = entry.getValue();
			Set<String> setOfClasses = plugin.getClasses();
			plugin.getName();
			logger.debug("set of classes  :" + setOfClasses);
			for (String name : names) {
				if (setOfClasses.contains(name)) {
					contains = true;
					break;
				}
			}

		}

		return contains;
	}

	/**       
	 * jarsFromRepository()      
	 * <p>This method will provide all the repository details for Plugin tab   </p>        
	 * @return JSONArray repository details
	 */
	public static JsonArray jarsFromRepository() {

		JsonArray pluginArray = new JsonArray();

		Map<String, Plugin> repositoryPlugins = REPOSITORY.getRepository();
		for (Map.Entry<String, Plugin> entry : repositoryPlugins.entrySet()) {

			Plugin plugin = entry.getValue();
			String completePath = plugin.getName();
			String pluginPath = "";
			ParentLastClassLoader classLoader = null;
			String systemDir = ApplicationProperties.getInstance().getSystemDirectory();
			if (completePath != null) {
				pluginPath = completePath.replace(systemDir, "");
			}

			String actualName = null;
			// name
			if (plugin.isDriver() && plugin.isEnabled()) {
				List<String> entryPointClasses = plugin.getEntryPointClasses();
				for (String entryPoint : entryPointClasses) {
					actualName = JsonUtils.functionsReference(entryPoint);
					classLoader = registry.getPluginClassLoader(entryPoint);
				}
			}

			pluginArray.add(prepareData(plugin, completePath, pluginPath, classLoader, actualName));
		}

		return pluginArray;
	}
	/**
	 * prepareData(Plugin plugin, String completePath, String pluginPath,
			ParentLastClassLoader classLoader, String actualName)
	 * @param plugin                    plugin instance
	 * @param completePath              file path of jar/plugin
	 * @param pluginPath				path or name of plugin				
	 * @param classLoader               to get instance
	 * @param actualName                name of Driver   
	 * @return
	 */
	public static JsonObject prepareData(Plugin plugin, String completePath, String pluginPath,
			ParentLastClassLoader classLoader, String actualName) {
		JsonObject plugins = new JsonObject();
		plugins.addProperty("name", actualName != null ? actualName : "");

		File file = new File(completePath);
		String temNames[] = file.getName().split("\\.(?=[^\\.]+$)");
		String tempName = temNames[0];
		plugins.addProperty("temporaryName", tempName);

		long lastModified = file.lastModified();
		String lastModifiedDate = DateFormatUtils.format(lastModified, "yyyy-MM-dd");
		plugins.addProperty("installedDate", lastModifiedDate);

		plugins.addProperty("pluginType", plugin.isDriver() ? "DatbaseDriver" : "");

		JsonObject details = new JsonObject();
		details.addProperty("entryPoint", plugin.getEntryPointClasses().toString());
		details.addProperty("isDriver", plugin.isDriver() ? "Yes" : "No");
		details.addProperty("actualPath", pluginPath);
		details.addProperty("classLoaderInstance", classLoader != null ? classLoader.toString() : "");
		details.addProperty("jarName", file.getName());


        plugins.add("details", details);

        plugins.addProperty("pluginType", plugin.isDriver() ? "DatabaseDriver" : "");

		plugins.addProperty("status", plugin.isEnabled() ? "Enabled" : "Disabled");
		return plugins;
	}

	/**
	 * List<String> entryPointClasses()
	 * <p>
	 * This method provide all the entryPointClasses from the PluginRegistry.
	 * </p>
	 * @return list of all plugin names.
	 */
	public static List<String> entryPointClasses() {
		Map<String, PluginEntry> availablePlugins = registry.getEnabledPlugins();
		logger.debug("KEYSET  :" + availablePlugins.keySet());
		List<String> listOfAllEntryPointClass = new ArrayList<String>();
		for (Map.Entry<String, PluginEntry> entry : availablePlugins.entrySet()) {
			List<String> entryPointClasses = entry.getValue().getPlugin().getEntryPointClasses();
			listOfAllEntryPointClass.addAll(entryPointClasses);

		}
		return listOfAllEntryPointClass;
	}

	/**
	 * getActualPathForDriver(String pluginJar)       
	 * <p> This method will provide the actual path of given jar. This method
	 * will provide the actual path if the jar is directly present in
	 * driver/plugins.</p>
	 * @param pluginJar                 jar name
	 * @return String actualPath        
	 */
	public static String getActualPathForDriver(String pluginJar) {
		ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
		String driverPath = applicationProperties.getDriverPath();
		String pluginPath = applicationProperties.getPluginPath();
		String actualPath = null;

		File driveFilePath = new File(driverPath + File.separator + pluginJar);
		File pluginFilePath = new File(pluginPath + File.separator + pluginJar);
		if (driveFilePath.exists()) {
			actualPath = driverPath + File.separator + pluginJar;
		}
		if (pluginFilePath.exists())
			actualPath = pluginPath + File.separator + pluginJar;

		return actualPath;
	}

	/**
	 * deleteFiles(String path)      
	 * <p> This method will delete a jar from driver/plugins folder. One level only  </p>  
	 * @param path              path of plugin
	 * @return {@code true} if file exists ,otherwise {@code false}     
	 */
	public static boolean deleteFiles(String path) {

		File file = new File(path);

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				f.delete();
			}
			file.delete();
		} else {
			file.delete();
		}
		return file.exists();
	}

	/**
	 * closeClassLoader(String entryPointClass)
	 * <p> This method will de-register the given class from the  pluginRegistry.  </p>              
	 * @param entryPointClass     plugin name                                             
	 */
	public static void closeClassLoader(String entryPointClass) {
		registry.deRegisterAndClosePlugin(entryPointClass);
	}

	/**
	 * closeEntryPointClass(File aFile)
	 * <p>
	 * This method will get all the registered entryPoint classes from the
	 * pluginRegistry. Then iterates all the classes from a given file path and
	 * compares each class with entryPoint class if matches then de-registers the
	 * entryPointClass from the pluginRegistry.
	 * </p>
	 * @param aFile        to get path
	 */
	public static void closeEntryPointClass(File aFile) {
		List<String> entryPointClasses = CustomWatcherUtils.entryPointClasses();
		Set<String> classes = PluginUtils.listClasses(aFile.getAbsolutePath());
		for (String aClass : classes) {
			if (entryPointClasses.contains(aClass))
				for (String entryClass : entryPointClasses) {
					if (entryClass.equalsIgnoreCase(aClass)) {
						CustomWatcherUtils.closeClassLoader(entryClass);
					}
				}

		}
	}
}

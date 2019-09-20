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

package com.helicalinsight.efw.framework;

import com.helicalinsight.efw.ApplicationProperties;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by user on 1/12/2017.
 * <p/>
 * Parses all the plugins and stores the directories and their resources.
 * Individual jar plugins are also opened and all the classes and their names are stored.
 * <p/>
 *
 * @author Rajasekhar
 */
class RepositoryLoader {

    private static final AvailablePluginClassesRepository REPOSITORY = AvailablePluginClassesRepository.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(RepositoryLoader.class);
    private static final ApplicationProperties INSTANCE = ApplicationProperties.getInstance();

    /**
     * Loads the directories Drivers and Plugins. Used only at boot time.
     */
    public synchronized void load() {
        //Load from Plugins
        //Pick these two locations from configuration
        String pluginPath = INSTANCE.getPluginPath();
        File file = new File(pluginPath);
        File[] files = file.listFiles();

        if (files != null) {
            load(files);
        }

        //Load from Drivers
        String driverPath = INSTANCE.getDriverPath();
        file = new File(driverPath);
        files = file.listFiles();
        if (files != null) {
            load(files);
        }
    }

    private void load(File[] files) {
        for (File aFile : files) {
            String absolutePath = aFile.getAbsolutePath();
            if (aFile.isFile()) {
                loadFile(absolutePath);
            } else if (aFile.isDirectory()) {
                File[] allFiles = aFile.listFiles();

                //Instantiate a plugin
                Set<String> resources = new HashSet<>();
                String name = FilenameUtils.getName(absolutePath);
                Directory directory = new Directory(name, resources);
                Set<String> classes = new HashSet<>();
                Plugin plugin = new Plugin(absolutePath, directory, classes);

                if (allFiles != null) {
                    for (File theFile : allFiles) {
                        //Sub directories are ignored
                        if (!theFile.isDirectory()) {
                            String resourcePath = theFile.getAbsolutePath();
                            resources.add(resourcePath);
                            if (PluginUtils.isJar(resourcePath)) {
                                if (theFile.exists()) {
                                    classes.addAll(PluginUtils.listClasses(resourcePath));
                                }
                            }
                        }
                    }

                    //Make entry
                    REPOSITORY.update(absolutePath, plugin);
                }
            }
        }
    }

    private void loadFile(String absolutePath) {
        if (PluginUtils.isJar(absolutePath)) {
            if (new File(absolutePath).exists()) {
                Set<String> classes = PluginUtils.listClasses(absolutePath);
                //Make entry. Directory is null in case of a jar
                Plugin plugin = new Plugin(absolutePath, classes);
                REPOSITORY.update(absolutePath, plugin);
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Ignored. File %s is not a jar file.", absolutePath));
            }
        }
    }

    public synchronized void makeEntry(File file) {
        String absolutePath = file.getAbsolutePath();
        Plugin plugin = REPOSITORY.getPlugin(absolutePath);

        if (file.isDirectory()) {
            if (plugin != null) {
                logger.error(String.format("Unknown event. Folder is already available in the repository. No need to " +
                        "" + "create new Plugin object of %s", absolutePath));
            } else {
                //New top level folder
                //A folder is a valid plugin folder only when it is a direct descendant of Plugins or Drivers.
                //Folders inside a plugin are ignored.
                if (isValidPluginFolder(absolutePath)) {
                    Set<String> resources = new HashSet<>();
                    String name = FilenameUtils.getName(absolutePath);
                    Directory directory = new Directory(name, resources);
                    Set<String> classes = new HashSet<>();
                    plugin = new Plugin(absolutePath, directory, classes);
                    REPOSITORY.update(absolutePath, plugin);
                    //As only one CREATE event is observed when a folder is copied to Plugins/Drivers
                    File[] list = file.listFiles();
                    if (list != null) {
                        for (File resource : list) {
                            String resourceAbsolutePath = resource.getAbsolutePath();
                            resources.add(resourceAbsolutePath);
                            if (PluginUtils.isJar(resourceAbsolutePath)) {
                                classes.addAll(PluginUtils.listClasses(resourceAbsolutePath));
                            }
                        }
                    }
                }
            }
        } else {
            //Logically getParent() should give you a directory, which is the name of the plugin folder or the
            // Drivers or Plugins directories.
            String directory = file.getParent();

            if (isWatchedDirectory(directory)) {
                //Plugin jar without a directory
                loadFile(absolutePath);
            } else {
                //Already a plugin is available. So place this jar/file inside the plugin
                plugin = REPOSITORY.getPlugin(directory);

                if (plugin == null) {
                    logger.error(String.format("Expecting a plugin directory in the repository but found null. " +
                            "Can't add %s", absolutePath));
                } else {
                    //Already a plugin (directory) is registered in repository
                    plugin.addFile(absolutePath);
                }
            }
        }
    }

    private boolean isValidPluginFolder(String absolutePath) {
        String parent = new File(absolutePath).getParent();
        boolean watchedDirectory = isWatchedDirectory(parent);
        if (logger.isInfoEnabled()) {
            logger.info("The file " + parent + (watchedDirectory ? " is a watched resource" : " is not a watched " +
                    "resource."));
        }
        return watchedDirectory;
    }

    private boolean isWatchedDirectory(String directory) {
        //Deal with forward backward slashes
        directory = new File(directory).getAbsolutePath();
        String pluginPath = new File(INSTANCE.getPluginPath()).getAbsolutePath();
        String driverPath = new File(INSTANCE.getDriverPath()).getAbsolutePath();
        return directory.equalsIgnoreCase(pluginPath) || directory.equalsIgnoreCase(driverPath);
    }

    public synchronized void deleteEntry(File file) {
        String absolutePath = file.getAbsolutePath();
        Plugin plugin = REPOSITORY.getPlugin(absolutePath);

        //As file is deleted check plugin.getDirectory
        if (file.isDirectory() || (plugin != null && plugin.getDirectory() != null)) {
            if (plugin != null) {
                Plugin deletedPlugin = REPOSITORY.deletePlugin(absolutePath);
                logger.error(String.format("Deleted a folder %s from repository.", deletedPlugin.getName()));
            } else {
                logger.error(String.format("Unknown event. Can't delete a non existing folder %s from repository.",
                        absolutePath));
            }
        } else {
            String directory = file.getParent();

            if (isWatchedDirectory(directory)) {
                //Simply delete the entry. A jar plugin
                REPOSITORY.deletePlugin(absolutePath);
            } else {
                //A jar from a folder that is already present
                plugin = REPOSITORY.getPlugin(directory);
                if (plugin != null) {
                    plugin.deleteFile(absolutePath);
                } else {
                    logger.error(String.format("Can't delete a non existing file %s from repository. File might have " +
                            "" + "been deleted when its folder is deleted.", absolutePath));
                }
            }
        }
    }
}
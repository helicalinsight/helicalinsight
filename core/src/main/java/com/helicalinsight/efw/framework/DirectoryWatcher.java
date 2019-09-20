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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

class DirectoryWatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);
    private static final ApplicationProperties INSTANCE = ApplicationProperties.getInstance();
    final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private boolean trace = false;
    private RepositoryLoader repositoryLoader = new RepositoryLoader();

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path path) throws IOException {
        WatchKey key = path.register(this.watcher, ENTRY_CREATE, ENTRY_DELETE);
        if (this.trace) {
            Path prev = this.keys.get(key);
            if (prev == null) {
                logger.debug(String.format("Registering: %s", path));
            } else {
                if (!path.equals(prev)) {
                    logger.debug(String.format("Update: %s -> %s", prev, path));
                }
            }
        }
        this.keys.put(key, path);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path path) throws IOException {
        // Register directory and sub-directories
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                register(path);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public DirectoryWatcher() {
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.keys = new HashMap<>();

            String pluginPath = INSTANCE.getPluginPath();
            Path plugins = Paths.get(pluginPath);
            logger.info(String.format("Scanning %s ...", plugins));
            registerAll(plugins);

            String driverPath = INSTANCE.getDriverPath();
            Path drivers = Paths.get(driverPath);
            logger.info(String.format("Scanning %s ...", drivers));
            registerAll(drivers);
            // enable trace after initial registration
            this.trace = true;
        } catch (IOException ex) {
            throw new PluginServiceException(ex);
        }
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void run() {
        for (; ; ) {
            // Wait for key to be signalled
            WatchKey key;
            try {
                key = this.watcher.take();
            } catch (InterruptedException ex) {
                logger.error("DirectoryWatcher is interrupted. Details are ", ex);
                return;
            }

            Path path = this.keys.get(key);
            if (path == null) {
                logger.info("WatchKey not recognized!!");
                continue;
            }

            //Only create and delete events are observed. The modify events are not observed.

            //All paths are relative to the Plugins and Drivers directories inside System directory
            //1. New Plugin folder with some jars and other files (as only PluginFolder/*.jar files are scanned)
            //2. New Plugin jar (single jar also can be a plugin)
            //3. New jar file inside an existing folder
            //4. A new file inside an existing plugin folder

            //Incorrect events
            //1. A new file(not jar) inside Plugins or Drivers. This case will be ignored
            //2. Sub folders inside a plugin are not scanned as jars themselves are folders(containers) of classes
            try {
                processEvents(key, path);
            } catch (Exception ignore) {
                logger.error("An exception occurred while processing DirectoryWatcher events ", ignore);
            }

            // Reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                this.keys.remove(key);

                // All directories are inaccessible
                if (this.keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    private void processEvents(WatchKey key, Path path) {
        Path previousChild = null;
        List<WatchEvent<?>> watchEvents = key.pollEvents();
        if (logger.isDebugEnabled()) {
            logger.debug("List of events " + watchEvents);
        }
        for (WatchEvent<?> event : watchEvents) {
            WatchEvent.Kind kind = event.kind();

            if (kind == OVERFLOW) {
                continue;
            }

            // Context for directory entry event is the file name of entry
            WatchEvent<Path> watchEvent = cast(event);
            Path name = watchEvent.context();
            Path child = path.resolve(name);

            if (previousChild == null) {
                previousChild = handleCreateAndDeleteEvents(kind, child);
            } else {
                if (previousChild.toString().equals(child.toString())) {
                    continue;
                } else {
                    previousChild = handleCreateAndDeleteEvents(kind, child);
                }
            }

            // If directory is created, and watching recursively, then
            // register it and its sub-directories
            if (kind == ENTRY_CREATE) {
                try {
                    if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                        registerAll(child);
                    }
                } catch (IOException ex) {
                    // Ignore to keep sample readable
                }
            }
        }
    }

    private Path handleCreateAndDeleteEvents(WatchEvent.Kind kind, Path child) {
        log(kind, child);
        File file = child.toFile();
        if (kind == ENTRY_CREATE) {
            this.repositoryLoader.makeEntry(file);
        }
        if (kind == ENTRY_DELETE) {
            this.repositoryLoader.deleteEntry(file);
        }
        //Don't let same file or folder be processed multiple times
        return child;
    }

    private void log(WatchEvent.Kind kind, Path child) {
        logger.info(String.format("Event notified is %s: Related to %s", kind.name(), child));
    }
}
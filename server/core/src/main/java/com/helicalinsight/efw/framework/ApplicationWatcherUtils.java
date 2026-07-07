package com.helicalinsight.efw.framework;

import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileBrowserCacheUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import io.methvin.watcher.DirectoryWatcher;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Rajesh
 *         Created by helical019 on 3/26/2019.
 */

public class ApplicationWatcherUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationWatcherUtils.class);
    private final Path directoryToWatch;
    private final DirectoryWatcher watcher;

    public ApplicationWatcherUtils(Path directoryToWatch) {
        try {
            this.directoryToWatch = directoryToWatch;
            this.watcher = DirectoryWatcher.builder()
                    .path(directoryToWatch)// or use paths(directoriesToWatch)
                    .logger(logger)
                    .listener(event -> {
                        Path path = event.path();
                        if (!StringUtils.containsIgnoreCase(path.toString(), "System")) {
                            String relativePath = ApplicationUtilities.getRelativeSolutionPath(path.toString());
                            String extension = FilenameUtils.getExtension(path.toString());
                            if (extension.isEmpty() && !relativePath.isEmpty()) {
                                extension = "newFolder";
                            }
                            // if directory is created, and watching recursively, then register it and its sub-directories
                            List<String> allExtension = JsonUtils.getAllVisibleExtension();
                            allExtension.add(JsonUtils.getFolderFileExtension());
                            allExtension.add("xml");
                            allExtension.add("newFolder");
                            allExtension.add(JsonUtils.getEfwvfExtension());
                            if (allExtension.contains(extension)) {
                                switch (event.eventType()) {
                                    case CREATE: /* file created */
                                    case MODIFY: /* file modified */
                                        madeChangesDbWithLock(path.toString());
                                        break;
                                    case DELETE: /* file deleted */
                                        deleteFromDbWithLock(path.toString());
                                        break;
                                }
                            }
                        }
                    })
                            // .fileHashing(false) // defaults to true
                            // .logger(logger) // defaults to LoggerFactory.getLogger(DirectoryWatcher.class)
                            // .watchService(watchService) // defaults based on OS to either JVM WatchService or the JNA macOS WatchService
                            //.fileHasher(FileHasher.LAST_MODIFIED_TIME)
                    .build();

        } catch (Exception e) {
            logger.error("The error occurred", e);
            throw new EfwException("Exception occurred during watcher.");
        }
    }

    public void stopWatching() throws IOException {
        watcher.close();
    }

    public CompletableFuture<Void> watch() {
        // you can also use watcher.watch() to block the current thread
        return watcher.watchAsync();
    }

    public void madeChangesDbWithLock(String path) {
        if (!ApplicationUtilities.lockPathForFileBrowser.contains(path)) {
            FileBrowserCacheUtils.madeChangesDb(path);
        }
    }

    public void deleteFromDbWithLock(String path) {
        if (!ApplicationUtilities.lockPathForFileBrowser.contains(path)) {
            FileBrowserCacheUtils.deleteFromDb(path);
        }

    }
}
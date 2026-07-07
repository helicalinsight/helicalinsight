package com.helicalinsight.efw;

import com.helicalinsight.admin.model.FileBrowserCache;
import com.helicalinsight.efw.resourceloader.DirectoryLoader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Rajesh
 *         Created by helical019 on 6/19/2019.
 */
public class HIManagedCallableThread implements Callable<List<Map<String, String>>> {
    private List<FileBrowserCache> listOfFileBrowser;
    private boolean flag1;
    private boolean flag2;
    private DirectoryLoader directoryLoader;

    public HIManagedCallableThread(List<FileBrowserCache> listOfFileBrowser, boolean flag1, boolean flag2, DirectoryLoader directoryLoader) {
        this.listOfFileBrowser = listOfFileBrowser;
        this.flag1 = flag1;
        this.flag2 = flag2;
        this.directoryLoader = directoryLoader;
    }

    @Override
    public List<Map<String, String>> call() throws Exception {
        return directoryLoader.prepareOnlyPermission(listOfFileBrowser, flag1, flag2);
    }
}

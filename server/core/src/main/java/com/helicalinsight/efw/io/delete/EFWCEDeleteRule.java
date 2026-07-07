package com.helicalinsight.efw.io.delete;

import com.helicalinsight.efw.io.IOOperationsUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rajesh
 *         Created by author on 6/21/2019.
 */
@Deprecated
public class EFWCEDeleteRule implements IDeleteRule {
    @Override
    public boolean isDeletable(File file) {
        return true;
    }

    @Override
    public void delete(File file) {
        deleteRelatedFiles(file);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    private void deleteRelatedFiles(File efwceFile) {
        List<File> listOfFiles = new ArrayList<>();
        String efwcePath = efwceFile.getPath();
        String directory = efwceFile.getParent();
        String uuid = FilenameUtils.getBaseName(efwcePath);

        File efwFile = new File(directory + File.separator + uuid + "." + JsonUtils.getEfwExtension());

        File efwdFile = new File(directory + File.separator + uuid + "." + JsonUtils.getEfwdExtension());

        File efwvfFile = new File(directory + File.separator + uuid + "." + JsonUtils.getEfwvfExtension());

        File htmlFile = new File(directory + File.separator + uuid + ".html");

        listOfFiles.add(efwceFile);
        listOfFiles.add(efwFile);
        listOfFiles.add(efwdFile);
        listOfFiles.add(efwvfFile);
        listOfFiles.add(htmlFile);
        listOfFiles.forEach(file -> IOOperationsUtility.safeDeleteWithLogs(file));

    }

}

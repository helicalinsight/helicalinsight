package com.helicalinsight.efw.io.delete;

import com.helicalinsight.efw.io.IOOperationsUtility;

import java.io.File;

/**
 * Created by author on 10/18/2019.
 *
 * @author Rajesh
 */
@Deprecated
public class HCRDeleteRule implements IDeleteRule{
    @Override
    public boolean isDeletable(File file) {
        return true;
    }

    @Override
    public void delete(File file) {
        IOOperationsUtility.safeDeleteWithLogs(file);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

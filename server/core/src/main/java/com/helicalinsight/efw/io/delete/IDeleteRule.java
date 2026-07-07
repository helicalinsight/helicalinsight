package com.helicalinsight.efw.io.delete;

import com.helicalinsight.efw.resourceloader.rules.IRule;

import java.io.File;

/**
 * Used for file delete operations based on various implementations
 * <p/>
 * Created by author on 12-Oct-14.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public interface IDeleteRule extends IRule {

    /**
     * The file is deletable only when it has matching user credentials
     *
     * @param file The file under concern
     * @return true if deletable
     */
    boolean isDeletable(File file);

    /**
     * Simply deletes the file
     *
     * @param file The file under concern
     */
    void delete(File file);

}

package com.helicalinsight.efw.io;

import org.apache.commons.fileupload.FileItem;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Somen
 *         Created by helical021 on 4/26/2018.
 */
public interface IUpload {
    public boolean processMultipartItem(HttpServletRequest request, FileItem fileObject, String destination,
                                        String extensionOfFileTypeToBeImported);
}

package com.helicalinsight.efw.externalresources;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;

public interface IExternalResource {

    /**
     * method for get File type
     *
     * @return String
     */
    String getFileType();

    /**
     * method for get File
     *
     * @return File
     */
    File getFile();

    /**
     * method for set File
     *
     * @param file File
     */
    void setFile(File file);

    /**
     * method for get HttpServletResponse
     *
     * @return HttpServletResponse
     */
    HttpServletResponse getResponse();

    /**
     * method for set HttpServletResponse
     *
     * @param response HttpServletResponse
     */
    void setResponse(HttpServletResponse response);
}

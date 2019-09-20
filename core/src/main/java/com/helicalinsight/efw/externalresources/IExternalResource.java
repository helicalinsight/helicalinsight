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

package com.helicalinsight.efw.externalresources;

import javax.servlet.http.HttpServletResponse;
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

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

import com.helicalinsight.efw.utility.ApplicationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * This class implements the IExternalResource interface and responsible for
 * reading different types of images
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class ImageReader implements IExternalResource {

    private static final Logger logger = LoggerFactory.getLogger(ImageReader.class);

    private static final int DEFAULT_BUFFER_SIZE = 10240;
    private File file;

    private HttpServletResponse response;

    /**
     * This method calls the getImage() method
     */
    @Override
    public String getFileType() {
        try {
            getImage();
        } catch (IOException ex) {
            logger.error("IOException", ex);
        }
        return null;
    }

    public void getImage() throws IOException {
        getResponse().reset();
        getResponse().setBufferSize(DEFAULT_BUFFER_SIZE);
        getResponse().setContentType(getFile().getName());
        getResponse().setHeader("Content-Length", String.valueOf(getFile().length()));
        getResponse().setHeader("Content-Disposition", "inline; filename=\"" + getFile().getName() + "\"");

        BufferedInputStream input = null;
        BufferedOutputStream output = null;
        try {
            input = new BufferedInputStream(new FileInputStream(getFile()), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(getResponse().getOutputStream(), DEFAULT_BUFFER_SIZE);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            logger.error("An IOException occurred", ex);
        } finally {
            ApplicationUtilities.closeResource(output);
            ApplicationUtilities.closeResource(input);
        }
    }

    /**
     * Getter method for HttpServletResponse
     */
    @Override
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Setter method for HttpServletResponse
     */
    @Override
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * Getter method for file
     */
    @Override
    public File getFile() {
        return file;
    }

    /**
     * Setter method for file
     */
    @Override
    public void setFile(File file) {
        this.file = file;

    }
}

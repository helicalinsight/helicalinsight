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
 * This class is used to read the external files like html, js, css
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */

public class ExternalResourceReader implements IExternalResource {

    private static final Logger logger = LoggerFactory.getLogger(ExternalResourceReader.class);

    /**
     * This variable is reference type of StringBuilder
     */
    private StringBuilder sb = null;

    /**
     * This variable is reference type of File
     */
    private File file;

    /**
     * This variable is reference of type HttpServletResponse
     */
    private HttpServletResponse response;

    /**
     * This method is responsible for reading the file line by line, write to
     * OutputStream and return the string
     */
    @Override
    public String getFileType() {
        logger.info("Invoking getFileType in  " + this.getClass().getName() + " for the file " + file);
        String currentLine;
        BufferedReader bufferedReader = null;
        try {
            sb = new StringBuilder();
            bufferedReader = new BufferedReader(new FileReader(getFile()));
            while ((currentLine = bufferedReader.readLine()) != null) {
                sb.append(currentLine);
                sb.append(System.getProperty("line.separator"));
            }
            String encoding = ApplicationUtilities.getEncoding();

            OutputStream outputStream = response.getOutputStream();
            String outputResult = sb.toString();
            outputStream.write(outputResult.getBytes(encoding));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            logger.error("IOException occurred", e);
            //handle error
        } finally {
            ApplicationUtilities.closeResource(bufferedReader);
        }
        return sb.toString();
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
}

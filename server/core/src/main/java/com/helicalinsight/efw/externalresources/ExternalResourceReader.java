package com.helicalinsight.efw.externalresources;

import com.helicalinsight.efw.utility.ApplicationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * This class is used to read the external files like html, js, css
 *
 * @author muqtar ahmed
 * @author Rajasekhar
 * @version 1.1
 * @since 1.0
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
        logger.info("ExternalResourceReader Class " + this.getClass().getName() + " for the file " + file);
        String currentLine;
        BufferedReader bufferedReader = null;
        try {
            sb = new StringBuilder();
            bufferedReader = new BufferedReader(new  InputStreamReader(new FileInputStream(getFile()), ApplicationUtilities.getEncoding()));
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

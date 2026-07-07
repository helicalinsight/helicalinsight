package com.helicalinsight.efw.externalresources;

import com.helicalinsight.efw.utility.ApplicationUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * This class implements the IExternalResource interface and responsible for
 * reading different types of images
 *
 * @author muqtar ahmed
 * @author Rajasekhar
 * @version 1.1
 * @since 1.0
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

        getResponse().setBufferSize(DEFAULT_BUFFER_SIZE);
        String extension = FilenameUtils.getExtension(getFile().getName());
        getResponse().setContentType(ApplicationUtilities.getContentType(extension));
        getResponse().setHeader("Content-Length", String.valueOf(getFile().length()));
        getResponse().setHeader("accept-ranges", "bytes");
        //getResponse().setHeader("Content-Disposition", "inline; filename=\"" + getFile().getName() + "\"");
        getResponse().setHeader("File-Name",  getFile().getName());

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
     * Setter method for HttpServletResponse
     */
    @Override
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
    
}

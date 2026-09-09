package com.helicalinsight.efw.externalresources;

import com.helicalinsight.efw.utility.ApplicationUtilities;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Streams binary font files (ttf/otf/ttc/woff) for {@code /getExternalResource}.
 */
@SuppressWarnings("unused")
public class FontReader implements IExternalResource {

    private static final Logger logger = LoggerFactory.getLogger(FontReader.class);
    private static final int DEFAULT_BUFFER_SIZE = 10240;

    private File file;
    private HttpServletResponse response;

    @Override
    public String getFileType() {
        try {
            streamFont();
        } catch (IOException ex) {
            logger.error("IOException while streaming font", ex);
        }
        return null;
    }

    private void streamFont() throws IOException {
        getResponse().setBufferSize(DEFAULT_BUFFER_SIZE);
        String extension = FilenameUtils.getExtension(getFile().getName());
        getResponse().setContentType(ApplicationUtilities.getContentType(extension));
        getResponse().setHeader("Content-Length", String.valueOf(getFile().length()));
        getResponse().setHeader("accept-ranges", "bytes");
        getResponse().setHeader("File-Name", getFile().getName());
        getResponse().setHeader("Cache-Control", "public, max-age=86400");

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
            logger.error("An IOException occurred while reading font {}", getFile(), ex);
        } finally {
            ApplicationUtilities.closeResource(output);
            ApplicationUtilities.closeResource(input);
        }
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}

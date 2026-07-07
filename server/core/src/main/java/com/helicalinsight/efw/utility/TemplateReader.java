package com.helicalinsight.efw.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * The instance of this class reads the file mentioned in the efw template tag.
 *
 * @author Avi
 * @author Muqtar Ahmed
 * @since 1.1
 */
public class TemplateReader {

    private static final Logger logger = LoggerFactory.getLogger(TemplateReader.class);

    /**
     * The template file to be read
     */
    private final File file;
    /**
     * For processing purposes
     */
    private StringBuilder sb = null;

    /**
     * Constructs an instance of the same class with the file
     *
     * @param file The file under concern
     */
    public TemplateReader(File file) {
        this.file = file;
    }

    /**
     * Returns the file content as a string by reading it
     *
     * @return The file content as string
     */
    public String readTemplate() {
        String currentLine;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new  InputStreamReader(new FileInputStream(file), ApplicationUtilities.getEncoding()));
            sb = new StringBuilder();
            while ((currentLine = bufferedReader.readLine()) != null) {
                sb.append(currentLine);
                sb.append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            logger.error("An Exception occurred", e);
            //handle error
        } finally {
            ApplicationUtilities.closeResource(bufferedReader);
        }
        logger.debug("Reading html file is completed.");
        return sb.toString();
    }
}
package com.helicalinsight.efw.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * The class uses RandomAccessFile api to write a file with a particular
 * encoding
 *
 * @author Rajasekhar
 * @since 1.0
 */
public class FileStatusInspectionUtility {

    private static final Logger logger = LoggerFactory.getLogger(FileStatusInspectionUtility.class);

    /**
     * returns true only if the file is completely written
     *
     * @param file       The file to written
     * @param htmlString The content of the file to be written
     * @param encoding   Encoding of the content. Usually utf-8
     * @return true only if the file is completely written
     */
    public boolean isCompletelyWritten(File file, String htmlString, String encoding) {

        RandomAccessFile stream = null;
        try {

            //On 7/20/2017 As reported by Envision. Fixed a bug as the existing code was only upgrading the content. It was not fully deleting the old content.
            if(file.exists()){
                file.delete();
            }
            //Bug fixes end
            stream = new RandomAccessFile(file, "rw");
            byte[] html = htmlString.getBytes(encoding);
            stream.write(html);
            return true;
        } catch (IOException ex) {
            logger.error("Exception stack trace is ", ex);
            logger.info("Skipping file " + file.getName() + " as it's not completely written");
        } finally {
            ApplicationUtilities.closeResource(stream);
        }
        return false;
    }
}

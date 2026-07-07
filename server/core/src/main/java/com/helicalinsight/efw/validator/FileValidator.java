package com.helicalinsight.efw.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * The various framework related xml files like efw, efwd, and efwvf have a
 * pre-defined file structure in xsd format. The efw files are validated using
 * the xsd file present at System/XSDfile directory using an instance of this
 * class.
 *
 * @author Rajasekhar
 * @author Muqtar Ahmed
 * @version 1.0
 * @since 1.0
 */
public class FileValidator {

    private static final Logger logger = LoggerFactory.getLogger(FileValidator.class);

    /**
     * The file under concern
     */
    private String file;

    /**
     * Getter for the file
     *
     * @return The file itself
     */
    public String getFile() {
        return file;
    }

    /**
     * Setter method for the file
     *
     * @param file The file under concern
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Checks whether the file exists physically or not
     *
     * @return true if the file physically exists
     */
    public boolean isFilePresent() {
        boolean fileExist = false;
        File xmlFle = new File(file);
        if (xmlFle.isFile() && xmlFle.exists()) {
            fileExist = true;
        }
        logger.info("The file " + file + (fileExist ? " exists." : " does not exists!"));
        return fileExist;
    }
}

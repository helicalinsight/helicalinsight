package com.helicalinsight.efw.io;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.XmlConfigurationException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileBrowserCacheUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * A directory of efwsr key value extension files will be imported in to a
 * destination directory. Other types of files or directories won't be imported.
 * <p>
 * Created by author on 21-10-2014.
 *
 * @author Rajasekhar
 * @version 1.1
 * @since 1.1
 */
@Deprecated
public final class EFWSRImportRule implements IImportRule {

    private static final Logger logger = LoggerFactory.getLogger(EFWSRImportRule.class);

    /**
     * For Singleton structure - A private constructor
     */
    private EFWSRImportRule() {
    }

    /**
     * Typical singleton class instance getter
     *
     * @return Instance of the same class
     */
    @NotNull
    public static IImportRule getInstance() {
        return EfwsrImportRuleHolder.INSTANCE;
    }

    /**
     * Imports the directory represented by the directory parameter in to the
     * corresponding destination specified as string.
     * <p>
     * The destinationDirectory should actually exist on the file system. If
     * matching file names are found in the destination directory then the
     * conflicting file will be renamed with system current time
     *
     * @param directory   The directory to be imported
     * @param destination The destination file path as string
     * @return false if an IOException occurs
     */
    public boolean importFile(@NotNull File directory, String destination) {
        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        File destinationDirectory = new File(solutionDirectory + File.separator + destination);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!modifyXmlFile(file)) {
                    /*
                     * Only in case of IOException the method returns false
					 */
                    logger.error("Couldn't import the file " + file + ". Trying to import other " +
                            "files.");
                }
                String lockPath = destinationDirectory.getAbsolutePath() + File.separator + file.getName();
                try {
                    ApplicationUtilities.addLockPathForFileBrowser(lockPath);
                    FileUtils.moveFileToDirectory(file, destinationDirectory, false);
                    FileBrowserCacheUtils.madeChangesDb(destinationDirectory.getAbsolutePath());
                    ApplicationUtilities.removeLockPathForFileBrowser(lockPath);
                } catch (FileExistsException e) {
                    ApplicationUtilities.removeLockPathForFileBrowser(lockPath);
                    logger.info("The destination already consists the file." + file);
                    String fileName = FilenameUtils.getName(file.getAbsolutePath());
                    String basePath = FilenameUtils.getPath(file.getAbsolutePath());
                    File modifiedFile = new File(FilenameUtils.getPrefix(file.toString()) + File.separator + basePath
                            + System.currentTimeMillis() + "_" + fileName);
                    logger.debug("Trying to modify conflicting file name with system time " + modifiedFile);
                    if (file.renameTo(modifiedFile)) {
                        logger.debug("Successfully modified to " + file);
                    } else {
                        logger.debug("Couldn't rename " + file);
                    }
                    lockPath = destinationDirectory.getAbsolutePath() + File.separator + modifiedFile.getName();
                    try {
                        ApplicationUtilities.addLockPathForFileBrowser(lockPath);
                        FileUtils.moveFileToDirectory(modifiedFile, destinationDirectory, false);
                        FileBrowserCacheUtils.madeChangesDb(destinationDirectory.getAbsolutePath());
                        ApplicationUtilities.removeLockPathForFileBrowser(lockPath);
                    } catch (FileExistsException ex) {
                        ApplicationUtilities.removeLockPathForFileBrowser(lockPath);
                        throw new OperationFailedException("An Error occurred. Couldn't import as" +
                                "the destination consists of a file/folder with the same " +
                                "physical name.");
                    } catch (IOException ex) {
                        ApplicationUtilities.removeLockPathForFileBrowser(lockPath);
                        logger.error("IO Error: ", ex);
                        throw new OperationFailedException("An Error occurred. Couldn't import. " + "Please check " +
                                "logs for more details.");
                    }
                } catch (IOException ex) {
                    ApplicationUtilities.removeLockPathForFileBrowser(lockPath);
                    logger.error("IO Error: ", ex);
                    throw new OperationFailedException("An Error occurred. Couldn't import. " + "Please check logs " +
                            "for more details.");
                }
            }
        }
        IOOperationsUtility.deleteEmptyDirectoryWithLogs(directory);
        return true;
    }

    /**
     * The newly imported efwsr files favourite tag will be made false and the
     * security tag will be modified according to the currently logged in user.
     *
     * @param file The file under concern
     * @return true if successfully modifies the xml
     */
    private boolean modifyXmlFile(@NotNull File file) {
        Efwsr efwsr = JaxbUtils.unMarshal(Efwsr.class, file);
        if (efwsr == null) {
            throw new OperationFailedException("The file " + file + " doesn't exists.");
        }
        efwsr.setSecurity(SecurityUtils.securityObject());
        efwsr.setFavourite("false");
        efwsr.setSchedulingReference(null);
        try {
            synchronized (this) {
                JaxbUtils.marshal(efwsr, file);
            }
        } catch (Exception ex) {
            logger.error("Stack trace: ", ex);
            return false;
        }
        return true;
    }

    /**
     * Only a directory of files of type with extension of the key value efwsr
     * will be imported. The method validates whether the directory consists of
     * other extension files or not. If it consists it returns false. Similarly
     * directories also can't be imported.
     *
     * @param directory The directory to be validated
     * @return true if successfully validated
     */
    public boolean validateDirectory(@NotNull File directory) {
        String efwsrExtension = JsonUtils.getEFWSRExtension();
        File[] files = directory.listFiles();
        if (files != null) {
            if (efwsrExtension != null) {
                logger.debug("files length : " + files.length);
                for (File file : files) {
                    if (!file.isFile() && efwsrExtension.equalsIgnoreCase(com.helicalinsight.efw.utility.FileUtils
                            .getExtensionOfFile(file))) {
                        logger.error("File " + file + " type is unknown! Can't import such files.");
                        return false;
                    }
                    if (file.isDirectory()) {
                        logger.error("File " + file + " is a directory. Can't import directories." +
                                " Expecting only efwsr files.");
                        return false;
                    }
                }
            } else {
                throw new XmlConfigurationException("EFWSR tag value not found in setting.xml");
            }
        } else {
            logger.error("Directory " + directory + " is null.");
            return false;
        }
        return true;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    /**
     * Initialization-on-demand holder idiom. Instance is created only when there is a
     * call to getInstance.
     */
    private static class EfwsrImportRuleHolder {
        public static final IImportRule INSTANCE = new EFWSRImportRule();
    }
}

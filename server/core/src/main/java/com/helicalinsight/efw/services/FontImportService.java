package com.helicalinsight.efw.services;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.utility.FontFileUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FontImportService {

    private static final Logger logger = LoggerFactory.getLogger(FontImportService.class);

    public void importFont(FileItem fileObject, String zipFilePassword) {
        String fileName = FilenameUtils.getName(fileObject.getName());
        String suffix = FilenameUtils.getExtension(fileName);
        if (!FontFileUtils.isSupportedUploadExtension(suffix)) {
            throw new OperationFailedException("Select a file only with extension ttf, otf, ttc, woff, woff2, jar, or zip");
        }

        String tempDirectory = TempDirectoryCleaner.getTempDirectory() + File.separator;
        File uploadedFile = new File(tempDirectory + fileName);
        try {
            if (uploadedFile.exists()) {
                uploadedFile.delete();
            }
            fileObject.write(uploadedFile);

            File targetDirectory = new File(ApplicationProperties.getInstance().getFontsPath());
            FileUtils.forceMkdir(targetDirectory);

            if (FontFileUtils.isFontExtension(suffix)) {
                FontFileUtils.copyFontFile(uploadedFile, targetDirectory);
            } else if ("jar".equalsIgnoreCase(suffix)) {
                FontFileUtils.extractFontEntriesFromJar(uploadedFile, targetDirectory);
            } else if ("zip".equalsIgnoreCase(suffix)) {
                FontFileUtils.extractFontEntriesFromZip(uploadedFile, targetDirectory, zipFilePassword);
            }

            FontService.refreshAfterImport();
        } catch (OperationFailedException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Font upload failed", ex);
            String message = ex.getMessage();
            if (message != null && message.contains("exists")) {
                message = "The file already exists";
            }
            throw new OperationFailedException("The font file could not be imported. " + message);
        } finally {
            FileUtils.deleteQuietly(uploadedFile);
        }
    }
}

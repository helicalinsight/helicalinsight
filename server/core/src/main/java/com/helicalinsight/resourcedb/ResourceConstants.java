package com.helicalinsight.resourcedb;

import com.helicalinsight.efw.utility.JsonUtils;

public class ResourceConstants {
    public final static String FILE_SEPERATOR = "/";

    //TODO need to use tag value from Extentions in setting.xml, to match with the resource type
    /*public final static String FOLDER_EXTENSION= ".efwFolder";*/

    public final static String FILE = "file";

    public final static String FOLDER = "folder";

    public final static String METADATA = "metadata";

    public final static String METADATA_EXTENSION = "."+JsonUtils.getMetadataExtension();

    public final static Integer ResourceNameSize=60;

    public final static String EFWDD = "efwdd";

}

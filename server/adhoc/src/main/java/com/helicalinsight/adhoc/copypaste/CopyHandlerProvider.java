package com.helicalinsight.adhoc.copypaste;

import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import static com.helicalinsight.efw.framework.utils.ApplicationContextAccessor.getBean;

/**
 * Class CopyHandlerProvider provides instances of {@link HiResourceCopyHandler} based on the resource type.
 */
public class CopyHandlerProvider {
	
	private static final String HREPORT="hr";
	private static final String EFWDD="efwdd";
	private static final String EFWSR="efwsr";
	private static final String METADATA="metadata";
	private static final String CUBE="cube";
	private static final String FOLDER="folder";
	private static final String EFWD="efwd";
	private static final String HCR="hcr";
	private static final String IMAGE="image";
	private static final String INSTANT="instant";
	private static final String AIAGENT="agent";

	 /**
     * Gets an instance of {@link HiResourceCopyHandler} based on the resource type.
     * @param resourceType 		 type of the resource like HREPORT,EFWDD,EFWSR etc.
     * @return An instance of {@link HiResourceCopyHandler} corresponding to the resource type.
     */

	 public static HiResourceCopyHandler getInstance(String resourceType) {
		
		switch(resourceType) {
			case HREPORT: return  getBean(HiResourceHreportCopyHandler.class);
			case EFWDD: return getBean(HiResourceEfddCopyHandler.class);
			case EFWSR: return getBean(HiResourceEfwsrCopyHandler.class);
			case METADATA: return getBean(HiResourceMetadataCopyHandler.class);
			case CUBE:return getBean(HiResourceCubeCopyHandler.class);
			case FOLDER:return getBean(HiResourceFolderCopyHandler.class);
			case HCR:return getBean(HiResourceHCRCopyHandler.class);
			case EFWD: return getBean(EfwdPlainConCopyHandler.class);
			case IMAGE: return getBean(ImageCopyHandler.class);
			case INSTANT: return getBean(InstantBICopyHandler.class);
			case AIAGENT: return getBean(AIAgentCopyHandler.class);
			default:
				throw new EfwServiceException("Could not find Handler for "+resourceType);
		}

	}
	
}

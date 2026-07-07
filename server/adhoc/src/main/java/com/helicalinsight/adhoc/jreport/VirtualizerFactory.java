package com.helicalinsight.adhoc.jreport;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.admin.dto.HCRPrintCacheConfigurationDTO;
import com.helicalinsight.admin.dto.SwapConfigDTO;
import com.helicalinsight.cache.CacheUtils;

import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

public final class VirtualizerFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(VirtualizerFactory.class);
	
	public JRVirtualizer create(final boolean isExport, final String directory) {
		return isExport ? createGzipVirtualizer() : createSwapFileVirtualizer(directory);
	}
	
	private final JRVirtualizer createGzipVirtualizer() {
		logger.debug("Initializing GZip virtualizer");
		HCRPrintCacheConfigurationDTO config = CacheUtils.getHCRPrintCacheConfig();
		return new JRGzipVirtualizer(config.getMaxPagesInMemory());
	}

	private final JRVirtualizer createSwapFileVirtualizer(String directory) {
		logger.debug("Initializing SwapFile  virtualizer");
		HCRPrintCacheConfigurationDTO config = CacheUtils.getHCRPrintCacheConfig();
		SwapConfigDTO swap  = 	config.getSwap();
		String swapDir = CacheUtils.getCacheDirectory() + File.separator + directory;
		new File(swapDir).mkdirs();
		JRSwapFile swapFile = new JRSwapFile(swapDir,  swap.getBlockSize(), swap.getMinGrowCount());
		return new JRSwapFileVirtualizer(config.getMaxPagesInMemory(), swapFile);
	}
}


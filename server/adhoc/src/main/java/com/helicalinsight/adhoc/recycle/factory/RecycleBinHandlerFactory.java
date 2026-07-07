package com.helicalinsight.adhoc.recycle.factory;

import com.helicalinsight.adhoc.recycle.handler.RecycleBinHandler;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

public class RecycleBinHandlerFactory {
	
	private  RecycleBinHandlerFactory() {
		
	}
	
	public static RecycleBinHandler getHandler(String binType,String action) {
		//  hi_resource_db_deleteHandler , ds_global_connections_deleteHandler, hi_resource_db_restoreHandler,
		// ds_global_connections_restoreHandler
		
		return (RecycleBinHandler) ApplicationContextAccessor.getBean(binType.toLowerCase()+"_"+action+"Handler");
	}

}

package com.helicalinsight.adhoc.recycle.factory;

import com.helicalinsight.adhoc.recycle.action.RecycleBinAction;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

public class RecycleBinActionFactory {

	private RecycleBinActionFactory() {

	}

	public static RecycleBinAction getInstance(String action) {
		return  (RecycleBinAction) ApplicationContextAccessor.getBean(action.toLowerCase() + "ActionComponent");
	}

}

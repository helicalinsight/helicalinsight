package com.helicalinsight.adhoc.metadata.genericdb;

import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

public final class DatasourceChangeHandlerFactory {

	private DatasourceChangeHandlerFactory() {

	}

	public static DatasourceChangeHandler getHandler(String source, String target) {
		if (isHomo(source, target)) {
			return ApplicationContextAccessor.getBean(HomogeneousDatasourceChangeHandler.class);
		} else {
			return ApplicationContextAccessor.getBean(HeterogeneousDatasourceChangeHandler.class);
		}
	}

	private static boolean isHomo(String source, String target) {
		return (GlobalJdbcTypeUtils.isJustGlobal(source) && GlobalJdbcTypeUtils.isJustGlobal(target))
				|| (GlobalJdbcTypeUtils.checkOtherConnections(source)
						&& GlobalJdbcTypeUtils.checkOtherConnections(target));
	}

}

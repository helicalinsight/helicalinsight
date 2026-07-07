package com.helicalinsight.adhoc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIHcrConnectionsGlobal;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;

@Component
@Scope("prototype")
public class HCRGlobalConnectionSaveHandler extends AbstractHCRConnectionSave{
	
	@Autowired
	GlobalConnectionService globalConServiceDB; 

	@Override
	public void saveConnection(JsonObject connDetails) {
		HIHcrConnectionsGlobal hiHcrConnectionsGlobal=new HIHcrConnectionsGlobal();
		GlobalConnections globalConnections=globalConServiceDB.findGlobalConnectionById(Integer.parseInt(connDetails.get("globalId").getAsString()),false);
		hiHcrConnectionsGlobal.setHiHcrConnections(getHiHcrConnections());
		hiHcrConnectionsGlobal.setGlobalConnections(globalConnections);
		globalConServiceDB.addHiHcrGlobalConnection(hiHcrConnectionsGlobal);
	}

}

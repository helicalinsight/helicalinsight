package com.helicalinsight.adhoc.services;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIHcrConnectionsEfwd;

@Component
@Scope("prototype")
public class HCREfwConnectionSaveHandler extends AbstractHCRConnectionSave{

	@Override
	public void saveConnection(JsonObject connectionObj) {
		HIHcrConnectionsEfwd hiHcrConnectionsEfwd=new HIHcrConnectionsEfwd();
		HIEfwdConnection hiEfwdConnection=efwServiceDB.findConnectionById(connectionObj.get("efwdId").getAsString(),false);
		hiHcrConnectionsEfwd.setHiEfwdConnection(hiEfwdConnection);
		hiHcrConnectionsEfwd.setHiHcrConnections(getHiHcrConnections());
		efwServiceDB.addHiHcrEfwdConnection(hiHcrConnectionsEfwd);
	}

}

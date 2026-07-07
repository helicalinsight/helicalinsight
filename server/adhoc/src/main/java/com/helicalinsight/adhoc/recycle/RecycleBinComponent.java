package com.helicalinsight.adhoc.recycle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.recycle.action.RecycleBinAction;
import com.helicalinsight.adhoc.recycle.factory.RecycleBinActionFactory;
import com.helicalinsight.efw.serviceframework.IComponent;


/**
* The {@code RecycleBinComponent} class is part of the recycling system, handling actions
* related to the recycle bin functionality within the application. It implements the
* {@code IComponent} interface, allowing it to execute specific recycle bin actions
* based on the provided JSON form data.
*/
public class RecycleBinComponent implements IComponent {

	/**
	* Executes the component action based on the specified JSON form data. It parses the
	* JSON data to determine the action to perform and then delegates the action to the
	* appropriate {@code RecycleBinAction} instance.
	*
	* @param jsonFormData 	 JSON string containing form data for recycle bin actions provides action.
	* @return String The result of the recycle bin action performed.
	*/
	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
		String action = formData.get("action").getAsString();
		RecycleBinAction recycleBinAction = RecycleBinActionFactory.getInstance(action);
		recycleBinAction.setFormData(formData); 
		return recycleBinAction.performAction();
	}
	
	
	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}


}

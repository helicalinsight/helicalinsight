package com.helicalinsight.efw.jasperintegration;

import org.junit.Test;

import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import com.helicalinsight.efw.io.delete.DeleteOperationHandler;

import net.sf.json.JSONArray;

public class DeleteOperationHandlerTest {

	//@Test(expected = OperationFailedException.class)
	public void testHandle() throws UnSupportedRuleImplementationException {
		DeleteOperationHandler deleteOperationHandler = new DeleteOperationHandler();
		JSONArray array = new JSONArray();
		array.add("sourceArray");
		deleteOperationHandler.handle(array.toString());
	}
}

package com.helicalinsight.efw.jasperintegration.bandcontents;

import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.jasperintegration.CompileRequest;
import com.helicalinsight.efw.jasperintegration.HCRCompiler;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JRDesignSubreportParameter;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OverflowType;
import static com.helicalinsight.datasource.HCRUtils.*;

@Component
public class HCRSubReport implements IHCRBandContents {
	
	
	private final HCRCompiler hcrCompiler;
	
	public HCRSubReport(HCRCompiler hcrCompiler) {
		this.hcrCompiler = hcrCompiler;
	}
	

	@Override
	public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
		addSubReportIfAny(band, bandJson, jasperDesign);
	}

	private void addSubReportIfAny(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
		if (bandJson.has("subReport")) {
			JsonArray subReportArray = GsonUtility.optJsonArray(bandJson, "subReport");
			if (subReportArray != null) {
				for (JsonElement jsonElement : subReportArray) {
					JsonObject eachSubReportJson = jsonElement.getAsJsonObject();
					JRDesignSubreport subReport = prepareSubReport(jasperDesign, bandJson,  eachSubReportJson);
					if(subReport != null) {
						band.addElement(subReport);
					}
				}
			}
		}
	}

	private JRDesignSubreport prepareSubReport(JasperDesign jasperDesign, JsonObject formData,  JsonObject subReportJson) {

		JRDesignSubreport subReport = new JRDesignSubreport(null);

		configureComponentElementProperties(subReport, subReportJson);

		
		if (subReportJson.has("connectionExpression")) {
			JRDesignExpression expression = HCRUtils.getExpressionFromJson(subReportJson, "connectionExpression");
			subReport.setConnectionExpression(expression);
		}
		
		else if (subReportJson.has("datasourceExpression")) {
			JRDesignExpression  expression = HCRUtils.getDataSetExpressionFromJson(subReportJson, "datasourceExpression");
			subReport.setDataSourceExpression(expression);
		}
		else {
			JRDesignExpression datasetExpression = new JRDesignExpression();
			datasetExpression.setText("new net.sf.jasperreports.engine.JREmptyDataSource()");
			subReport.setDataSourceExpression(datasetExpression);
		}
		
		Integer subreportResourceId  =  subReportJson.get("subReportExpression").getAsInt();
		
		Boolean generateXML = GsonUtility.optBooleanValue(formData, "generateXML", false);
		
		CompileRequest compileRequest  = new CompileRequest(generateXML, subreportResourceId);
		
		String compiledFilePath =  hcrCompiler.compile(compileRequest);
		
		if ( compiledFilePath.isBlank()) {
			return null;
		}
		
		JRDesignExpression subreportExpression = new JRDesignExpression();
		subreportExpression.setText("\""+compiledFilePath+"\"");
		
		subReport.setExpression(subreportExpression);
		subReport.setRunToBottom(GsonUtility.optBooleanValue(subReportJson, "runToBottom", true));
		subReport.setUsingCache(GsonUtility.optBooleanValue(subReportJson, "usingCache", true));
		subReport.setOverflowType(OverflowType.valueOf(GsonUtility.optStringValue(subReportJson, "overFlowType", "STRETCH")));
		
		HCRUtils.applyStyleReference(subReportJson, subReport::setStyleNameReference);
		
		if(subReportJson.has("parameters")) {
			JsonArray parameterArray = subReportJson.getAsJsonArray("parameters");
			for(JsonElement element : parameterArray ) {
				JsonObject parameter = element.getAsJsonObject();
				JRDesignExpression subreportParameterExpression = HCRUtils.getExpressionFromJson(parameter, "expression");
				JRDesignSubreportParameter subreportParameter = new JRDesignSubreportParameter();
				subreportParameter.setName(parameter.get("name").getAsString());
				subreportParameter.setExpression(subreportParameterExpression);
				try {
					subReport.addParameter(subreportParameter);
				} catch (JRException e) {
					throw new EfwServiceException("Error occurred while setting the subreport parameter : " + e.getMessage());
				}
			}
		}
		
		
		subReport.setPrintInFirstWholeBand(GsonUtility.optBooleanValue(subReportJson, "printInFirstWholeBand", false));
		subReport.setPrintRepeatedValues(GsonUtility.optBooleanValue(subReportJson, "printRepeatedValues", false));
		subReport.setRemoveLineWhenBlank(GsonUtility.optBooleanValue(subReportJson, "removeLineWhenBlank", false));
		
		JRDesignExpression whenExpression = HCRUtils.getExpressionFromJson(subReportJson, "printWhenExpression");
		subReport.setPrintWhenExpression(whenExpression);
		subReport.setPrintWhenDetailOverflows(GsonUtility.optBooleanValue(subReportJson, "printWhenDetailsOverflow", false));
		prepareGroupChanges(jasperDesign, subReportJson, subReport);
		return subReport;
	}

	private void configureComponentElementProperties(JRDesignSubreport subReport, JsonObject formData) {

		JsonObject componentElementProperties = GsonUtility.optJsonObject(formData, PROPERTY_COMPONENT_ELEMENT_PROPS);

		if (componentElementProperties == null)
			return;

		subReport.setHeight(componentElementProperties.get("height").getAsInt());
		subReport.setWidth(componentElementProperties.get("width").getAsInt());
		subReport.setX(componentElementProperties.get("X").getAsInt());
		subReport.setY(componentElementProperties.get("Y").getAsInt());
		setElementPositionType(componentElementProperties, subReport);
		prepareStretchType(componentElementProperties, subReport);
		subReport.setMode(ModeEnum.getByName(componentElementProperties.get("mode").getAsString()));
		subReport.setForecolor(HCRUtils.getColorFromJson(componentElementProperties, PROPERTY_FORE_COLOR));
		subReport.setBackcolor(HCRUtils.getColorFromJson(componentElementProperties, PROPERTY_BACK_COLOR));
	}

}

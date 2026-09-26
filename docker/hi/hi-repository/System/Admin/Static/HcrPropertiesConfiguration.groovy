import com.helicalinsight.export.ExportUtils;
import com.helicalinsight.efw.ApplicationProperties;
import java.io.File;
import groovy.json.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
String parentPath = ApplicationProperties.getInstance().getSystemDirectory() + File.separator + "Admin" + File.separator;
String diagram = ExportUtils.getFileAsString(parentPath+ "Static"+ File.separator + "diagramCanvas"+ExportUtils.JSON_EXTENSION)
String textField = ExportUtils.getFileAsString(parentPath+ "Static"+ File.separator + "textField"+ExportUtils.JSON_EXTENSION)
String field = ExportUtils.getFileAsString(parentPath+"Static"+ File.separator +"field"+ExportUtils.JSON_EXTENSION)
String image = ExportUtils.getFileAsString(parentPath+"Static"+ File.separator +"image"+ExportUtils.JSON_EXTENSION)
String table = ExportUtils.getFileAsString(parentPath+"Static"+ File.separator +"table"+ExportUtils.JSON_EXTENSION)
String crosstab = ExportUtils.getFileAsString(parentPath+"Static"+ File.separator +"crosstab"+ExportUtils.JSON_EXTENSION)
String chart = ExportUtils.getFileAsString(parentPath+"Static"+ File.separator +"chart"+ExportUtils.JSON_EXTENSION)
String subreport = ExportUtils.getFileAsString(parentPath+"Static"+ File.separator +"subReport"+ExportUtils.JSON_EXTENSION)
String customVisualization = ExportUtils.getFileAsString(parentPath+"Static"+ File.separator +"customVisualization"+ExportUtils.JSON_EXTENSION)
String basicLine=ExportUtils.getFileAsString(parentPath+"Static"+ File.separator +"basicLine"+ExportUtils.JSON_EXTENSION)
String tooltipInfo=ExportUtils.getFileAsString(parentPath+"Static"+ File.separator +"tooltipInfo"+ExportUtils.JSON_EXTENSION)


JSONObject diagramJson = JSONObject.fromObject(diagram);
JSONObject textFieldJson = JSONObject.fromObject(textField);
JSONObject fieldJson = JSONObject.fromObject(field);
JSONObject basicLineJson = JSONObject.fromObject(basicLine);
JSONObject imageJson = JSONObject.fromObject(image);
JSONObject tableJson = JSONObject.fromObject(table);
JSONObject chartJson = JSONObject.fromObject(chart);
JSONObject subreportJson = JSONObject.fromObject(subreport);
JSONObject crosstabJson = JSONObject.fromObject(crosstab);
JSONObject customVisualizationJson = JSONObject.fromObject(customVisualization);
JSONObject tooltipInfoJson = JSONObject.fromObject(tooltipInfo);
JSONObject finalObj = new JSONObject();
finalObj.put("diagramCanvas",diagramJson);
finalObj.put("textField",textFieldJson);
finalObj.put("field",fieldJson);
finalObj.put("basicLine",basicLineJson);
finalObj.put("image",imageJson);
finalObj.put("table",tableJson);
finalObj.put("crosstab",crosstabJson);
finalObj.put("customVisualization",customVisualizationJson);
finalObj.put("chart",chartJson);
finalObj.put("subReport",subreportJson);
finalObj.put("tooltipInfo",tooltipInfoJson);
return finalObj
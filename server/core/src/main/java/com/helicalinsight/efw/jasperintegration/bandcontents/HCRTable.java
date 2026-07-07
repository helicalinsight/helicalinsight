package com.helicalinsight.efw.jasperintegration.bandcontents;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;
import net.sf.jasperreports.components.table.DesignCell;
import net.sf.jasperreports.components.table.GroupCell;
import net.sf.jasperreports.components.table.StandardColumn;
import net.sf.jasperreports.components.table.StandardGroupCell;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.engine.component.ComponentKey;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

/**
 * Created by author on 11/13/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
public class HCRTable implements IHCRBandContents {
    
    @Autowired
    private HCRTextField hcrTextField;
    private JasperDesign jasperDesign;
    
    @Autowired
    private HCRImage hcrImage;


    @Override
    public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
        this.jasperDesign = jasperDesign;
        addTableIfAny(band, bandJson);
    }

    private void addTableIfAny(JRDesignBand band, JsonObject bandJson) {
        if (bandJson.has("table")) {
            JsonArray tableArray = GsonUtility.optJsonArray(bandJson,"table");
            if (tableArray != null) {
                for (int index = 0; index < tableArray.size(); index++) {
                    JsonObject eachTableObject = tableArray.get(index).getAsJsonObject();
                    JRDesignComponentElement jrDesignComponentElement = prepareTable(eachTableObject);
                    band.addElement(jrDesignComponentElement);
                }
            }
        }
    }

    private JRDesignComponentElement prepareTable(JsonObject eachTableJson) {

        JRDesignComponentElement componentElement = new JRDesignComponentElement();
        //prepare Table
        StandardTable table = new StandardTable();
        //prepare Entire Column
        JsonArray columnsArray = eachTableJson.getAsJsonArray("columns");
        for (int index = 0; index < columnsArray.size(); index++) {
            JsonObject eachColumnJson = columnsArray.get(index).getAsJsonObject();
            StandardColumn column = new StandardColumn();
            column.setWidth(eachColumnJson.get("width").getAsInt());
            if (eachColumnJson.has("tableHeader"))
                prepareTableHeader(eachColumnJson, column);
            if ( eachColumnJson.has("tableGroupHeaders"))
            	prepareTableGroups(eachColumnJson, column, "tableGroupHeaders", column::setGroupHeaders);
            if (eachColumnJson.has("columnHeaderOfTable"))
                prepareColumnHeader(eachColumnJson, column);
            if (eachColumnJson.has("columnFooterOfTable"))
                prepareColumnFooter(eachColumnJson, column);
            if (eachColumnJson.has("columnData"))
                prepareColumnDetail(eachColumnJson, column);
            if ( eachColumnJson.has("tableGroupFooters"))
            	prepareTableGroups(eachColumnJson, column, "tableGroupFooters", column::setGroupFooters);
            if (eachColumnJson.has("tableFooter"))
                prepareTableFooter(eachColumnJson, column);

            table.addColumn(column);
        }
        

        // JRFillDatasetRun run = new JRFillDatasetRun();
        JsonObject dataSetRunJson = eachTableJson.getAsJsonObject("dataSetRun");
        JRDesignDatasetRun datasetRun = HCRUtils.configureSubDatasetRun(dataSetRunJson);
        table.setDatasetRun(datasetRun);
        
        HCRUtils.configureComponentElementProperties(componentElement, eachTableJson);
        HCRUtils.applyStyleReference(eachTableJson, componentElement::setStyleNameReference);
        componentElement.setComponentKey(new ComponentKey("http://jasperreports.sourceforge.net/jasperreports/components", "jr", "table"));
        componentElement.setComponent(table);
        

        return componentElement;
    }

    private void prepareColumnHeader(JsonObject eachColumnJson, StandardColumn column) {
        //prepare column header
        JsonObject columnHeaderJson = eachColumnJson.getAsJsonObject("columnHeaderOfTable");
        
        if(!isEnabled(columnHeaderJson)) return ;
        
        DesignCell headerCell = getDesignStaticCell(columnHeaderJson);
        column.setColumnHeader(headerCell);
    }

    private void prepareTableHeader(JsonObject eachColumnJson, StandardColumn column) {
        //prepare column header
        JsonObject tableHeaderJson = eachColumnJson.getAsJsonObject("tableHeader");
        
        if(!isEnabled(tableHeaderJson)) return ;
        
        DesignCell headerCell = getDesignStaticCell(tableHeaderJson);
        column.setTableHeader(headerCell);
    }
    
    private void prepareTableGroups(
    	JsonObject eachColumnJson, 
    	StandardColumn column,
    	String jsonKey,
    	Consumer<List<GroupCell>> groupSetter ) {
    	
    	JsonArray groupArray= eachColumnJson.getAsJsonArray(jsonKey);
    	
    	if ( groupArray.isEmpty()) return ;
    	
    	 List<GroupCell> groupCells = new ArrayList<>();
    	
    	 for(JsonElement groupElement : groupArray ) {
    			JsonObject group = groupElement.getAsJsonObject();
    			JsonElement groupNameElement = group.get("name");
    			if ( groupNameElement == null) continue ;
    			String groupName = group.get("name").getAsString();
    			DesignCell groupCell = getDesignStaticCell(group);
    			GroupCell standatdGroupCell = new StandardGroupCell(groupName, groupCell);
    			groupCells.add(standatdGroupCell);
    	}
    	 groupSetter.accept(groupCells);
    }
   
    
    private void prepareTableFooter(JsonObject eachColumnJson, StandardColumn column) {
        //prepare column header
        JsonObject tableFooterJson = eachColumnJson.getAsJsonObject("tableFooter");
        
        if(!isEnabled(tableFooterJson)) return ;
        
        DesignCell headerCell = getDesignStaticCell(tableFooterJson);
        column.setTableFooter(headerCell);
    }
    
    private Boolean isEnabled(JsonObject jsonObject) {
    	 return GsonUtility.optBooleanValue(jsonObject, "enabled", true);
    }

    private DesignCell getDesignStaticCell(JsonObject staticCellJson) {
        DesignCell headerCell = new DesignCell();
        HCRUtils.applyStyleReference(staticCellJson, headerCell::setStyleNameReference);
        headerCell.setHeight(GsonUtility.optInt(staticCellJson, "height"));
        if (staticCellJson.has("rowSpan"))
            headerCell.setRowSpan(GsonUtility.optInt(staticCellJson,"rowSpan"));
        if (staticCellJson.has("textField")) {
            JsonArray textFields =  GsonUtility.optJsonArray(staticCellJson, "textField");
            for(JsonElement element : textFields) {
            	JsonObject staticTextJson = element.getAsJsonObject();
            	JRDesignTextField jrDesignTextField = hcrTextField.prepareTextField(staticTextJson, jasperDesign);
                headerCell.addElement(jrDesignTextField);
            }
        }
        
        if (staticCellJson.has("image")) {
            JsonArray images =  GsonUtility.optJsonArray(staticCellJson, "image");
            for(JsonElement element : images) {
            	JsonObject imageJson = element.getAsJsonObject();
            	JRDesignImage jrDesignImage = hcrImage.prepareImage(jasperDesign, imageJson);
                headerCell.addElement(jrDesignImage);
            }
        }
        
        return headerCell;
    }


    private void prepareColumnDetail(JsonObject eachColumnJson, StandardColumn column) {
        JsonObject columnDataJson = eachColumnJson.getAsJsonObject("columnData");
        
        if(!isEnabled(columnDataJson)) return ;
        
        DesignCell headerCell = getDesignStaticCell(columnDataJson);
        column.setDetailCell(headerCell);
    }

    private void prepareColumnFooter(JsonObject eachColumnJson, StandardColumn column) {
        //prepare column footer
        JsonObject columnFooterJson = eachColumnJson.getAsJsonObject("columnFooterOfTable");
        
        if(!isEnabled(columnFooterJson)) return ;
        
        DesignCell footerCell = getDesignStaticCell(columnFooterJson);
        column.setColumnFooter(footerCell);
    }
}

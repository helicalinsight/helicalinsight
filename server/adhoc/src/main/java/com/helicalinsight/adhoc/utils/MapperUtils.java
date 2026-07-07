package com.helicalinsight.adhoc.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataTables;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@Component
public class MapperUtils {
	
	public Table mapToTable(HIMetadataTables dbTables) {
		Table table = ApplicationContextAccessor.getBean(Table.class);
        table.setName(dbTables.getTableName());
        table.setAliasName(dbTables.getTableAliasName());
        table.setId(""+dbTables.getId());
        table.setDbId(""+dbTables.getHiMetadataDatabases().getId());
        table.setType(dbTables.getView() ? "view" : null);
        table.setOriginalName(dbTables.getOriginalName());
       return table;
	}
	
	public Column mapToColumn(HIMetadataColumns dbCol) {
		Column column = ApplicationContextAccessor.getBean(Column.class);
		column.setAliasName(dbCol.getColumnAliasName());
		column.setDefaultFunction(dbCol.getDefaultFunction());
		column.setId(""+dbCol.getId());
		column.setType(dbCol.getColumn_type());
		column.setOriginalName(dbCol.getColumnName());
		column.setName(dbCol.getColumnName());
		column.setOriginalName(dbCol.getOriginalName());
		return column;
	}

	public void mapToColumnList(HIMetadataColumns dbCol,List<Column> columnList) {
		Column column = mapToColumn(dbCol);
		columnList.add(column);
	}

}

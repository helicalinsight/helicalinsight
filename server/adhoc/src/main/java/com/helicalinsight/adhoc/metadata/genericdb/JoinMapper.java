package com.helicalinsight.adhoc.metadata.genericdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.LeftTable;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.RightTable;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataRelationships;
import com.helicalinsight.admin.model.HIMetadataTables;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;


/**
 * The JoinMapper class maps JSON representation of a join to a Join object and vice versa.
 */
@Component
public class JoinMapper {

	@Autowired
	RelationshipsTemplate rTemplate;
	/**
     * Maps a JSON string representation of a join to a Join object.
     * 
     * @param stringJoin 		 JSON string consists of  it, type, operator.
     * @return the Join object mapped from the JSON string
     */
	public Join map(String stringJoin) {

		/**
		 * TODO : Enhancements 1 . Remove table , column from joins use tableId ,
		 * columnId instead.
		 * 
		 */

		ObjectNode json = JacksonUtility.fromObject(stringJoin);
		Join join = ApplicationContextAccessor.getBean(Join.class);
		join.setId(json.path("id").asText());
		join.setType(json.get("type").asText());
		join.setOperator(json.get("operator").asText());

		LeftTable leftTable = ApplicationContextAccessor.getBean(LeftTable.class);
		ObjectNode jsonLeftTable = json.with("left");
		String table = jsonLeftTable.get("table").asText();
		leftTable.setTable(table);
		leftTable.setColumn(jsonLeftTable.get("column").asText());
		leftTable.setId(jsonLeftTable.get("tableId").asText());
		join.setLeftTable(leftTable);

		RightTable rightTable = ApplicationContextAccessor.getBean(RightTable.class);
		ObjectNode jsonRightTable = json.with("right");
		String referenceTable = jsonRightTable.get("table").asText();
		rightTable.setTable(referenceTable);
		rightTable.setColumn(jsonRightTable.get("column").asText());
		rightTable.setId(jsonRightTable.get("tableId").asText());
		join.setRightTable(rightTable);
		String databaseId = jsonLeftTable.get("dbId").asText();
		leftTable.setDbId(databaseId);
		join.setDatabaseId(databaseId);
		String referenceDbId = jsonRightTable.get("dbId").asText();
		join.setReferenceDatabaseId(referenceDbId);
		rightTable.setDbId(referenceDbId);
		return join;
	}
	/**
     * Maps a HIMetadataRelationships object to a Join object.
     * 
     * @param item 				 HIMetadataRelationships object provides id, type, operator etc.
     * @return the Join object mapped from the HIMetadataRelationships object
     * @throws MetadataRetrievalException if the join columns are empty or malformed
     */
	public Join map(HIMetadataRelationships item) {
		Join join = ApplicationContextAccessor.getBean(Join.class);
		join.setId("" + item.getId());
		join.setOperator(item.getOperator());
		join.setType(item.getJoinType());
		LeftTable leftTable = ApplicationContextAccessor.getBean(LeftTable.class);
		RightTable rightTable = ApplicationContextAccessor.getBean(RightTable.class);
		HIMetadataColumns leftMetadataColumns = item.getLeftMetadataColumns();
		HIMetadataColumns rightMetadataColumns = item.getRightMetadataColumns();
		if (leftMetadataColumns == null || rightMetadataColumns == null) {
			throw new MetadataRetrievalException("The joins columns are either empty or malformed");
		}
		HIMetadataTables hiMetadataTables = leftMetadataColumns.getHiMetadataTables();
		leftTable.setTable(hiMetadataTables.getTableName());
		leftTable.setColumn(leftMetadataColumns.getColumnName());
		leftTable.setId(""+hiMetadataTables.getId());
		leftTable.setDbId("" + hiMetadataTables.getHiMetadataDatabases().getId());
		HIMetadataTables hiMetadataRightTables = rightMetadataColumns.getHiMetadataTables();
		rightTable.setTable(hiMetadataRightTables.getTableName());
		rightTable.setColumn(rightMetadataColumns.getColumnName());
		rightTable.setId(""+hiMetadataRightTables.getId());
		rightTable.setDbId("" + hiMetadataRightTables.getHiMetadataDatabases().getId());
		Relationship relationship = ApplicationContextAccessor.getBean(Relationship.class);
		relationship.setTable(hiMetadataTables.getTableName());
		relationship.setReferenceTable(hiMetadataRightTables.getTableName());
		join.setLeftTable(leftTable);
		join.setRightTable(rightTable);
		return join;
	}
}

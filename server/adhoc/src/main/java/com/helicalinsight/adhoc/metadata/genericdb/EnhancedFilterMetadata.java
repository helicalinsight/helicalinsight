package com.helicalinsight.adhoc.metadata.genericdb;

import com.helicalinsight.adhoc.genericsql.RestrictedMetadata;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
/**
 * Provides functionality to filter metadata based on restricted criteria.
 * This class allows filtering metadata including tables, columns, and joins
 * based on restrictions specified in the restricted metadata.
 */
public class EnhancedFilterMetadata {

	private Metadata metadata;
	private String mode = "metadata";

	public EnhancedFilterMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public EnhancedFilterMetadata(Metadata metadata, String mode) {
		this(metadata);
		this.mode = mode;
	}
	/**
     * Filters the metadata based on restricted criteria specified in the restricted metadata.
     * @param restrictedMetadata 		 restricted metadata containing filtering criteria.
     */
	public void filter(RestrictedMetadata restrictedMetadata) {
		Tables tables = this.metadata.getDatabase().getTables();
		if (tables != null) {
			List<Table> tableList = tables.getTableList();
			filter(tableList, restrictedMetadata);
			Connections connections = this.metadata.getConnections();
			if (connections != null) {
				List<ConnectionDatabase> cdbList = connections.getConnectionDatabase();
				cdbList.forEach(cdb -> filter(cdb.getDatabase().getTables().getTableList(), restrictedMetadata));
			}
		}
		eliminateJoins(restrictedMetadata);
	}
	/**
     * Filters the list of tables based on restricted criteria specified in the restricted metadata.
     *
     * @param tableList          		 list of tables to be filtered.
     * @param restrictedMetadata 		 restricted metadata containing restricted columns.
     */
	public void filter(List<Table> tableList, RestrictedMetadata restrictedMetadata) {

		List<String> blackListedTables = restrictedMetadata.getRestrictedTables();
		ListIterator<Table> iterator = tableList.listIterator();
		if (!tableList.isEmpty()) {
			while (iterator.hasNext()) {
				Table table = iterator.next();
				String expression = table.getId();
				if (blackListedTables.contains(expression)) {
					iterator.remove();
				} else {
					List<String> columnExpressions = restrictedMetadata.getRestrictedColumns();
					if (!columnExpressions.isEmpty()) {
						removeColumn(table, columnExpressions);
					}
				}
			}
		}
	}
	/**
     * Removes columns from a table based on restricted criteria specified in the restricted metadata.
     *
     * @param table             		 table from which columns need to be removed.
     * @param columnExpressions 		 list of column expressions to be removed.
     */
	private void removeColumn(Table table, List<String> columnExpressions) {
		Columns columns = table.getColumns();
		if (columns != null) {
			ListIterator<Column> columnListIterator = columns.getColumn().listIterator();
			while (columnListIterator.hasNext()) {
				Column column = columnListIterator.next();
				String expression = column.getId();
				if (columnExpressions.contains(expression)) {
					columnListIterator.remove();
				}
			}
		}
	}

	/**
	 * Eliminates joins from metadata based on restricted criteria specified in the
	 * restricted metadata.
	 *
	 * @param restrictedMetadata 		 restricted metadata containing restricted table and columns
	 *                                  
	 */
	private void eliminateJoins(RestrictedMetadata restrictedMetadata) {

		Relationships relationships = this.metadata.getDatabase().getRelationships();
		ExternalRelationships external = this.metadata.getExternalRelationships();
		List<Relationship> listOfRelations = relationships != null ? relationships.getListOfRelations() : null;
		listOfRelations = listOfRelations == null ? external.getListOfRelations() : listOfRelations;

		if (listOfRelations == null) {
			relationships = ApplicationContextAccessor.getBean(Relationships.class);
			listOfRelations = new ArrayList<>();
			relationships.setListOfRelations(listOfRelations);
		}
		if (external != null) {
			List<Relationship> externalList = external.getListOfRelations();
			if (externalList != null) {
				listOfRelations.addAll(externalList);
			}
		}

		// internal connection joins

		Connections connections = this.metadata.getConnections();
		if (connections != null) {
			List<ConnectionDatabase> cdbList = connections.getConnectionDatabase();
			for (ConnectionDatabase cdb : cdbList) {
				Relationships insideRelations = cdb.getDatabase().getRelationships();
				if (insideRelations != null) {
					listOfRelations.addAll(insideRelations.getListOfRelations());
				}
			}
		}

		if (listOfRelations == null || listOfRelations.isEmpty()) {
			return;
		}

		List<String> restrictedTables = restrictedMetadata.getRestrictedTables();
		Set<String> restrictedColumns = new HashSet<>(restrictedMetadata.getRestrictedColumns());
		ListIterator<Relationship> relationshipListIterator = listOfRelations.listIterator();

		while (relationshipListIterator.hasNext()) {
			Relationship relationship = relationshipListIterator.next();
			List<Join> joinsList = relationship.getJoin();
			if ("query".equalsIgnoreCase(this.mode)) {
				queryMode(restrictedTables, relationshipListIterator, joinsList, restrictedColumns);
			} else {
				metadataMode(restrictedTables, joinsList, restrictedColumns);
			}
		}

	}

	/**
	 * Filters joins in query mode based on restricted criteria specified in the
	 * restricted metadata.
	 *
	 * @param restrictedTables     list of restricted tables.
	 * @param relationshipIterator relationships iterator which removes columns.
	 * @param joinsList            list of joins having left and right joins
	 * @param restrictedColumns    set of restricted columns to be removed.
	 */
	private void queryMode(List<String> restrictedTables, ListIterator<Relationship> relationshipListIterator,
			List<Join> joinsList, Set<String> restrictedColumns) {

		for (Join join : joinsList) {
			LeftTable left = join.getLeftTable();
			RightTable right = join.getRightTable();
			if (restrictedTables.contains(left.getTableId()) || restrictedTables.contains(right.getTableId())) {
				relationshipListIterator.remove();
			}
		}

		if (joinsList.size() == 1) {
			Join join = joinsList.get(0);
			String leftColumn = join.getLeftTable().getColumnId();
			String rightColumn = join.getRightTable().getColumnId();

			if (restrictedColumns.contains(leftColumn) || restrictedColumns.contains(rightColumn)) {
				relationshipListIterator.remove();
			}
		} else {
			ListIterator<Join> joinListIterator = joinsList.listIterator();
			while (joinListIterator.hasNext()) {
				Join join = joinListIterator.next();
				String leftColumn = join.getLeftTable().getColumnId();
				String rightColumn = join.getRightTable().getColumnId();

				if (restrictedColumns.contains(leftColumn) || restrictedColumns.contains(rightColumn)) {
					joinListIterator.remove();
				}
			}

			if (joinsList.isEmpty()) {
				relationshipListIterator.remove();
			}
		}
	}

	/**
	 * Filters joins in metadata mode based on restricted criteria specified in the
	 * restricted metadata.
	 *
	 * @param restrictedTables  list of restricted tables.
	 * @param joinsList         list of joins whic provides left and right join.
	 * @param restrictedColumns set of restricted columns.
	 */
	private void metadataMode(List<String> restrictedTables, List<Join> joinsList, Set<String> restrictedColumns) {
		for (Join join : joinsList) {
			if (join.getLeftTable() != null && join.getRightTable() != null) {
				String leftTableExpression = join.getLeftTable().getTableId();
				String rightTableExpression = join.getRightTable().getTableId();

				String leftColumnExpression = join.getLeftTable().getColumnId();
				String rightColumnExpression = join.getRightTable().getColumnId();

				if ((restrictedTables.contains(leftTableExpression) || restrictedTables.contains(rightTableExpression))
						|| restrictedColumns.contains(leftColumnExpression)
						|| restrictedColumns.contains(rightColumnExpression)) {
					// To preserve the order, set all except id to null
					join.setLeftTable(null);
					join.setOperator(null);
					join.setRightTable(null);
					join.setType(null);
				}
			}
		}
	}
}
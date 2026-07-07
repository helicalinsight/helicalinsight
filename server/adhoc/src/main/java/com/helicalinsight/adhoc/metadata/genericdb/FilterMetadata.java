package com.helicalinsight.adhoc.metadata.genericdb;

import com.helicalinsight.adhoc.genericsql.RestrictedMetadata;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.adhoc.metadata.security.MetadataSecurity;
import com.helicalinsight.adhoc.metadata.security.SecurityExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * The FilterMetadata class is responsible for filtering metadata based on security
 * restrictions applied to tables and columns. It eliminates restricted tables,
 * columns, and relationships from the metadata, ensuring data access compliance.
 *
 * Created  on 9/14/2015.
 * @author Somen
 * @author Rajasekhar
 */
public class FilterMetadata {

    private static final Logger logger = LoggerFactory.getLogger(FilterMetadata.class);

    private Metadata metadata;
    private String mode = "metadata";

    public FilterMetadata(Metadata metadata) {
        this.metadata = metadata;
        if (logger.isDebugEnabled()) {
            logger.debug("Processing the restrictions applied over the metadata");
        }
    }

    public FilterMetadata(Metadata metadata, String mode) {
        this(metadata);
        this.mode = mode;
    }
    /**
     * Filters the metadata based on security restrictions applied to tables and columns.
     */
    public void filter() {
        MetadataSecurity access = this.metadata.getMetadataSecurity();
        if (access != null) {
            List<SecurityExpression> expressions = access.getExpressions();

            RestrictedMetadata restrictedMetadata = new RestrictedMetadata(expressions);
            if("new".equalsIgnoreCase(restrictedMetadata.getStrategy())){
            	logger.debug("Redirecting request to EnhancedMetadataFilter");
            	new  EnhancedFilterMetadata(this.metadata,mode).filter(restrictedMetadata);
            	return ;
            }
            List<String> blackListedTables = restrictedMetadata.getRestrictedTables();
            

            Tables tables = this.metadata.getDatabase().getTables();
            if (tables != null) {
                List<Table> tableList = tables.getTableList();
                if (tableList != null) {
                    ListIterator<Table> tableListIterator = tableList.listIterator();
                    while (tableListIterator.hasNext()) {
                        Table table = tableListIterator.next();
                        String tableName = table.getName();
                        String aliasName = table.getAliasName();
                        if (blackListedTables.contains(tableName) || blackListedTables.contains(aliasName)) {
                            tableListIterator.remove();
                        } else {
                            List<String> columnNames = restrictedMetadata.restrictedColumnsFromTable(tableName,
                                    aliasName);
                            if (columnNames.size() > 0) {
                                eliminateColumn(table, columnNames);
                            }
                        }
                    }
                    eliminateRelationships(restrictedMetadata);
                }
            }
        }
    }
    /**
     * Eliminates restricted columns from the given table.
     *
     * @param table       		 table from which to eliminate columns.
     * @param columnNames 		 list of column names to eliminate.
     */
    private void eliminateColumn(Table table, List<String> columnNames) {
        Columns columns = table.getColumns();
        if (columns != null) {
            ListIterator<Column> columnListIterator = columns.getColumn().listIterator();
            while (columnListIterator.hasNext()) {
                Column column = columnListIterator.next();
                String columnAliasName = column.getAliasName();
                String columnName = column.getName();
                if (columnNames.contains(columnAliasName) || columnNames.contains(columnName)) {
                    columnListIterator.remove();
                }
            }
        }
    }
    /**
     * Eliminates restricted relationships from the metadata.
     *
     * @param restrictedMetadata 		 restricted metadata object containing security restrictions.
     */
    private void eliminateRelationships(RestrictedMetadata restrictedMetadata) {
        Relationships relationships = this.metadata.getDatabase().getRelationships();
        List<Relationship> listOfRelations;
        if (relationships != null && relationships.getListOfRelations() != null) {
            listOfRelations = relationships.getListOfRelations();
        } else {
            return;
        }

        List<String> restrictedTables = restrictedMetadata.getRestrictedTables();
        ListIterator<Relationship> relationshipListIterator = listOfRelations.listIterator();

        /*If any restricted table is found in the relationship, then it should be removed from the available
        relationships. If one or more restricted columns from a table are found in the Relationship->Joins,
        then only that join should be removed. In case, if only one join is found in the relationship, the relationship
        itself can be removed. However, in case of composite joins only that join which consists of the restricted
        column has to be removed.*/

        while (relationshipListIterator.hasNext()) {
            Relationship relationship = relationshipListIterator.next();

            String leftTable = relationship.getTable();
            String rightTable = relationship.getReferenceTable();

            List<Join> joinsList = relationship.getJoin();
            List<String> listOfColumns = restrictedMetadata.restrictedColumnsFromTable(leftTable, null);
            //For both left and right columns one Set is enough
            Set<String> restrictedColumns = new HashSet<>(listOfColumns);
            restrictedColumns.addAll(restrictedMetadata.restrictedColumnsFromTable(rightTable, null));

            if ("query".equalsIgnoreCase(this.mode)) {
                queryMode(restrictedTables, relationshipListIterator, leftTable, rightTable, joinsList,
                        restrictedColumns);
            } else {
                //mode is metadata.  So keep everything except the join ids
                metadataMode(restrictedTables, leftTable, rightTable, joinsList, restrictedColumns);
            }
        }
    }
    /**
     * query mode removes the restricted Columns , restricted tables.
     *
     * @param restrictedTables         		 list of restricted tables.
     * @param relationshipListIterator 		 iterator for relationships.
     * @param leftTable                		 left table name.
     * @param rightTable               		 right table name.
     * @param joinsList                		 list of joins in the relationship.
     * @param restrictedColumns        		 set of restricted columns.
     */
    private void queryMode(List<String> restrictedTables, ListIterator<Relationship> relationshipListIterator,
                           String leftTable, String rightTable, List<Join> joinsList, Set<String> restrictedColumns) {
        if (restrictedTables.contains(leftTable) || restrictedTables.contains(rightTable)) {
            relationshipListIterator.remove();
            return;
        }

        if (joinsList.size() == 1) {
            Join join = joinsList.get(0);
            String leftColumn = join.getLeftTable().getColumn();
            String rightColumn = join.getRightTable().getColumn();

            if (restrictedColumns.contains(leftColumn) || restrictedColumns.contains(rightColumn)) {
                relationshipListIterator.remove();
            }
        } else {
            ListIterator<Join> joinListIterator = joinsList.listIterator();
            while (joinListIterator.hasNext()) {
                Join join = joinListIterator.next();
                String leftColumn = join.getLeftTable().getColumn();
                String rightColumn = join.getRightTable().getColumn();

                if (restrictedColumns.contains(leftColumn) || restrictedColumns.contains(rightColumn)) {
                    joinListIterator.remove();
                }
            }

            if (joinsList.size() == 0) {
                relationshipListIterator.remove();
            }
        }
    }
    /**
     * Handles filtering in metadata mode, and set the join to null to preserve the order.
     *
     * @param restrictedTables      list of restricted tables.
     * @param leftTable             left table name.
     * @param rightTable            right table name.
     * @param joinsList             list of joins in the relationship.
     * @param restrictedColumns     set of restricted columns.
     */
    private void metadataMode(List<String> restrictedTables, String leftTable, String rightTable,
                              List<Join> joinsList, Set<String> restrictedColumns) {
        for (Join join : joinsList) {
            if ((restrictedTables.contains(leftTable) || restrictedTables.contains(rightTable)) ||
                    (restrictedColumns.contains(join.getLeftTable().getColumn())) || (restrictedColumns.contains(join
                    .getRightTable().getColumn()))) {
                //To preserve the order, set all except id to null
                join.setLeftTable(null);
                join.setOperator(null);
                join.setRightTable(null);
                join.setType(null);
            }
        }
    }
}
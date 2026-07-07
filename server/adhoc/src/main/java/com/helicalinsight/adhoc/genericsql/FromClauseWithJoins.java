package com.helicalinsight.adhoc.genericsql;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.LeftTable;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.RightTable;
import com.helicalinsight.datasource.GsonUtility;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a builder for the FROM clause of a SQL query with join operations.
 * it extends {@link AbstractFromImpl} and implements {@link FromClause}
 * Created by author on 10/15/2015.
 * @author Rajasekhar
 */
final class FromClauseWithJoins extends AbstractFromImpl implements FromClause {
    private final SqlQueryContext context;
    private final Map<String, String> nameAndFullNameMap;
    private final GraphAlgorithm graphAlgorithm;
    private final StringBuilder query;
    private final List<JoinClause> joinClauses;
    private final List<String> derivedTableNames;

    public FromClauseWithJoins(SqlQueryContext context) {
        this.query = new StringBuilder();
        this.joinClauses = new ArrayList<>();
        this.context = context;
        this.nameAndFullNameMap = this.context.getNameAndFullNameMap();
        Map<String, Node> nodesWithWeights = new GraphContext(this.context.getMetadata()).nodesWithWeights();
        this.graphAlgorithm = new GraphAlgorithmImpl(this.context.getGraphNodes(), nodesWithWeights);
        this.derivedTableNames = this.context.getDerivedTableNames();
    }
    /**
     * Builds the FROM clause of the SQL query with join operations.
     *
     * @param query 	 existing SQL query.
     * @return complete SQL query with the FROM clause and join operations.
     */
    public String from(String query) {
        this.query.append(query).append(" \nfrom\n\t");

        prepareJoinClauses();

        StringBuilder fromClause = new StringBuilder();

        for (JoinClause joinClause : this.joinClauses) {
            fromClause.append(joinClause.render()).append(" ");
        }

        String fromClauseString = fromClause.toString().trim();
        return this.query.append(fromClauseString).append(" \n").toString();
    }
    /**
     * Prepares the join clauses for the FROM clause.
     */
    private void prepareJoinClauses() {
        List<Node> nodeList = this.graphAlgorithm.connectedComponents();
        if (nodeList == null) {
            throw new MetadataDesignException("The selected tables can't be joined. Illegal state.");
        }

        int size = getSizeOfTables(nodeList);

        for (int index = 1; index < size; index++) {
            Node node = nodeList.get(index);
            String current = node.getName();

            List<String> selectedAdjacent = getAdjacentTables(nodeList, index, node.getAdjacencyList());

            if (selectedAdjacent.isEmpty()) {
                throw new QueryBuilderException("Current table can't be joined with any preceding tables. Invalid " +
                        "state.");
            }

            int joinsCount = selectedAdjacent.size();
            JoinClause joinClause = null;
            //Get all the joins of the current table by looping through the selected adjacent tables
            for (int counter = 0; counter < joinsCount; counter++) {
                String previous = selectedAdjacent.get(counter);
                //First get the join condition
                if (counter == 0) {
                    if (index == 1) {
                        //Reverse the order in case of the first two tables
                        joinClause = joinClause(current, previous);
                        joinClause.setFirstClause(true);
                    } else {
                        joinClause = joinClause(current, previous);
                    }
                } else {
                    StringBuilder andCondition = joinCondition(listOfJoins(current, previous));
                    joinClause.addAndConditions(andCondition);
                }

                //Before exiting, add the conditions to the list
                if (counter == (joinsCount - 1)) {
                    this.joinClauses.add(joinClause);
                }
            }
        }
    }

    private int getSizeOfTables(List<Node> selectedTables) {
        int size = selectedTables.size();

        if (size != 2) {
            if (!this.graphAlgorithm.isValid(selectedTables)) {
                throw new MetadataDesignException("The selected tables can't be joined. Illegal state.");
            }
        }
        return size;
    }
    /**
     * Gets the list of adjacent tables for a given node.
     *
     * @param selectedTables 		 list of selected tables.
     * @param index          		 index of the node.
     * @param adjacencyList  		 list of adjacent nodes.
     * @return The list of adjacent tables.
     */
    private List<String> getAdjacentTables(List<Node> selectedTables, int index, List<Node> adjacencyList) {
        List<String> selectedAdjacent = new ArrayList<>();
        for (int counter = 0; counter < index; counter++) {
            Node neighbour = selectedTables.get(counter);
            if (adjacencyList.contains(neighbour)) {
                selectedAdjacent.add(neighbour.getName());
            }
        }
        return selectedAdjacent;
    }
    /**
     * Builds a join clause between two tables.
     *
     * @param current  		 current table.
     * @param previous 		 previous table.
     * @return The join clause between the tables.
     */
    private JoinClause joinClause(String current, String previous) {
        List<Join> joins = listOfJoins(current, previous);

        StringBuilder on = joinCondition(joins);

        StringBuilder joinType = new StringBuilder();
        // Per set of joins only one join type is used. The first join type
        // found in the xml of this relationship
        String sqlJoinType = sqlJoinType(joins.get(0), current);
        joinType.append(sqlJoinType);

        String fullTableNameCurrent = fullTableName(current,
                this.derivedTableNames.contains(current));
        String fullTableNamePrevious = fullTableName(previous,
                this.derivedTableNames.contains(previous));
        StringBuilder currentTable = new StringBuilder(fullTableNameCurrent);
        StringBuilder previousTable = new StringBuilder(fullTableNamePrevious);

        return new JoinClause(currentTable, previousTable, joinType, on);
    }
    /**
     * Gets the SQL join type for the join.
     *
     * @param join    	 join object.
     * @param current 	 current table.
     * @return The SQL join type.
     */
    private String sqlJoinType(Join join, String current) {
        String type = join.getType();
        if (!("inner".equalsIgnoreCase(type) || "full".equalsIgnoreCase(type))) {
            String right = join.getRightTable().getTable();
            if (current.equalsIgnoreCase(right)) {
                return super.sqlJoinType(type);
            } else if ("left".equalsIgnoreCase(type)) {
                return super.sqlJoinType("right");
            } else {
                return super.sqlJoinType("left");
            }
        }
        return super.sqlJoinType(type);
    }
    /**
     * Gets the full table name including any aliases.
     *
     * @param table   	 table name.
     * @param isView  	 Indicates if the table is a view.
     * @return The full table name.
     */
    private String fullTableName(String table, boolean isView) {
        if (isView) {
            table = subQuery(table, this.context).toString();
        } else {
            String name = this.nameAndFullNameMap.get(table);
            String originalTableName = FromClauseWithoutJoins.getOriginalTableName(name, this.context.getFormData());
            if (originalTableName != null && !originalTableName.isEmpty()) {
                String databaseName = this.context.getDatabaseName();
                if(originalTableName.contains("_#1")) 
                	table =  this.context.quotes(originalTableName)+ " " + this.context.quotes(this.context.getAliasName(name))+" ";
                else 
                	table = this.context.quotes(originalTableName) + " " + this.context.quotes(this.context.getTableName(name)) + " ";
            } else {
            	String alias = this.context.quotes(this.context.getAliasName(name));
            	String nameWithQuotes = this.context.quotes(name);
                table = name.contains("_#1_") ?  nameWithQuotes+" " +alias : nameWithQuotes;
            }
        }
        return table;
    }
    /**
     * Retrieves the list of join conditions between two tables.
     *
     * @param first  		 name of the first table.
     * @param second 		 name of the second table.
     * @return The list of join conditions.
     */
    private List<Join> listOfJoins(String first, String second) {
        Relationship relationship = relation(first, second);

        if (relationship == null || relationship.getJoin() == null) {
            throw new IllegalStateException(String.format("Metadata consists of inconsistent nodes. " + "The " +
                    "relationship between %s and %s is empty or not defined.", first, second));
        }

        return relationship.getJoin();
    }
    /**
     * Constructs the join condition based on the provided list of join objects.
     *
     * @param joins 		 list of join objects containing join conditions.
     * @return The constructed join condition as a StringBuilder.
     */
    private StringBuilder joinCondition(List<Join> joins) {
        StringBuilder on = new StringBuilder();
        int counter = 0;
        for (Join join : joins) {
            StringBuilder condition = parse(join);
            if (counter == 0) {
                on.append(condition);
            } else {
                on.append(" and ").append(condition);
            }
            counter++;
        }

        //There should have been at least one join
        if (counter == 0) {
            throw new QueryBuilderException("Joins must be present in the relationships. Malformed metadata.");
        }
        return on;
    }
    /**
     * Retrieves the Relationship object between two tables based on their names.
     *
     * @param current  		 name of the current table.
     * @param previous 		 name of the previous table.
     * @return Relationship object between the two tables, or {@code null} if no relationship exists.
     */
    @Nullable
    private Relationship relation(String current, String previous) {
        for (Relationship relationship : this.context.getListOfRelationships()) {
            if (relationship.isIdentical(current, previous)) {
                return relationship;
            }
        }
        return null;
    }
    /**
     * Generates the join condition based on the details provided in the Join object.
     *
     * @param join 		 Join object containing the details of the join.
     * @return A StringBuilder representing the join condition.
     */
    private StringBuilder parse(@NotNull Join join) {
        StringBuilder on = new StringBuilder();

        LeftTable leftTable = join.getLeftTable();
        RightTable rightTable = join.getRightTable();

        String left = leftTable.getTable();
        String right = rightTable.getTable();

        String leftName = this.nameAndFullNameMap.get(left);
        String rightName = this.nameAndFullNameMap.get(right);

        if (this.derivedTableNames.contains(left)) {
            leftName = left;
        }

        if (this.derivedTableNames.contains(right)) {
            rightName = right;
        }

        leftName=left;
        rightName=right;

        
        
        JsonObject formData = this.context.getFormData();
        
        
        JsonObject duplicateOriginalMap = GsonUtility.optJsonObject(formData,"duplicateOriginalMap");
        String leftSide = leftName + "." + leftTable.getColumn();
        String rightSide = rightName + "." + rightTable.getColumn();
        String leftPresent = GsonUtility.optString(duplicateOriginalMap,leftSide);
        String rightPresent = GsonUtility.optString(duplicateOriginalMap,rightSide);
        if (leftPresent != null && !leftPresent.isEmpty()) {
            leftSide = leftPresent;
        }
        if (rightPresent != null && !rightPresent.isEmpty()) {
            rightSide = rightPresent;
        }

        on.append(this.context.quotes(leftSide))
                .append(" ").append(join.getOperator()).append(" ").append(this.context.quotes(rightSide));

        return on;
    }
}
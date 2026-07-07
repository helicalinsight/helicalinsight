package com.helicalinsight.adhoc.genericsql;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a join clause in an SQL query, including the current table, previous table, join type, and join condition.
 * Provides methods to construct and render the join clause.
 * 
 * Created by author on 10/23/2015.
 * @author Rajasekhar
 */
final class JoinClause {
    private final StringBuilder current;

    private final StringBuilder previous;

    private final StringBuilder joinType;

    private final StringBuilder on;

    private final List<StringBuilder> andConditions;

    private boolean isFirstClause = false;
    /**
     * Constructs a JoinClause object with the given components.
     * 
     * @param current  		 current table being joined
     * @param previous 		 previous table that was joined
     * @param joinType 		 type of join (e.g., INNER JOIN, LEFT JOIN)
     * @param on       		 join condition
     */
    public JoinClause(StringBuilder current, StringBuilder previous, StringBuilder joinType, StringBuilder on) {
        this.current = current;
        this.previous = previous;
        this.joinType = joinType;
        this.on = on;
        this.andConditions = new ArrayList<>();
    }
    /**
     * Sets whether this is the first join clause in the query.
     * @param isFirstClause 		{@code true} if it is the first join clause, {@code false} otherwise
     */
    public void setFirstClause(boolean isFirstClause) {
        this.isFirstClause = isFirstClause;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        JoinClause that = (JoinClause) other;

        if (current != null ? !current.equals(that.current) : that.current != null) {
            return false;
        }
        if (previous != null ? !previous.equals(that.previous) : that.previous != null) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (joinType != null ? !joinType.equals(that.joinType) : that.joinType != null) {
            return false;
        }
        return !(on != null ? !on.equals(that.on) : that.on != null);
    }

    @Override
    public int hashCode() {
        int result = current != null ? current.hashCode() : 0;
        result = 31 * result + (previous != null ? previous.hashCode() : 0);
        result = 31 * result + (joinType != null ? joinType.hashCode() : 0);
        result = 31 * result + (on != null ? on.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.render().toString();
    }
    /**
     * Generates the SQL representation of the join clause.
     * @return The SQL representation of the join clause as a StringBuilder
     */
    public StringBuilder render() {
        StringBuilder sql = new StringBuilder();
        if (this.andConditions.isEmpty()) {
            if (this.isFirstClause) {
                sql.append(bothTables()).append(")");
            } else {
                sql.append("\n\t").append(onlyCurrentTable()).append(")");
            }
        } else {
            StringBuilder conditions = new StringBuilder().append(" and ");
            int size = this.andConditions.size();

            for (int index = 0; index < size; index++) {
                conditions.append(this.andConditions.get(index));
                if ((index != size - 1) && (size != 1)) {
                    conditions.append(" and ");
                }
            }

            if (this.isFirstClause) {
                sql.append(bothTables()).append(conditions).append(")");
            } else {
                sql.append("\n\t").append(onlyCurrentTable()).append(conditions).append(")");
            }
        }

        return sql;
    }
    /**
     * Constructs a StringBuilder representing the join clause when both tables are involved in the join.
     * @return A StringBuilder representing the join clause with both tables
     */
    private StringBuilder bothTables() {
        return this.previous.append(" \n\t").append(this.joinType).append(" ").append(this.current).append(" on (")
                .append(this.on);
    }
    /**
     * Constructs a StringBuilder representing the join clause when only the current table is involved in the join.
     * @return A StringBuilder representing the join clause with only the current table
     */
    private StringBuilder onlyCurrentTable() {
        return this.joinType.append(" ").append(this.current).append(" on (").append(this.on);
    }
    /**
     * Adds an additional AND condition to the join clause.
     * @param condition 		 additional AND condition to add
     */
    public void addAndConditions(StringBuilder condition) {
        this.andConditions.add(condition);
    }
}

package com.helicalinsight.adhoc.genericsql;

/**
 * Created by author on 13-04-2015.
 *
 * @author Rajasekhar
 */
final class Aggregate {
    private String column;

    private String alias;

    private String function;

    private String custom;
    private Boolean applyBeforeAggregate;

    @Override
    public String toString() {
        return "Aggregate{" +
                "column='" + column + '\'' +
                ", alias='" + alias + '\'' +
                ", function='" + function + '\'' +
                ", custom='" + custom + '\'' +
                ", applyBeforeAggregate=" + applyBeforeAggregate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Aggregate aggregate = (Aggregate) o;

        if (alias != null ? !alias.equals(aggregate.alias) : aggregate.alias != null) return false;
        if (applyBeforeAggregate != null ? !applyBeforeAggregate.equals(aggregate.applyBeforeAggregate) : aggregate.applyBeforeAggregate != null)
            return false;
        if (column != null ? !column.equals(aggregate.column) : aggregate.column != null) return false;
        if (custom != null ? !custom.equals(aggregate.custom) : aggregate.custom != null) return false;
        if (function != null ? !function.equals(aggregate.function) : aggregate.function != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = column != null ? column.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (function != null ? function.hashCode() : 0);
        result = 31 * result + (custom != null ? custom.hashCode() : 0);
        result = 31 * result + (applyBeforeAggregate != null ? applyBeforeAggregate.hashCode() : 0);
        return result;
    }

    public Boolean getApplyBeforeAggregate() {

        return applyBeforeAggregate;
    }

    public void setApplyBeforeAggregate(Boolean applyBeforeAggregate) {
        this.applyBeforeAggregate = applyBeforeAggregate;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

}

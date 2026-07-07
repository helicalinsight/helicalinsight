package com.helicalinsight.adhoc.genericsql;

import org.hibernate.dialect.Dialect;

/**
 * Created by user on 11/25/2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class CalciteHibernateDialect extends Dialect {

    @Override
    public char openQuote() {
        return '`';
    }

    @Override
    public char closeQuote() {
        return '`';
    }

  
    public boolean supportsLimit() {
        return true;
    }

    
    public String getLimitString(String query, int offset, int limit) {
        return query + " limit " + limit;
    }
}

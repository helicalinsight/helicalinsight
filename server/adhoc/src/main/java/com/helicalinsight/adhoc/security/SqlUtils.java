package com.helicalinsight.adhoc.security;


import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.*;

/**
 * This code defines a class named SqlUtils for parsing SQL statements and extracting information.
 * Created by Helical on 3/16/2021.
 */
public class SqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(SqlUtils.class);
    private Map<String,Set<String>> sqlMap;
    private Set<String> columnNames = new HashSet<>();
    private Set<String> tablesNames = new HashSet<>();
    private Set<String> schemaNames = new HashSet<>();
    private Set<String> databaseNames = new HashSet<>();
    private Set<String> aliasNames =  new HashSet<>();
    private Expression expr = null;
    private Expression conditionalExpr = null;
    private boolean isSql=true;
    private boolean isExpression=true;
    private boolean isConditionalExpression=true;
    public SqlUtils(){
        sqlMap = new HashMap<String,Set<String>>();
    }
    /**
     * This method parses a provided SQL statement and extracts information about the tables,
     * columns, aliases, and other elements used within the statement. It considers a configuration
     * parameter to enable or disable parsing complex queries.
     *
     * @param sql 						 SQL statement to be parsed.
     * @param allowComplexJsonQueries 	 configuration parameter (boolean string) indicating whether
     *                               	 complex query checks should be enabled ("allow_complex_json_queries")
     *                                	 or disabled ("any other string").
     * @return A map containing the extracted information. Keys represent categories ("tables", "columns",
     *         "aliases", etc.), and values are sets of corresponding elements identified in the SQL statement.
     * @throws Exception If the SQL statement is invalid, parsing fails, or security restrictions are violated.
     */
    public Map<String,Set<String>> getSqlColumns(String sql,String allowComplexJsonQueries) throws Exception{
        //Parse Sql
        try{
            sql=modifedSql(sql);
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            Statement stmt = parserManager.parse(new StringReader(sql));
            if(!(stmt instanceof Select)){
                throw new Exception("Only Select Queries are supported in the custom column");
            }
            Select selectStmt = (Select)stmt;
            PlainSelect plainSelect = (PlainSelect) selectStmt.getSelectBody();
            List<SelectItem> selectItemsList=plainSelect.getSelectItems();
            for(SelectItem selectItem:selectItemsList){
                SelectExpressionItem selectExpressionItem = (SelectExpressionItem)selectItem;
                Expression expression = selectExpressionItem.getExpression();
                if(expression instanceof Function){
                    expression.getClass().getName();
                    nestedFunction((Function) expression, sqlMap,allowComplexJsonQueries);
                }
                if (expression instanceof Column){
                    sqlMap.putAll(parseColumnNames((Column)expression,allowComplexJsonQueries));
                }
            }
            //where clause
            Expression whereExpression=plainSelect.getWhere();
            if(null!=whereExpression){
                whereExpression.accept(new ExpressionVisitorAdapter(){
                    @Override
                    protected void visitBinaryExpression(BinaryExpression expr) {
                        sqlMap.putAll(parseExpressions(expr,sqlMap,allowComplexJsonQueries));
                    }
                });
            }

            //From Clause
            FromItem fromItem=plainSelect.getFromItem();
            FromItemVisitor fromItemVisitor = (FromItemVisitor)fromItem;
            if(fromItemVisitor instanceof Table){
                ((Table) fromItemVisitor).getAlias().getName();
            }

            //GroupBy Clause
            GroupByElement groupByElement=plainSelect.getGroupBy();
            if(null!=groupByElement){
                List<Expression> groupByExpression=groupByElement.getGroupByExpressions();
                for(Expression expr:groupByExpression){
                    if(expr instanceof Column){
                        sqlMap.putAll(parseColumnNames((Column)expr,allowComplexJsonQueries));
                    }
                }
            }

            //Having Clause
            Expression having = plainSelect.getHaving();
            if(having!=null){
                sqlMap.putAll(parseExpressions(having,sqlMap,allowComplexJsonQueries));
            }

            List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
            if(null!=orderByElements){
                if(orderByElements.size()!=0){
                    for(OrderByElement orderByElement:orderByElements){
                        Expression expr = orderByElement.getExpression();
                        sqlMap.putAll(parseExpressions(expr,sqlMap,allowComplexJsonQueries));
                    }
                }
            }


            Distinct distinct = plainSelect.getDistinct();
            if(null!=distinct){
                List<SelectItem> distinctItems=distinct.getOnSelectItems();
            }

            if(null!=plainSelect.getJoins()){
                List<Join> joins = plainSelect.getJoins();
                if(joins.size()>0){
                    throw new SecurityException("Unsupported Sql/Expression");
                }
            }

            sqlMap.put("tables",tablesNames);
        }catch(Exception e){
            logger.error("Invalid Sql Checking for Expressions");
            isSql=false;
        }
        //Parse Expression
        if(!isSql){
            try{
                Expression expr =CCJSqlParserUtil.parseExpression(sql);
                sqlMap.putAll(parseExpressions(expr,sqlMap,allowComplexJsonQueries));
            }catch(Exception e){
                logger.error("Invalid Expression Checking for Conditional Expression");
                isExpression=false;
            }
        }
        //parse conditional expressions
        if(!isExpression){
            try{
                Expression expr=CCJSqlParserUtil.parseCondExpression(sql);
                sqlMap.putAll(parseExpressions(expr, sqlMap,allowComplexJsonQueries));
            }catch (Exception e){
                logger.error("invalid conditional expression");
                throw new SecurityException("invalid sql");
            }

        }
        return sqlMap;
    }

    /**
     * This private helper method parses a given expression and extracts information about tables, columns,
     * and potentially complex queries (depending on the `allowComplexJson` parameter). The extracted information
     * is added to the provided `sqlMap`.
     *
     * @param expr 						 expression object to be parsed.
     * @param sqlMap 					 map to store the extracted information. Keys represent categories ("tables", "columns",
     *               					 "aliases", etc.), and values are sets of corresponding elements identified in the SQL statement.
     * @param allowComplexJson 			 configuration parameter (boolean string) indicating whether complex query checks
     *                         			 should be enabled ("allow_complex_json_queries") or disabled ("any other string").
     * @return The updated `sqlMap` containing the extracted information from the parsed expression.
     * @throws Exception If errors occur during expression parsing.
     */
    private Map<String,Set<String>> parseExpressions(Expression expr,Map<String,Set<String>> sqlMap,String allowComplexJson){
        if(expr instanceof Function){
            ExpressionList parameters = ((Function) expr).getParameters();
            List<Expression> expressions = parameters.getExpressions();
            for(Expression exp:expressions){
                if(exp instanceof Column){
                    sqlMap.putAll(parseColumnNames((Column) exp,allowComplexJson));
                }else if(exp instanceof Function){
                    sqlMap.putAll(nestedFunction((Function) exp,sqlMap,allowComplexJson));
                }else{
                    sqlMap.putAll(extractColumns(exp,sqlMap,allowComplexJson));
                }
            }
        }else if(expr instanceof Column){
            sqlMap.putAll(parseColumnNames((Column)expr,allowComplexJson));
        }else{
            sqlMap.putAll(extractColumns(expr,sqlMap,allowComplexJson));
        }

        return sqlMap;
    }


    /**
     * Handles nested functions, extracting table, column, and complex query information
     * (based on `allowComplexJson`) from parameters and adding them to `sqlMap`.
     *
     * @param func       			 nested function to parse.
     * @param sqlMap     			 map to store extracted information.
     * @param allowComplexJson 		 Configuration parameter for complex query checks.
     * @return updated `sqlMap` with information from nested function parameters.
     * @throws Exception If errors occur during expression parsing.
     */
    private Map<String,Set<String>> nestedFunction(Function func,Map<String,Set<String>> sqlMap,String allowComplexJson){
        ExpressionList parameters = func.getParameters();
        if(func.isAllColumns()){
            Set<String> allColumns = new HashSet<String>();
            allColumns.add("*");
            sqlMap.put("AllColumns",allColumns);
        }else if(null!=parameters){
            List<Expression> expressions = parameters.getExpressions();
            for(Expression exp:expressions){
               // System.out.println(exp.getClass().getName());
                if(exp instanceof Column){
                    sqlMap.putAll(parseColumnNames((Column)exp,allowComplexJson));
                }else if(exp instanceof Function){
                    sqlMap.putAll(nestedFunction((Function) exp,sqlMap,allowComplexJson));
                }else if(exp instanceof Expression){
                    sqlMap.putAll(extractColumns(exp,sqlMap,allowComplexJson));
                }else if(exp instanceof IsBooleanExpression){
                    //System.out.println(((IsBooleanExpression) exp).getLeftExpression());
                }
            }
        }
        return sqlMap;
    }


    /**
     * This method extracts table and column information from a complex expression using an ExpressionVisitor.
     *
     * @param expr		       expression to be analyzed.
     * @param sqlMap           map to store extracted information (tables, columns, etc.).
     * @param allowComplexJson Configuration parameter for complex query checks ("allow_complex_json_queries" or "any other string").
     * @return updated `sqlMap` containing extracted information from the expression.
     * @throws Exception If errors occur during expression analysis.
     */
    private Map<String,Set<String>> extractColumns(Expression expr,Map<String,Set<String>> sqlMap,String allowComplexJson){
        System.out.println(expr.getClass().getName());
        expr.accept(new ExpressionVisitorAdapter(){


            @Override
            public void visit(NotExpression notExpression) {
                Expression expression=notExpression.getExpression();
                extractColumns(expression,sqlMap,allowComplexJson);
            }

            @Override
            public void visit(Parenthesis parenthesis) {
                Expression expression=parenthesis.getExpression();
                extractColumns(expression,sqlMap,allowComplexJson);
            }

            @Override
            public void visit(IsNullExpression isNullExpression) {
                Expression expr=isNullExpression.getLeftExpression();
                if(expr instanceof Column){
                    //columnNames.add(((Column) expr).getColumnName());
                    sqlMap.putAll(parseColumnNames((Column)expr,allowComplexJson));
                }
            }

            @Override
            public void visit(Function function) {
                //columnNames.addAll(nestedFunction(function, columnNames));
                sqlMap.putAll(nestedFunction(function,sqlMap,allowComplexJson));
            }

            @Override
            public void visit(CastExpression castExpression) {
                Expression leftExpression = castExpression.getLeftExpression();
                //System.out.println(leftExpression.getClass().getName());
                if(leftExpression instanceof Column){
                    //columnNames.add(((Column) leftExpression).getColumnName());
                    sqlMap.putAll(parseColumnNames((Column)leftExpression,allowComplexJson));
                }
                super.visit(castExpression);
            }


            @Override
            public void visit(JsonExpression jsonExpr) {
                //System.out.println("jsonExpression:"+jsonExpr.toString());
            }


            @Override
            protected void visitBinaryExpression(BinaryExpression expr) {
                Expression leftExpression = expr.getLeftExpression();
                //System.out.println(leftExpression.getClass().getName());
                Expression rightExpression = expr.getRightExpression();
                //System.out.println(rightExpression.getClass().getName());
                if(leftExpression instanceof Column){
                    //columnNames.add(((Column) leftExpression).getColumnName());
                    sqlMap.putAll(parseColumnNames((Column)leftExpression,allowComplexJson));
                }else if(leftExpression instanceof BinaryExpression){
                    extractColumns(leftExpression,sqlMap,allowComplexJson);
                }
                else if(leftExpression instanceof Function){
                    ExpressionList expList=((Function) leftExpression).getParameters();
                    List<Expression> expressions=expList.getExpressions();
                    for(Expression exp:expressions){
                        if(exp instanceof Column){
                            //columnNames.add(((Column) exp).getColumnName());
                            sqlMap.putAll(parseColumnNames((Column)exp,allowComplexJson));
                        }
                    }
                }else{
                    leftExpression.accept(new ExpressionVisitorAdapter(){
                        @Override
                        protected void visitBinaryExpression(BinaryExpression expr) {
                            //System.out.println(expr.getClass().getName());
                            Expression leftExpression1 = expr.getLeftExpression();
                            if(leftExpression1 instanceof Column){
                                //columnNames.add(((Column) leftExpression1).getColumnName());
                                sqlMap.putAll(parseColumnNames((Column)leftExpression1,allowComplexJson));
                            }
                        }
                    });
                }
                if(rightExpression instanceof Column){
                    //columnNames.add(((Column) rightExpression).getColumnName());
                    sqlMap.putAll(parseColumnNames((Column)rightExpression,allowComplexJson));
                }else if(rightExpression instanceof BinaryExpression){
                    extractColumns(rightExpression,sqlMap,allowComplexJson);
                }else if(rightExpression instanceof Function){
                    ExpressionList expList=((Function) rightExpression).getParameters();
                    List<Expression> expressions=expList.getExpressions();
                    for(Expression exp:expressions){
                        if(exp instanceof Column){
                            //columnNames.add(((Column) exp).getColumnName());
                            sqlMap.putAll(parseColumnNames((Column)rightExpression,allowComplexJson));
                        }
                    }
                }else{
                    rightExpression.accept(new ExpressionVisitorAdapter(){
                        @Override
                        protected void visitBinaryExpression(BinaryExpression expr) {
                            //System.out.println(expr.getClass().getName());
                            Expression rightExpression = expr.getRightExpression();
                            if(rightExpression instanceof Column){
                                //columnNames.add(((Column) rightExpression).getColumnName());
                                sqlMap.putAll(parseColumnNames((Column)rightExpression,allowComplexJson));
                            }
                        }
                    });
                }
            }
        });
        return sqlMap;
    }

    /**
     * This method extracts table, schema, database, alias, and column information from a Column object.
     *
     * @param column 				 Column object to be analyzed.
     * @param allowComplexJson 		 Configuration parameter for complex query checks ("allow_complex_json_queries" or "any other string").
     * @return The updated `sqlMap` containing extracted information from the Column object.
     * @throws Exception If errors occur during expression analysis or string manipulation.
     */
    private Map<String,Set<String>> parseColumnNames(Column column,String allowComplexJson){
        Set<String> complexList = rePosition(column.getASTNode());
        Table table=column.getTable();
        if(null!=table){
            String tableName = table.getName().trim();
            tableName=modifiedString(tableName);
            tablesNames.add(tableName);
            if(null!=table.getAlias()){
                Alias alias = table.getAlias();
                String aliasName = alias.getName().trim();
                aliasName=modifiedString(aliasName);
                aliasNames.add(aliasName);
            }
            if(null!=table.getDatabase().getDatabaseName()){
                String dbName = table.getDatabase().getDatabaseName().trim();
                dbName=modifiedString(dbName);
                databaseNames.add(dbName);
            }
            if(null!=table.getSchemaName()){
                String schemaName = table.getSchemaName().trim();
                schemaName=modifiedString(schemaName);
                schemaNames.add(schemaName);
            }
        }
        String columnName = StringUtils.strip(column.getColumnName(), "\"").trim();
        columnName=modifiedString(columnName);
        if(!checkSqlKeyWords(columnName)){
            columnNames.add(columnName);
        }
        sqlMap.put("catalog",databaseNames);
        sqlMap.put("schema",schemaNames);
        sqlMap.put("tables",tablesNames);
        sqlMap.put("column",columnNames);
        sqlMap.put("alias",aliasNames);
        if(allowComplexJson.equals("allow_complex_json_queries")){
            sqlMap.put("complexQuery",complexList);
        }
        return sqlMap;
    }
    /**
     * This method checks if a given column name is allowed based on a simulated whitelisted keyword list.
     *
     * @param columnName 	 column name to be checked.
     * @return {@code true} if the column name is allowed, {@code false} otherwise.
     * @throws Exception If errors occur while retrieving the  configuration.
     */
    public boolean checkSqlKeyWords(String columnName){
        JsonObject sqlWhiteListKeyWords = JsonUtils.getSqlWhiteListKeyWords();
        //System.out.println(sqlWhiteListKeyWords);
        columnName=columnName.toUpperCase();
        String isAllowed=sqlWhiteListKeyWords.get("allowed").getAsString();
        if(isAllowed.equalsIgnoreCase("true")){
            if(null!=sqlWhiteListKeyWords.get("")){
                String key = sqlWhiteListKeyWords.get("").getAsString().trim().replaceAll("\n","").replaceAll("\t","");
                String[] keywordsArray = key.split(",");
                for(int i=0;i<keywordsArray.length;i++){
                    if(keywordsArray[i].equalsIgnoreCase(columnName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * This method modifies a provided SQL string based on escape string configurations for SQL Server brackets.
     *
     * @param sql 			 SQL statement to be modified.
     * @return modified SQL string with square brackets removed (if enabled in configuration).
     * @throws Exception If errors occur while retrieving escape strings.
     */
    public String modifedSql(String sql){
        JsonObject escape_strings = JsonUtils.getEscapeStrings();
        JsonObject jsonObject= escape_strings.get("sql-server-db").getAsJsonObject();
        String isEnabled=  jsonObject.get("enabled").getAsString();
        if(isEnabled.equalsIgnoreCase("true")){
            String opening_square_bracket=jsonObject.get("opening_square_bracket").getAsString();
            String closing_square_bracket=jsonObject.get("closing_square_bracket").getAsString();
            sql=sql.replaceAll("\\"+opening_square_bracket,  "").replaceAll("\\"+closing_square_bracket,  "");
        }
        return sql;
    }
    /**
     * This method modifies a given element name based on a configuration of escape strings.
     *
     * @param elementName 		 element name to be modified.
     * @return modified element name string.
     * @throws Exception If errors occur while retrieving escape strings.
     */
    public static String modifiedString(String elementName){
        JsonObject escapeStrings = JsonUtils.getEscapeStrings();
        JsonObject dbLiterals=  escapeStrings.get("common-db-literals").getAsJsonObject();
        String dbLiteralString=dbLiterals.get("").toString().trim().replaceAll("\n","").replaceAll("\t","");
        String[] dbLiteralArray = dbLiteralString.split(",");
        for(int i=0;i<dbLiteralArray.length;i++){
            elementName=StringUtils.removeStart(elementName,dbLiteralArray[i]);
            elementName=StringUtils.removeEnd(elementName,dbLiteralArray[i]);
        }
        return elementName;
    }

    /**
     * This method extracts a list of element names from a provided (AST) node.
     *
     * @param astNode (SimpleNode)  AST node representing the element hierarchy.
     * @return A set of strings containing the element names in their relative order.
     */
    public static Set<String> rePosition(SimpleNode astNode){
        Set<String> namesList = new LinkedHashSet<>();
        String elementName =  modifiedString(astNode.jjtGetFirstToken().image);
        namesList.add(elementName);
        Token nextToken = astNode.jjtGetFirstToken().next;
        while(nextToken!=null){
            String image = modifiedString(nextToken.image);
            if(!StringUtils.equals(image,".") && !StringUtils.isEmpty(image)){
                namesList.add(image);
            }
            nextToken=nextToken.next;
        }
        return namesList;
    }
}

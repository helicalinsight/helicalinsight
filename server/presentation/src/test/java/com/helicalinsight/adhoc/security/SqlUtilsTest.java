package com.helicalinsight.adhoc.security;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.IsBooleanExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.parser.Token;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Database;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SqlUtilsTest {

	@Test
	public void ut_a1_test_getSqlColumns() throws Exception {
		SqlUtils sqlUtils = new SqlUtils();
		sqlUtils.getSqlColumns("sql", "allowComplexJsonQueries");
		
	}
	
	
	@Test(expected = SecurityException.class)
	public void ut_a2_test_getSqlColumns() throws Exception {
		SqlUtils sqlUtils = new SqlUtils();
		String sql = "SELECT * FROM [TableName]";
		Statement stmt = mock(Statement.class);
		
		try(MockedConstruction<CCJSqlParserManager> construction = mockConstruction(CCJSqlParserManager.class,(mock,context)->{
			when(mock.parse(any(Reader.class))).thenReturn(stmt);
		})){
			sqlUtils.getSqlColumns(sql, "allowComplexJsonQueries");
			
		}
	}
	
	
	@Test
	public void ut_a3_test_getSqlColumns() throws Exception {
		SqlUtils sqlUtils = new SqlUtils();
		String sql = "SELECT * FROM [TableName]";
		Select select = mock(Select.class);
		PlainSelect plainSelect = mock(PlainSelect.class);
		SelectExpressionItem  selectExpressionItem = mock(SelectExpressionItem.class);
		Function function = mock(Function.class);
		when(select.getSelectBody()).thenReturn(plainSelect);
		List<SelectItem> selectItemsList=new ArrayList<>();
		selectItemsList.add(selectExpressionItem);
		when(plainSelect.getSelectItems()).thenReturn(selectItemsList);
		when(selectExpressionItem.getExpression()).thenReturn(function);
		try(MockedConstruction<CCJSqlParserManager> construction = mockConstruction(CCJSqlParserManager.class,(mock,context)->{
			when(mock.parse(any(Reader.class))).thenReturn(select);
		})){
			Map<String, Set<String>> sqlColumns = sqlUtils.getSqlColumns(sql, "allowComplexJsonQueries");
			System.out.println(sqlColumns);
		}
	}
	
	@Test(expected = SecurityException.class)
	public void ut_a4_test_getSqlColumns() throws Exception {
		SqlUtils sqlUtils = new SqlUtils();
		String sql = "SELECT * FROM [TableName]";
		Select select = mock(Select.class);
		PlainSelect plainSelect = mock(PlainSelect.class);
		SelectExpressionItem  selectExpressionItem = mock(SelectExpressionItem.class);
		Function function = mock(Function.class);
		ExpressionList parameters = mock(ExpressionList.class);
		Expression whereExpression= mock(Expression.class);
		Table table = mock(Table.class);
		
		
		when(select.getSelectBody()).thenReturn(plainSelect);
		List<SelectItem> selectItemsList=new ArrayList<>();
		selectItemsList.add(selectExpressionItem);
		when(plainSelect.getSelectItems()).thenReturn(selectItemsList);
		when(selectExpressionItem.getExpression()).thenReturn(function);
		when(function.getParameters()).thenReturn(parameters);
		when(function.isAllColumns()).thenReturn(true);
		when(plainSelect.getWhere()).thenReturn(whereExpression);
		when(plainSelect.getFromItem()).thenReturn(table);
		try(MockedConstruction<CCJSqlParserManager> construction = mockConstruction(CCJSqlParserManager.class,(mock,context)->{
			when(mock.parse(any(Reader.class))).thenReturn(select);
		})){
			Map<String, Set<String>> sqlColumns = sqlUtils.getSqlColumns(sql, "allowComplexJsonQueries");
			System.out.println(sqlColumns);
		}
	}
	
	@Test(expected = SecurityException.class)
	public void ut_a5_test_getSqlColumns() throws Exception {
		SqlUtils sqlUtils = new SqlUtils();
		String sql = "SELECT * FROM [TableName]";
		Select select = mock(Select.class);
		PlainSelect plainSelect = mock(PlainSelect.class);
		SelectExpressionItem  selectExpressionItem = mock(SelectExpressionItem.class);
		Function function = mock(Function.class);
		ExpressionList parameters = mock(ExpressionList.class);
		Expression whereExpression= mock(Expression.class);
		Table table = mock(Table.class);
		Column column = mock(Column.class);
		IsBooleanExpression expression = mock(IsBooleanExpression.class);
		SimpleNode simpleNode = mock(SimpleNode.class);
		Token token = new Token();
		
		List<Expression> list = new ArrayList<>();
		list.add(whereExpression);
		list.add(expression);
		list.add(column);
		
		when(select.getSelectBody()).thenReturn(plainSelect);
		List<SelectItem> selectItemsList=new ArrayList<>();
		selectItemsList.add(selectExpressionItem);
		when(plainSelect.getSelectItems()).thenReturn(selectItemsList);
		when(selectExpressionItem.getExpression()).thenReturn(function);
		when(function.getParameters()).thenReturn(parameters);
		when(function.isAllColumns()).thenReturn(false);
		when(plainSelect.getWhere()).thenReturn(whereExpression);
		when(plainSelect.getFromItem()).thenReturn(table);
		when(parameters.getExpressions()).thenReturn(list);
		when(column.getASTNode()).thenReturn(simpleNode);
		when(simpleNode.jjtGetFirstToken()).thenReturn(token);
		
		try(MockedConstruction<CCJSqlParserManager> construction = mockConstruction(CCJSqlParserManager.class,(mock,context)->{
			when(mock.parse(any(Reader.class))).thenReturn(select);
		})){
			Map<String, Set<String>> sqlColumns = sqlUtils.getSqlColumns(sql, "allowComplexJsonQueries");
			System.out.println(sqlColumns);
		}
	}
	
	@Test(expected = SecurityException.class)
	public void ut_a6_test_getSqlColumns() throws Exception {
		SqlUtils sqlUtils = new SqlUtils();
		String sql = "SELECT * FROM [TableName]";
		Select select = mock(Select.class);
		PlainSelect plainSelect = mock(PlainSelect.class);
		SelectExpressionItem  selectExpressionItem = mock(SelectExpressionItem.class);
		Function function = mock(Function.class);
		ExpressionList parameters = mock(ExpressionList.class);
		Expression whereExpression= mock(Expression.class);
		Table table = mock(Table.class);
		Column column = mock(Column.class);
		IsBooleanExpression expression = mock(IsBooleanExpression.class);
		SimpleNode simpleNode = mock(SimpleNode.class);
		Token token = new Token();
		GroupByElement groupByElement = mock(GroupByElement.class);
		Database database = mock(Database.class);
		Alias  alias = mock(Alias.class);
		OrderByElement orderByElement = mock(OrderByElement.class);
		Distinct distinct = mock(Distinct.class);
		Join join = mock(Join.class);
		List<Expression> list = new ArrayList<>();
		list.add(column);
		
		when(select.getSelectBody()).thenReturn(plainSelect);
		List<SelectItem> selectItemsList=new ArrayList<>();
		selectItemsList.add(selectExpressionItem);
		when(plainSelect.getSelectItems()).thenReturn(selectItemsList);
		when(selectExpressionItem.getExpression()).thenReturn(function);
		when(function.getParameters()).thenReturn(parameters);
		when(function.isAllColumns()).thenReturn(true);
		when(plainSelect.getGroupBy()).thenReturn(groupByElement);
		when(groupByElement.getGroupByExpressions()).thenReturn(list);
		when(parameters.getExpressions()).thenReturn(list);
		when(column.getASTNode()).thenReturn(simpleNode);
		when(simpleNode.jjtGetFirstToken()).thenReturn(token);
		when(column.getTable()).thenReturn(table);
		when(column.getColumnName()).thenReturn("columnName");
		when(table.getAlias()).thenReturn(alias);
		when(alias.getName()).thenReturn("aliasName");
		when(table.getName()).thenReturn("tableName");
		when(table.getSchemaName()).thenReturn("schemaName");
		when(table.getDatabase()).thenReturn(database);
		when(database.getDatabaseName()).thenReturn("dbName");
		when(table.getSchemaName()).thenReturn("schemaName");
		when(plainSelect.getHaving()).thenReturn(expression);
		List<OrderByElement> orderByElements = new ArrayList<>();
		orderByElements.add(orderByElement);
		when(plainSelect.getOrderByElements()).thenReturn(orderByElements);
		when(orderByElement.getExpression()).thenReturn(function);
		when(plainSelect.getDistinct()).thenReturn(distinct);
		when(distinct.getOnSelectItems()).thenReturn(selectItemsList);
		List<Join> joins =  new ArrayList<>();
		joins.add(join);
		when(plainSelect.getJoins()).thenReturn(joins);
		try(MockedConstruction<CCJSqlParserManager> construction = mockConstruction(CCJSqlParserManager.class,(mock,context)->{
			when(mock.parse(any(Reader.class))).thenReturn(select);
		})){
			Map<String, Set<String>> sqlColumns = sqlUtils.getSqlColumns(sql, "allowComplexJsonQueries");
			System.out.println(sqlColumns);
		}
	}
	
	@Test
	public void ut_a7_test_checkSqlKeyWords() {
		SqlUtils sqlUtils = new SqlUtils();
		boolean checkSqlKeyWords = sqlUtils.checkSqlKeyWords("SQL_TSI_YEAR");
		assertTrue(checkSqlKeyWords);
	}
}

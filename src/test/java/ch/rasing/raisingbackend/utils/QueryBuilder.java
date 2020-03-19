package ch.rasing.raisingbackend.utils;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class QueryBuilder {
	
	String tableName;
	String attributeDatatypePair;
	String attributesForInsertion;
	String valuesForInsertion;
	String selectStatement = "*";
	String whereStatement = "";
	
	/**
	 * returns a QueryBuilder instance
	 * @return
	 */
	public static QueryBuilder getInstance() {
		return new QueryBuilder();
	}
	
	/**
	 * sets the tableName for the query
	 * @param name
	 * @return
	 */
	public QueryBuilder tableName(String name) {
		assert this.tableName == null;
		this.tableName = name;
		return this;
	}
	
	/**
	 * converts the map to a string
	 * @param pair
	 * @return
	 */
	public QueryBuilder attributeDatatypePairBuilder(Map<String, Type> pair) {
		assert attributeDatatypePair == null;
		for(String attr: pair.keySet()) {
			this.attributeDatatypePair += attr + ", ";
			this.attributeDatatypePair += pair.get(attr).toString();
		}
		return this;
	}
	
	/**
	 * sets the attribute datatype pair
	 * @param atr 
	 * @param t
	 * @return QueryBuilder
	 */
	public QueryBuilder pair(String atr, Type t) {
		if(attributeDatatypePair == null) {
			attributeDatatypePair = atr + t.toString();
		}else {
			attributeDatatypePair += ", " + atr + t.toString(); 
		}
		return this;
	}
	
	public QueryBuilder attribute(String attribute) {
		if(this.attributesForInsertion == null) {
			this.attributesForInsertion = attribute;
		}else {
			this.attributesForInsertion += ", " + attribute;
		}
		return this;
	}
	
	public QueryBuilder values(String value) {
		if(valuesForInsertion == null) {
			valuesForInsertion = "'" + value + "'";
		}else {
			valuesForInsertion += ", '" + value + "'"; 
		}
		return this;
	}
	
	public QueryBuilder setSelect(String statement) {
		assert selectStatement == "*";
		this.selectStatement = statement;
		return this;
	}
	
	public QueryBuilder where(String field, String value) {
		if(whereStatement == "") {
			this.whereStatement = field + " = '" + value + "'";
		}else {
			this.whereStatement += " AND " + field + " = '" + value + "'";
		}
		
		return this;
	}
	
	public String select() {
		assert tableName != null;
		whereStatement = whereStatement!= null?"": " WHERE " + whereStatement;
		return "SELECT " + selectStatement + " FROM " + tableName + whereStatement;
	}
	
	public String insert() {
		assert this.tableName != null;
		assert this.valuesForInsertion != null;
		assert this.attributesForInsertion != null;
		return "INSERT INTO " + tableName + "(" + attributesForInsertion + ") VALUES (" + valuesForInsertion + ")";  
	}
	
	public String createTable() {
		assert this.attributeDatatypePair != null;
		assert this.tableName != null;
		return "CREATE TABLE " + tableName + "(" + attributeDatatypePair + ");";
	}
	
	public String dropTable(String tablename) {
		return "DROP TABLE " + tablename;
	}
}

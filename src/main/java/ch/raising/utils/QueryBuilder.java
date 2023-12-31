package ch.raising.utils;

import java.util.Map;

/**
 * helper class for repository tests
 * @author manus
 *
 */
public class QueryBuilder {
	
	String tableName;
	String attributeDatatypePair;
	String attributesForInsertion;
	String valuesForInsertion = "";
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
	public QueryBuilder tableName(String tableName) {
		assert this.tableName == null;
		this.tableName = tableName;
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
	
	public QueryBuilder value(String value) {
		if(valuesForInsertion == "") {
			valuesForInsertion = "'" + value + "'";
		}else {
			valuesForInsertion += ", '" + value + "'"; 
		}
		return this;
	}
	public QueryBuilder value(long value) {
		if(valuesForInsertion == "") {
			valuesForInsertion = "'" + value + "'";
		}else {
			valuesForInsertion += ", '" + value + "'"; 
		}
		return this;
	}
	
	public QueryBuilder qMark() {
		if(valuesForInsertion == "") {
			valuesForInsertion = "?";
		}else {
			valuesForInsertion += ", " + "?"; 
		}
		return this;
	}
	
	public QueryBuilder setSelect(String statement) {
		assert selectStatement == "*";
		this.selectStatement = statement;
		return this;
	}
	
	public QueryBuilder whereEquals(String field, String value) {
		if(whereStatement == "") {
			this.whereStatement = field + " = '" + value + "'";
		}else {
			this.whereStatement += " AND " + field + " = '" + value + "'";
		}
		
		return this;
	}
	public QueryBuilder whereEqualsQmark(String field) {
		if(whereStatement == "") {
			this.whereStatement = field + " = " + "?";
		}else {
			this.whereStatement += " AND " + field + " = " + "?" + "";
		}
		
		return this;
	}
	
	public String select() {
		assert tableName != null;
		whereStatement = whereStatement== null?"": " WHERE " + whereStatement;
		return "SELECT " + selectStatement + " FROM " + tableName + whereStatement;
	}
	
	public String insert() {
		assert this.tableName != null;
		assert this.valuesForInsertion != "";
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

	public String delete() {
		assert whereStatement != "";
		return "DELETE FROM " + tableName + " WHERE " + whereStatement + ";";
	}
}

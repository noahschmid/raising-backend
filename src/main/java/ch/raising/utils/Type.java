package ch.raising.utils;

public enum Type {
	VARCHAR ("varchar"), 
	INT ("int"), 
	DATE ("date"), 
	LONG ("long"), 
	BIGINT ("bigint"),
	SERIAL ("serial"), 
	BYTEA ("bytea"),
	BINARY ("binary"),
	IDENTITY("identity"),
	TIMESTAMP("timestamp");
	
	String name;
	Type(String name){
		this.name  =name;
	}
	public String toString(){
		return  " " + name;
	}
}

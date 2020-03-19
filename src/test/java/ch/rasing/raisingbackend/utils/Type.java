package ch.rasing.raisingbackend.utils;

public enum Type {
	VARCHAR ("varchar"), 
	INT ("int"), 
	DATE ("date"), 
	LONG ("long"), 
	SERIAL ("serial");
	
	String name;
	Type(String name){
		this.name  =name;
	}
	public String toString(){
		return  " " + name;
	}
}

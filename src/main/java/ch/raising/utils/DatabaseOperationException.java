package ch.raising.utils;

public class DatabaseOperationException extends Exception {
String message;
	
	public DatabaseOperationException(String message) {
		this.message = "DataBaseOperationException: " + message;
	}
	public String getMessage() {
		return message;
	}

}

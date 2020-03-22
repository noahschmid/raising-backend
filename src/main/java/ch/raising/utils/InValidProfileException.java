package ch.raising.utils;

public class InValidProfileException extends Exception {

	String message;
	
	public InValidProfileException(String message) {
		this.message = "InValidProfileException: " + message;
	}
	public String getMessage() {
		return message;
	}

}

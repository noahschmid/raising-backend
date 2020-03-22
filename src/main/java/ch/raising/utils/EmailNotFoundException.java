package ch.raising.utils;

public class EmailNotFoundException extends Exception {
	String message;

	public EmailNotFoundException(String message) {
		this.message = "EmailNotFoundException: " + message;
	}

	public String getMessage() {
		return message;
	}

}

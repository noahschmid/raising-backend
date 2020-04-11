package ch.raising.utils;

import ch.raising.models.Account;

public class InvalidProfileException extends Exception {

	String message;
	Account account;
	
	public InvalidProfileException(String message) {
		this.message = "InvalidProfileException: " + message;
	}
	public InvalidProfileException(String message, Account acc) {
		this.message = "InvalidProfileException: " + message;
		this.account = acc;
	}
	
	public Account getAccount() {
		return account;
	}
	public String getMessage() {
		return message;
	}

}

package ch.raising.utils;

import ch.raising.models.Account;

public class InValidProfileException extends Exception {

	String message;
	Account account;
	
	public InValidProfileException(String message) {
		this.message = "InValidProfileException: " + message;
	}
	public InValidProfileException(String message, Account acc) {
		this.message = "InValidProfileException: " + message;
		this.account = acc;
	}
	
	public Account getAccount() {
		return account;
	}
	public String getMessage() {
		return message;
	}

}

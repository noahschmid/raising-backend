package ch.raising.models.enums;

public enum InteractionType {
	VIDEO_CONFERENCE("a video conference", "a video confernce with you."),
	COFFEE("a coffee meeting", "drink a coffee with you."),
	BUSINESS_PLAN("a business plan exchange", "exchange a business plan with you."),
	PHONE_CALL("a phone call", "make a phone call with you."),
	EMAIL("an email conversation", "send an email to you.");
	
	String pretty;
	String actionString;

	InteractionType(String pretty, String actionString) {
		this.pretty = pretty;
		this.actionString = actionString;
	}
	/**
	 * 
	 * @return a pretty sring for using in the context of a sentence
	 */
	public String getPretty() {
		return pretty;
	}
	/**
	 * @see ch.raising.services.NotificationService
	 * @return a string that completes a sentence
	 */
	public String getActionString() {
		return actionString;
	}
}

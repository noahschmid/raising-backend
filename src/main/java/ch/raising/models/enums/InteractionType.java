package ch.raising.models.enums;

public enum InteractionType {
	VIDEO_CONFERENCE("a video conference", "a video confernce with"),
	COFFEE("a coffee meeting", "drink a coffee with"),
	BUSINESS_PLAN("a business plan exchange", "exchange a business plan with"),
	PHONE_CALL("a phone call", "make a phone call with"),
	EMAIL("an email conversation", "send an email to");
	
	String pretty;
	String actionString;

	InteractionType(String pretty, String actionString) {
		this.pretty = pretty;
		this.actionString = actionString;
	}
	
	
	public String getPretty() {
		return pretty;
	}

	public String getActionString() {
		return actionString;
	}
}

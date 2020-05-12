package ch.raising.models.enums;

public enum InteractionType {
	VIDEO_CONFERENCE("a video confernce"),
	COFFEE("a coffee"),
	BUSINESS_PLAN("a business plan"),
	PHONE_CALL("a phone call"),
	EMAIL("an email");
	
	String pretty;
	InteractionType(String pretty) {
		this.pretty = pretty;
	}
	
	
	public String getPretty() {
		return pretty;
	}
}
package ch.raising.models.enums;

public enum InteractionTypes {
	VIDEO_CONFERENCE("a video confernce"),
	COFFEE("a coffee"),
	BUSINESS_PLAN("a business plan"),
	PHONE_CALL("a phone call"),
	EMAIL("an email");
	
	String pretty;
	InteractionTypes(String pretty) {
		this.pretty = pretty;
	}
	
	@Override
	public String toString() {
		return pretty;
	}
}

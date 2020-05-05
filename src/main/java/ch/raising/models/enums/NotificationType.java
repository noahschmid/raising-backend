package ch.raising.models.enums;

import java.util.Arrays;
import java.util.List;

public enum NotificationType {
	NEVER,
	MATCHLIST,
	REQUEST,
	LEAD,
	CONNECTION;
	
	public static List<NotificationType> getAll(){
		return Arrays.asList(MATCHLIST,REQUEST,LEAD,CONNECTION);
	}
}

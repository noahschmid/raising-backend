package ch.raising.models;

import java.util.List;
import java.util.ArrayList;

import ch.raising.models.enums.Device;
import ch.raising.models.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Settings {
	@Builder.Default
	private long accountId = -1;
	@Builder.Default
	private String token = "";
	@Builder.Default
	private Device device = Device.NONE;
	@Builder.Default
	private List<NotificationType> notificationTypes = new ArrayList<NotificationType>();
	@Builder.Default
	private String language = "";
	@Builder.Default
	private int numberOfMatches = -1;

	@Override
	public String toString() {
		return "token: {accountId: " + accountId + ",token: " + token + ", device:" + device + ", notificationTypes: "
				+ notificationTypes.toString() + ", language: " + language + "nubmerOfMatches: " + numberOfMatches
				+ "}";
	}
}

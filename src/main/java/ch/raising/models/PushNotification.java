package ch.raising.models;

import ch.raising.models.enums.Device;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PushNotification {
	private long accountId;
	private String title;
	private String collapseBy;
	private String message;
	private Device device;
	private String token;
}

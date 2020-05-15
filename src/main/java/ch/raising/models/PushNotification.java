package ch.raising.models;

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
public class PushNotification {
	private long accountId;
	private long requesteeId;
	private long actionId;
	private String title;
	private String message;
	private Device device;
	private NotificationType type;
	private String token;
}

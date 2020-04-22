package ch.raising.models;

import java.util.List;

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
public class DeviceToken {
	private long id;
	private long accountId;
	private String token;
	private Device device;
	private List<NotificationType> notificationTypes;
}

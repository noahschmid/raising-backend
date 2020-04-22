package ch.raising.services;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.NotificationRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.DeviceToken;
import ch.raising.models.PushNotification;
import ch.raising.models.enums.NotificationType;

@Service
public class NotificationService {

	private final NotificationRepository notificationRepo;
	private final FCMNotificationService fcmService;
	
	@Autowired
	public NotificationService(NotificationRepository notificationRepo, FCMNotificationService fcmService) {
		this.notificationRepo = notificationRepo;
		this.fcmService = fcmService;
	}

	public void registerDeviceToken(DeviceToken token) throws SQLException, DataAccessException{
		token.setAccountId(getAccountId());
		notificationRepo.addNewDeviceToken(token);
	}
	
	public void update(DeviceToken token) throws SQLException, DataAccessException{
		if(token.getNotificationTypes().contains(NotificationType.NEVER))
			token.setNotificationTypes(Arrays.asList(NotificationType.NEVER));
		notificationRepo.update(token,getAccountId());
	}
	
	public void sendTestNotificationToSelf(long id, String msg, String title) throws InterruptedException, ExecutionException {
		PushNotification notification = new PushNotification();
		DeviceToken token = notificationRepo.findDeviceTokenByAccountId(id);
		notification.setMessage(msg);
		notification.setTitle(title);
		notification.setToken(token.getToken());
		fcmService.sendMessage(notification);
	}
	
	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}
}

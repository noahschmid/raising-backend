package ch.raising.services;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.SettingRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.PushNotification;
import ch.raising.models.Settings;

@Service
public class NotificationService {

	private final SettingRepository notificationRepo;
	private final FCMNotificationService fcmService;
	
	@Autowired
	public NotificationService(SettingRepository notificationRepo, FCMNotificationService fcmService) {
		this.notificationRepo = notificationRepo;
		this.fcmService = fcmService;
	}
	
	public void sendTestNotificationToSelf(long id, String msg, String title) throws InterruptedException, ExecutionException, DataAccessException, SQLException {
		PushNotification notification = new PushNotification();
		Settings token = notificationRepo.findByAccountId(id);
		notification.setMessage(msg);
		notification.setTitle(title);
		notification.setToken(token.getToken());
		fcmService.sendMessage(notification);
	}
	
	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}
}

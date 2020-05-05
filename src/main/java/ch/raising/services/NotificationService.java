package ch.raising.services;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.SettingRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Interaction;
import ch.raising.models.PushNotification;
import ch.raising.models.Settings;
import ch.raising.models.enums.InteractionTypes;

/**
 * 
 * @author manus
 * This class manages different notifications and if the user wants to receive them.
 */
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
	
	public void sendLeadNotificationTo(long accountId, InteractionTypes type) {
		String message = "You have got a new lead for " + type.name();
		String title = "New Lead";
		
		
	}
	
	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}
}

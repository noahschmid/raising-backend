package ch.raising.services;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import ch.raising.data.SettingRepository;
import ch.raising.models.Account;
import ch.raising.models.PushNotification;
import ch.raising.models.Settings;
import ch.raising.models.enums.InteractionType;
import ch.raising.models.enums.NotificationType;
import ch.raising.utils.DatabaseOperationException;

/**
 * 
 * @author manus This class manages different notifications and if the user
 *         wants to receive them.
 */
@Service
public class NotificationService {

	private final SettingRepository settingRepo;
	private final FCMNotificationService fcmService;
	private final AccountService accountService;
	private final String title = "Rai$ing";

	@Autowired
	public NotificationService(SettingRepository notificationRepo, FCMNotificationService fcmService,
			AccountService accountService) {
		this.settingRepo = notificationRepo;
		this.fcmService = fcmService;
		this.accountService = accountService;
	}

	public void sendLeadNotification(long partnerAccountId, InteractionType type) {
		String message = getAccountName(partnerAccountId) + " wants to " + type.getActionString() + " with you.";
		sendMessage(partnerAccountId, message, title, NotificationType.LEAD);
	}

	public void sendConnectionNotification(long requesteeId, long partnerAccountId, InteractionType interaction) {
		String name = getAccountName(requesteeId);
		String message = name + " accepted your request for " + interaction.getPretty() + ".";
		sendMessage(partnerAccountId, message, title, NotificationType.CONNECTION);
	}

	public void sendRequestMatch(long requesteeId, long partnerId) {
		String name = getAccountName(requesteeId);
		String message = name + " would like to get in contact with you. Check out their profile";
		sendMessage(partnerId, message, title, NotificationType.REQUEST);
	}

	public void sendMatchRequestAccept(long requesteeId, long partnerId) {
		String name = getAccountName(requesteeId);
		String message = name + " is now a lead. Open Rai$ing and start interacting!";
		sendMessage(partnerId, message, title, NotificationType.REQUEST);
	}

	private String getAccountName(long partnerAccount) {
		try {
			Account requestee = accountService.getAccount(partnerAccount);
			String name = requestee != null ? requestee.getFirstName() + " " + requestee.getLastName() : "someone";
			if (requestee != null && accountService.isStartup(requestee.getAccountId())) {
				name = requestee.getCompanyName();
			}
			return name;
		} catch (DataAccessException | SQLException | DatabaseOperationException e) {
			LoggerFactory.getLogger(NotificationService.class)
					.error("Partneraccount for notification could not be fetched: " + e.getMessage());
			return "someone";
		}
	}

	private void sendMessage(long partnerAccountId, String message, String title, NotificationType type) {
		try {
			Settings notificationSettings = settingRepo.findInfoByAccountId(partnerAccountId);
			if (notificationSettings.getToken() == null || notificationSettings.getToken().equals("")) {
				return; // we cannot send a notification if there is no token
			}
			if (!notificationSettings.getNotificationTypes().contains(NotificationType.NEVER)
					&& notificationSettings.getNotificationTypes().contains(type)) {
				PushNotification notification = new PushNotification();
				notification.setMessage(message);
				notification.setTitle(title);
				notification.setToken(notificationSettings.getToken());
				notification.setClickAction("NOTIFICATION");
				fcmService.sendMessage(notification);
			}
		} catch (DataAccessException | SQLException e) {
			LoggerFactory.getLogger(NotificationService.class).error("Could not retreive settings");
			e.printStackTrace();
		} catch (InterruptedException | ExecutionException e) {
			LoggerFactory.getLogger(NotificationService.class)
					.error("Notification could not be sent: " + e.getMessage());
		}
	}

}

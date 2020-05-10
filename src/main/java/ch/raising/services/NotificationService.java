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

	@Autowired
	public NotificationService(SettingRepository notificationRepo, FCMNotificationService fcmService,
			AccountService accountService) {
		this.settingRepo = notificationRepo;
		this.fcmService = fcmService;
		this.accountService = accountService;
	}

	public void sendTestNotificationToSelf(long id, String msg, String title)
			throws InterruptedException, ExecutionException, DataAccessException, SQLException {
		PushNotification notification = new PushNotification();
		Settings token = settingRepo.findByAccountId(id);
		notification.setMessage(msg);
		notification.setTitle(title);
		notification.setToken(token.getToken());
		fcmService.sendMessage(notification);
	}

	public void sendLeadNotificationTo(long partnerAccountId, InteractionType type) {
		String message = "You have got a new lead for " + type.getPretty() + ".";
		String title = "New Lead";
		sendMessage(partnerAccountId, message, title, NotificationType.LEAD);
	}

	public void sendConnectionNotification(long partnerAccountId, InteractionType interaction) {
		Account partner;
		String message = "";
		String title = "New Connection";

		partner = getPartnerAccount(partnerAccountId);
		if (partner != null) {
			message = partner.getFirstName() + " " + partner.getLastName() + " accepted your Request for "
					+ interaction.getPretty() + ".";
		} 
		sendMessage(partnerAccountId, message, title, NotificationType.CONNECTION);
	}

	public void sendRequestMatchNotification(long partnerAccount) {
		Account partner = getPartnerAccount(partnerAccount);
		String message = "";
		String title = "New Matching Request";
		if (partner != null) {
			message += partner.getFirstName() + " " + partner.getLastName();
			message += "requested a match.";
		} 

		sendMessage(partnerAccount, message, title, NotificationType.REQUEST);
	}
	
	public void sendAcceptMatchNotification(long partnerAccount) {
		Account partner = getPartnerAccount(partnerAccount);
		String message = "";
		String title = "Accepted Matching Request";
		if (partner != null) {
			message += partner.getFirstName() + " " + partner.getLastName();
			message += "accepted your request.";
		}

		sendMessage(partnerAccount, message, title, NotificationType.REQUEST);
	}

	private Account getPartnerAccount(long partnerAccount) {
		try {
			Account partner = accountService.getAccount(partnerAccount);
			return partner;
		} catch (DataAccessException | SQLException | DatabaseOperationException e) {
			LoggerFactory.getLogger(NotificationService.class)
					.error("Partneraccount for notification could not be fetched: " + e.getMessage());
			return null;
		}
	}

	private void sendMessage(long partnerAccountId, String message, String title,
			NotificationType type) {
		try {
			Settings notificationSettings = settingRepo.findInfoByAccountId(partnerAccountId);
			if(notificationSettings.getToken() == null || notificationSettings.getToken().equals("")) {
				return; //we cannot send a notification if there is no token
			}
			if (!notificationSettings.getNotificationTypes().contains(NotificationType.NEVER)
					&& notificationSettings.getNotificationTypes().contains(type)) {
				PushNotification notification = new PushNotification();
				notification.setMessage(message);
				notification.setTitle(title);
				notification.setToken(notificationSettings.getToken());
				notification.setClickAction(type.name());
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

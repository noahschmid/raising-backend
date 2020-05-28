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
 * This class manages different notifications and if the user wants to receive
 * them.
 * 
 * @author manus
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

	/**
	 * sends a notification if a new lead was generated
	 * 
	 * @see ch.raising.services.InteractionService
	 * @see ch.raising.services.Matchingservice
	 * @param requesteeId
	 * @param partnerAccountId
	 * @param type
	 * @param actionId
	 */
	public void sendLeadNotification(long requesteeId, long partnerAccountId, InteractionType type, long actionId) {
		String name = getAccountName(requesteeId);
		String message = name + " wants to " + type.getActionString();
		PushNotification push = PushNotification.builder().accountId(partnerAccountId).message(message).title(title)
				.actionId(actionId).type(NotificationType.LEAD).build();
		sendMessage(push);
	}

	/**
	 * sends a notification if a connection is made
	 * 
	 * @see InteractionService
	 * @param requesteeId
	 * @param partnerAccountId
	 * @param interaction
	 * @param actionId
	 */
	public void sendConnectionNotification(long requesteeId, long partnerAccountId, InteractionType interaction,
			long actionId) {
		String name = getAccountName(requesteeId);
		String message = name + " accepted your request for " + interaction.getPretty() + ".";
		PushNotification push = PushNotification.builder().accountId(partnerAccountId).requesteeId(requesteeId)
				.message(message).title(title).actionId(actionId).type(NotificationType.CONNECTION).build();
		sendMessage(push);
	}

	/**
	 * is sent if a match was accepted by one party.
	 * 
	 * @param requesteeId
	 * @param partnerId
	 * @param actionId
	 */
	public void sendRequestMatch(long requesteeId, long partnerId, long actionId) {
		String name = getAccountName(requesteeId);
		String message = name + " would like to get in contact with you. Check out their profile";
		PushNotification push = new PushNotification();
		push.setMessage(message);
		push.setActionId(actionId);
		push.setTitle(title);
		push.setRequesteeId(requesteeId);
		push.setType(NotificationType.MATCHLIST);
		push.setAccountId(partnerId);
		sendMessage(push);
	}

	/**
	 * is sent if both parties have accepted a match
	 * 
	 * @param requesteeId
	 * @param partnerId
	 * @param actionId
	 */
	public void sendMatchRequestAccept(long requesteeId, long partnerId, long actionId) {
		String name = getAccountName(requesteeId);
		String message = name + " is now a lead. Open Rai$ing and start interacting!";
		PushNotification push = PushNotification.builder().accountId(partnerId).requesteeId(requesteeId)
				.message(message).title(title).actionId(actionId).type(NotificationType.REQUEST).build();
		sendMessage(push);
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

	private void sendMessage(PushNotification push) {
		try {
			Settings notificationSettings = settingRepo.findInfoByAccountId(push.getAccountId());
			if (notificationSettings.getToken() == null || notificationSettings.getToken().equals("")) {
				return; // we cannot send a notification if there is no token
			}
			if (!notificationSettings.getNotificationTypes().contains(NotificationType.NEVER)
					&& notificationSettings.getNotificationTypes().contains(push.getType())) {
				push.setToken(notificationSettings.getToken());
				fcmService.sendMessage(push);
			}
		} catch (DataAccessException | SQLException e) {
			LoggerFactory.getLogger(NotificationService.class).error("Could not retreive settings");
		} catch (InterruptedException | ExecutionException e) {
			LoggerFactory.getLogger(NotificationService.class)
					.error("Notification could not be sent: " + e.getMessage());
		}
	}

}

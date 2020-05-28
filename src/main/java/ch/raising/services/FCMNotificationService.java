package ch.raising.services;

import java.util.concurrent.ExecutionException;


import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import ch.raising.models.PushNotification;
/**
 * Service for creating and configuring the Firebase Cloud Messaging request for pushnotifications.
 * @author manus
 *
 */
@Service
public class FCMNotificationService {

	private final static String ANDROIDCLICKACTION = "NOTIFICATION";
	private final static String CHANNELID = "1";
	/**
	 * sends the Message to FCM
	 * @param notification to be sent
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void sendMessage(PushNotification notification) throws InterruptedException, ExecutionException {
		LoggerFactory.getLogger(FCMNotificationService.class).info("Sending message to FCM: " + notification);
		Message m = getMessage(notification);
		String response = FirebaseMessaging.getInstance().sendAsync(m).get();
		LoggerFactory.getLogger(FCMNotificationService.class).info("Sent message to FCM: " + response);
	}

	private Message getMessage(PushNotification notification) {
		return Message.builder()
				.setAndroidConfig(getAndroidConfig())
				.setApnsConfig(getApnsConfig(notification.getType().name()))
				.setToken(notification.getToken())
				.setNotification(new Notification(notification.getTitle(), notification.getMessage()))
				.putData("interaction", notification.getType().name())
				.putData("relationshipId", "" + notification.getActionId())
				.putData("accountId", "" + notification.getRequesteeId())
				.putData("click_action", notification.getType().name())
				.build();
	}

	private AndroidConfig getAndroidConfig() {
		return AndroidConfig.builder().setTtl(120000).setRestrictedPackageName("com.raising.app").setPriority(Priority.HIGH)
				.setNotification(AndroidNotification.builder().setClickAction(ANDROIDCLICKACTION).setChannelId(CHANNELID).build()).build();
	}

	private ApnsConfig getApnsConfig(String clickAction) {
		return ApnsConfig.builder().setAps(Aps.builder().setCategory(clickAction).setThreadId(clickAction).build()).build();
	}

}

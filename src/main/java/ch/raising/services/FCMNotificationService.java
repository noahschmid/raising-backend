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

@Service
public class FCMNotificationService {

	public void sendMessage(PushNotification notification) throws InterruptedException, ExecutionException {
		LoggerFactory.getLogger(FCMNotificationService.class).info("sending message: " + notification.getMessage());
		Message m = getMessage(notification);
		String response = FirebaseMessaging.getInstance().sendAsync(m).get();
		LoggerFactory.getLogger(FCMNotificationService.class).info("sent message: " + response);

	}

	private Message getMessage(PushNotification notification) {
		return Message.builder().setAndroidConfig(getAndroidConfig(notification.getClickAction())).setApnsConfig(getApnsConfig(notification.getClickAction()))
				.setToken(notification.getToken())
				.setNotification(new Notification(notification.getTitle(), notification.getMessage()))
				.build();
	}

	private AndroidConfig getAndroidConfig(String clickAction) {
		return AndroidConfig.builder().setTtl(120000).setRestrictedPackageName("com.raising.app").setPriority(Priority.HIGH)
				.setNotification(AndroidNotification.builder().setClickAction(clickAction).setChannelId("1").build()).build();
	}

	private ApnsConfig getApnsConfig(String clickAction) {
		return ApnsConfig.builder().setAps(Aps.builder().setCategory(clickAction).setThreadId(clickAction).build()).build();
	}

}

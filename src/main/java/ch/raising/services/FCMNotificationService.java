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
		return Message.builder().setAndroidConfig(getAndroidConfig(notification.getCollapseBy())).setApnsConfig(getApnsConfig(notification.getCollapseBy()))
				.setToken(notification.getToken())
				.setNotification(new Notification(notification.getTitle(), notification.getMessage()))
				.build();
	}

	private AndroidConfig getAndroidConfig(String collapseBy) {
		return AndroidConfig.builder().setTtl(120000).setCollapseKey(collapseBy).setPriority(Priority.NORMAL)
				.setNotification(AndroidNotification.builder().setTag(collapseBy).build()).build();
	}

	private ApnsConfig getApnsConfig(String collapseBy) {
		return ApnsConfig.builder().setAps(Aps.builder().setCategory(collapseBy).setThreadId(collapseBy).build()).build();
	}

}

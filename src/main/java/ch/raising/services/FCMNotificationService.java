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

import ch.qos.logback.core.util.Duration;
import ch.raising.models.PushNotification;
import ch.raising.models.enums.Device;

@Service
public class FCMNotificationService {

	public void sendMessage(PushNotification notification) throws InterruptedException, ExecutionException {
		LoggerFactory.getLogger(FCMNotificationService.class).info("sending message: " + notification.getMessage());

		Message m = getMessage(notification);
		String response = FirebaseMessaging.getInstance().sendAsync(m).get();
		LoggerFactory.getLogger(FCMNotificationService.class).info("sent message: " + response);

	}

	private Message getMessage(PushNotification notification) {
		return Message.builder().setAndroidConfig(getAndroidConfig()).setApnsConfig(getApnsConfig())
				.setToken(notification.getToken())
				.setNotification(new Notification(notification.getTitle(), notification.getMessage()))
				.build();
	}

	private AndroidConfig getAndroidConfig() {
		return AndroidConfig.builder().setTtl(120000).setCollapseKey("test").setPriority(Priority.NORMAL)
				.setNotification(AndroidNotification.builder().setTag("test").build()).build();
	}

	private ApnsConfig getApnsConfig() {
		return ApnsConfig.builder().setAps(Aps.builder().setCategory("test").setThreadId("test").build()).build();
	}

}

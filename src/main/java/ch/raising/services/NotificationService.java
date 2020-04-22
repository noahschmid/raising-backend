package ch.raising.services;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;

@Service
public class NotificationService {

	FirebaseMessaging m = FirebaseMessaging.getInstance();
	
	public NotificationService() {
		
	}
}

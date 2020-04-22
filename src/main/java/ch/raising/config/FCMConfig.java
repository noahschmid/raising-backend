package ch.raising.config;

import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FCMConfig {

	String firebaseCredentials = "/firebase_credentials/firebase_credentials.json";

	public FCMConfig() throws Exception {
		try {
			FirebaseOptions opt = new FirebaseOptions.Builder()
					.setCredentials(
							GoogleCredentials.fromStream(new ClassPathResource(firebaseCredentials).getInputStream()))
					.build();
			if(FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(opt);
			}
			LoggerFactory.getLogger(FCMConfig.class).info("FCM intialized.");
		} catch (Exception e) {
			LoggerFactory.getLogger(FCMConfig.class).error(e.getMessage());
			throw e;
		}
	}

}

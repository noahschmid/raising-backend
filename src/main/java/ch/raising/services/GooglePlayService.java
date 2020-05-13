package ch.raising.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisher.Purchases.Subscriptions;
import com.google.api.services.androidpublisher.AndroidPublisher.Purchases.Subscriptions.Get;
import com.google.api.services.androidpublisher.AndroidPublisherRequestInitializer;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;

@Service
public class GooglePlayService {
	
	private final static String PATH_TO_CREDENTIALS = "/googleapi_credentials/api-8284263320575791019-789165-c3ce80fd9122.json";
	private final static String PACKAGE_NAME = "com.raising.app";
	private final ObjectMapper mapper;
	private final JacksonFactory fac;
	private final GoogleCredential cred;
	
	private static final Logger Logger = LoggerFactory.getLogger(GooglePlayService.class);

	public GooglePlayService(MappingJackson2HttpMessageConverter mapper, JacksonFactory fac) throws IOException {
		this.mapper = mapper.getObjectMapper();
		this.fac = fac;
		this.cred = GoogleCredential
				.fromStream(new ClassPathResource(PATH_TO_CREDENTIALS).getInputStream())
				.createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));
	}

	public void verifyPurchaseToken(String token, String subscriptionId) {
		HttpTransport http = new NetHttpTransport();
		AndroidPublisher pub = new AndroidPublisher(http, fac, cred);
		try {
			Get sub = pub.purchases().subscriptions().get(PACKAGE_NAME, subscriptionId, token);	
			Logger.info("google api request response: {}", sub.entrySet());
		}catch(IOException e) {
			Logger.error("did not work");
		}
		
		
		
	}

}

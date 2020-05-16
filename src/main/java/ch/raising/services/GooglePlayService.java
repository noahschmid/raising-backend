package ch.raising.services;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.Arrays;
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
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisher.Purchases.Subscriptions;
import com.google.api.services.androidpublisher.AndroidPublisher.Purchases.Subscriptions.Get;
import com.google.api.services.androidpublisher.AndroidPublisherRequestInitializer;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.SubscriptionPurchase;
import com.google.firebase.auth.internal.GetAccountInfoResponse;

import ch.raising.models.AndroidSubscription;
import ch.raising.utils.InvalidSubscriptionException;

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
		this.cred = GoogleCredential.fromStream(new ClassPathResource(PATH_TO_CREDENTIALS).getInputStream())
				.createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));
	}

	public AndroidSubscription verifyPurchaseToken(String token, String subscriptionId) throws InvalidSubscriptionException {
		HttpTransport http = new NetHttpTransport();
		HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) throws IOException {
				cred.initialize(request);
			}
		};
		AndroidPublisher pubg = new AndroidPublisher.Builder(http, fac, httpRequestInitializer)
				.setApplicationName(PACKAGE_NAME).build();
		try {
			Get sub = pubg.purchases().subscriptions().get(PACKAGE_NAME, subscriptionId, token);
			SubscriptionPurchase resp = sub.execute();
			AndroidSubscription subscription = AndroidSubscription.builder()
												.expiresDate(resp.getExpiryTimeMillis())
												.purchaseToken(token)
												.orderId(resp.getOrderId())
												.subscriptionId(subscriptionId)
												.build();

			Logger.info("Play-API verified successfully for: {}", subscription);
			return subscription;
		} catch (IOException e) {
			Logger.error("Payment verification google play api error: " + e.getMessage());
			throw new InvalidSubscriptionException(e.getMessage());
		}

	}

}

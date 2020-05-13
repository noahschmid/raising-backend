package ch.raising.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;

@Service
public class GooglePlayService {

	private final static String PATH_TO_CREDENTIALS = "/googleapi_credentials/raising-32593-c3dc64321c53.json";
	private final static String PATH_TO_CREDENTIALS2 = "/googleapi_credentials/api-8284263320575791019-789165-c3ce80fd9122.json";
	private final static String API_KEY = "AIzaSyChpK3H-vexg_JcDZhnCLKMTGj-j-H-Dds";
	private final ObjectMapper mapper;

	private static final Logger Logger = LoggerFactory.getLogger(GooglePlayService.class);

	public GooglePlayService(MappingJackson2HttpMessageConverter mapper) {
		this.mapper = mapper.getObjectMapper();
	}

	public void verifyPurchaseToken(String token, String subscriptionId) throws FileNotFoundException, IOException {
		GoogleCredential cred = GoogleCredential
				.fromStream(new ClassPathResource(PATH_TO_CREDENTIALS2).getInputStream())
				.createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));
		
	}

}

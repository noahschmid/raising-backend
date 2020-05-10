package ch.raising.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.tomcat.util.json.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AppStoreService {

	private static final String PRODUCTION_URL = "https://buy.itunes.apple.com/verifyReceipt";
	private static final String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
	private static final String PASSWORD = "1f6d372d08144670841c6912cbe8539d";

	private static final Logger Logger = LoggerFactory.getLogger(AppStoreService.class);

	private final ObjectMapper mapper;
	private final URL productionUrl;
	private final URL sandboxUrl;

	public AppStoreService(MappingJackson2HttpMessageConverter mapper) throws MalformedURLException {
		this.productionUrl = new URL(PRODUCTION_URL);
		this.sandboxUrl = new URL(SANDBOX_URL);
		this.mapper = mapper.getObjectMapper();
	}

	public void verifyReceipt(String receipt) throws IOException {
		HashMap<String, String> requestData = new HashMap<String, String>();
		requestData.put("password", PASSWORD);
		requestData.put("receipt-data", receipt);
		requestData.put("exclude-old-transactions", "true");
		String json = mapper.writeValueAsString(requestData);
		sandboxRequest(json);
	}

	public void productionRequest(String receipt) {

	}

	public void sandboxRequest(String json) throws IOException {
		HttpsURLConnection con = (HttpsURLConnection) sandboxUrl.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");

		final OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

		wr.write(json);
		wr.flush();

		final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
		StringBuilder response = new StringBuilder();
		String nextline = br.readLine();

		while (nextline != null) {
			response.append(nextline.trim());
			nextline = br.readLine();
		}
		
		Logger.info(response.toString());
		
		
		JsonNode jn = mapper.readTree(response.toString());

		
		Logger.info("expires_date: {}",jn.findValue("expires_date_ms").asText());
		Logger.info("originalTransactionId: {}", jn.findValue("original_transaction_id").asText());
		Logger.info("latestReceiptData: {}", jn.findValue("latest_receipt").asText());
		
	}
}

package ch.raising.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import org.apache.tomcat.util.json.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.raising.models.IOSSubscription;
import ch.raising.utils.InvalidSubscriptionException;

@Service
public class IOSService {

	private static final String PRODUCTION_URL = "https://buy.itunes.apple.com/verifyReceipt";
	private static final String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
	private static final String PASSWORD = "1f6d372d08144670841c6912cbe8539d";
	

	private static final Logger Logger = LoggerFactory.getLogger(IOSService.class);

	private final ObjectMapper mapper;

	public IOSService(MappingJackson2HttpMessageConverter mapper) throws MalformedURLException {
		this.mapper = mapper.getObjectMapper();
	}

	/**
	 * 
	 * @param receipt
	 * @return a fully initialized receipt
	 * @throws InvalidSubscriptionException if the receipt could not be validated for any reason
	 */
	public IOSSubscription verifyReceipt(String receipt) throws InvalidSubscriptionException{
		IOSReceiptValidationRequest request = new IOSReceiptValidationRequest(receipt, PASSWORD, mapper);

		IOSSubscription response;
		try {
			response = productionRequest(request.toJson());
			
			if(response.getStatus() == 0) {
				Logger.info("Payment verified successfully for ios-production environment: {}", response.toString());
				return response;
			}else if(response.getStatus() == 21007){
				response = sandboxRequest(request.toJson());
				if(response.getStatus() != 0) {
					throw new InvalidSubscriptionException("cannot validate sandbox-receipt. status: " + response.getStatus());
				}else {
					Logger.info("Payment verified successfully for ios-sandbox environment: {}", response.toString());
					return response;
				}
			}else {
				throw new InvalidSubscriptionException("cannot validate production-receipt. status: " + response.getStatus());
			}
		} catch (IOException e) {
			throw new InvalidSubscriptionException("there was a problem regarding the json");
		}
		
	}

	public IOSSubscription productionRequest(String json) throws IOException {
		HttpsURLConnection con = prepareConnection(PRODUCTION_URL);
		sendReceipt(con, json);
		return receiveData(con);
	}

	public IOSSubscription sandboxRequest(String json) throws IOException {
		HttpsURLConnection con = prepareConnection(SANDBOX_URL);
		sendReceipt(con, json);
		return receiveData(con);
	}

	private IOSSubscription receiveData(HttpsURLConnection con)
			throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
		StringBuilder response = new StringBuilder();
		String nextline = br.readLine();

		while (nextline != null) {
			response.append(nextline.trim());
			nextline = br.readLine();
		}
		JsonNode jn = mapper.readTree(response.toString());
		int status = jn.findValue("status").asInt();
		if (status == 0) {
			String latestReceipt = jn.findValue("latest_receipt").asText();
			jn = jn.findValue("latest_receipt_info");
			return IOSSubscription.builder().status(status)
					.expiresDate(jn.findValue("expires_date_ms").asLong())
					.latestReceiptData(latestReceipt)
					.originalTransactionId(jn.findValue("original_transaction_id").asText())
					.subscriptionId(jn.findValue("product_id").asText()).build();
		} else {
			return IOSSubscription.builder().status(status).build();
		}
	}

	private void sendReceipt(HttpsURLConnection con, String json) throws IOException {
		final OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(json);
		wr.flush();
	}

	private HttpsURLConnection prepareConnection(String url) throws IOException {
		URL urlConnection = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) urlConnection.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		return con;
	}

	private class IOSReceiptValidationRequest {
		private String receipt;
		private String password;
		private String excludeOldTransactions;
		private ObjectMapper mapper;

		/**
		 * exclude-old-transactions is true by default
		 * 
		 * @param receipt
		 * @param password
		 */
		public IOSReceiptValidationRequest(String receipt, String password, ObjectMapper mapper) {
			this(receipt, password, mapper, true);
		}

		public IOSReceiptValidationRequest(String receipt, String password, ObjectMapper mapper,
				boolean excludeOldTransactions) {
			this.receipt = receipt;
			this.password = password;
			this.excludeOldTransactions = excludeOldTransactions ? "true" : "false";
			this.mapper = mapper;
		}

		public String toJson() throws JsonProcessingException {
			HashMap<String, String> requestData = new HashMap<String, String>();
			requestData.put("password", password);
			requestData.put("exclude-old-transactions", excludeOldTransactions);
			requestData.put("receipt-data", receipt);
			return mapper.writeValueAsString(requestData);
		}
	}
}

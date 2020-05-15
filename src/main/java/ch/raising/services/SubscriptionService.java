package ch.raising.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.raising.data.AndroidSubscriptionRepository;
import ch.raising.data.IOSSubscriptionRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.AndroidSubscription;
import ch.raising.models.IOSSubscription;
import ch.raising.utils.InvalidSubscriptionException;

/**
 * The class that is used to talk to the repositories aswell as the individual
 * platform depended services.
 * 
 * @author manus
 *
 */
@Service
public class SubscriptionService {

	private final IOSSubscriptionRepository iosRepo;
	private final AndroidSubscriptionRepository androidRepo;
	private final IOSService iosService;
	private final GooglePlayService googlePlayService;
	private final ObjectMapper mapper;

	private final static Logger Logger = LoggerFactory.getLogger(SubscriptionService.class);

	public SubscriptionService(IOSSubscriptionRepository iosRepo, AndroidSubscriptionRepository androidRepo,
			IOSService iosService, GooglePlayService googlePlayService, MappingJackson2HttpMessageConverter mapper) {
		this.iosRepo = iosRepo;
		this.androidRepo = androidRepo;
		this.iosService = iosService;
		this.googlePlayService = googlePlayService;
		this.mapper = mapper.getObjectMapper();
	}

	public IOSSubscription verifyIOSSubscription(String receipt)
			throws DataAccessException, InvalidSubscriptionException {
		long accountId = getAccountId();
		IOSSubscription resp = iosService.verifyReceipt(receipt);
		updateIOSRepository(accountId, resp);
		return resp;
	}

	private void updateIOSRepository(long accountId, IOSSubscription resp) {
		long expiresDate = iosRepo.getExpiresDateInMs(accountId);
		if (!iosRepo.hasEntry(accountId)) {
			iosRepo.addNewReceipt(resp, accountId);
		} else if (expiresDate < resp.getExpiresDate().getTime()) {
			iosRepo.updateReceipt(resp, accountId);
		}
	}

	public AndroidSubscription verifyAndroidSubscription(String purchaseToken, String subscriptionId)
			throws InvalidSubscriptionException {
		long accountId = getAccountId();
		AndroidSubscription resp = googlePlayService.verifyPurchaseToken(purchaseToken, subscriptionId);
		updateAndroidRepository(accountId, resp);
		return resp;
	}

	private void updateAndroidRepository(long accountId, AndroidSubscription resp) {
		long expiresDate = androidRepo.getExpiresDateInMs(accountId);
		if (!androidRepo.hasEntry(accountId)) {
			androidRepo.addNewPurchaseToken(resp, accountId);
		} else if (expiresDate < resp.getExpiresDate().getTime()) {
			androidRepo.updatePurchseToken(resp, accountId);
		}
	}

	public boolean hasIOSSubscription() {
		return hasIOSSubscription(getAccountId());
	}

	public boolean hasIOSSubscription(long id) {
		long accountId = getAccountId();
		long now = System.currentTimeMillis();
		if (now < iosRepo.getExpiresDateInMs(id)) {
			return true;
		} else {
			try {
				IOSSubscription receipt = iosRepo.getSubscriptionInfo(accountId);
				IOSSubscription verified = verifyIOSSubscription(receipt.getLatestReceiptData());
				if (verified.getExpiresDate().getTime() < now) {
					return false;
				} else {
					return true;
				}
			} catch (DataAccessException | SQLException e) {
				Logger.error("Status polling failed: could not retreive data from repository: {}", e.getMessage());
				return false;
			} catch (InvalidSubscriptionException e) {
				Logger.error("Status polling failed: could not verify receipt: {}", e.getMessage());
				return false;
			}
		}
	}

	public boolean hasAndroidSubscription(long accountId) {
		long now = System.currentTimeMillis();
		if (now < androidRepo.getExpiresDateInMs(accountId)) {
			return true;
		} else {
			try {
				AndroidSubscription oldToken = androidRepo.findSubscription(accountId);
				AndroidSubscription verified = verifyAndroidSubscription(oldToken.getPurchaseToken(),
						oldToken.getSubscriptionId());
				if (verified.getExpiresDate().getTime() < now) {
					return false;
				} else {
					return true;
				}
			} catch (DataAccessException e) {
				Logger.error("Status polling failed: could not retreive data from repository: {}", e.getMessage());
				return false;
			} catch (InvalidSubscriptionException e) {
				Logger.error("Status polling failed: could not verify receipt: {}", e.getMessage());
				return false;
			}
		}

	}

	public boolean isSubscribed(long id) {
		return hasAndroidSubscription(id) || hasIOSSubscription(id);
	}

	public Object getAndroidInfo(long accountId) {
		return androidRepo.findSubscription(accountId);
	}

	public IOSSubscription getIOSInfo(long accountId) throws DataAccessException, SQLException {
		return iosRepo.getSubscriptionInfo(accountId);
	}

	public boolean hasAndroidSubscription() {
		return hasAndroidSubscription(getAccountId());
	}

	public IOSSubscription getIOSInfo() throws DataAccessException, SQLException {
		return getIOSInfo(getAccountId());
	}

	public boolean isSubscribed() {
		return isSubscribed(getAccountId());
	}

	public Object getAndroidInfo() {
		return getAndroidInfo(getAccountId());
	}

	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}

	public void processAndroidPush(Map<String, String> json) throws InvalidSubscriptionException, JsonMappingException, JsonProcessingException {
		String purchaseToken = "";
		String subscriptionId = "";
		Logger.info("Got a push from GooglePlay-API");
//		JsonNode message = mapper.readTree(json.get("message"));
//		String base64Payload = message.findValue("data").asText();
//		String decodedPayload = new String(Base64.getDecoder().decode(base64Payload));
//		Logger.info("payload: {}", decodedPayload);
		//find fields in testnotification and do nothing (if not present in subscriptionNotification)
		//verifyAndroidSubscription(purchaseToken, subscriptionId);
	}

	public void processIOSPush(Map<String, String> json) throws DataAccessException, InvalidSubscriptionException, JsonMappingException, JsonProcessingException {
		Logger.info("Got a push from IOS-Server");//unified_receipt.latest_receipt
		JsonNode jn = mapper.readTree(json.get("unified_receipt"));
		String receipt = jn.findValue("latest_receipt").asText();
		verifyIOSSubscription(receipt);
	}
}

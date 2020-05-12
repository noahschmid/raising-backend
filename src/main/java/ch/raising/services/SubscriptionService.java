package ch.raising.services;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AndroidSubscriptionRepository;
import ch.raising.data.IOSSubscriptionRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.responses.IOSSubscription;
import ch.raising.utils.InvalidSubscriptionException;

/**
 * The class that is used to talk to the repositories aswell as the individual platform dependend services.
 * 
 * @author manus
 *
 */
@Service
public class SubscriptionService {

	private final IOSSubscriptionRepository iosRepo;
	private final AndroidSubscriptionRepository androidRepo;
	private final IOSService iosService;
	
	public SubscriptionService(IOSSubscriptionRepository iosRepo, AndroidSubscriptionRepository androidRepo, IOSService iosService) {
		this.iosRepo = iosRepo;
		this.androidRepo = androidRepo;
		this.iosService = iosService;
	}

	public void verifyIOSSubscription(String receipt) throws DataAccessException, InvalidSubscriptionException {
		long accountId = getAccountId();
		IOSSubscription resp = iosService.verifyReceipt(receipt);
		long expiresDate = iosRepo.getExpiresDateInMs(accountId);
		if (expiresDate == 0l) {
			iosRepo.addNewReceipt(resp, accountId);
		} else if(expiresDate < resp.getExpiresDate().getTime()) {
			iosRepo.updateReceipt(resp, accountId);
		}
	}

	public void updateAndroidSubscription(String purchaseToken, String subscriptionId) throws DataAccessException {
		long accountId = getAccountId();
		if (androidRepo.hasEntry(accountId)) {
			androidRepo.updateReceipt(purchaseToken, subscriptionId, accountId);
		} else {
			androidRepo.addNewReceipt(purchaseToken, subscriptionId, accountId);
		}
	}

	public boolean hasIOSSubscription() {
		return hasIOSSubscription(getAccountId());
	}

	public boolean hasIOSSubscription(long id) {
		long now = System.currentTimeMillis();
		if(now < iosRepo.getExpiresDateInMs(id))
			return true;
		else
			return false;
		/*
		 * a future feature is that if the subscription is expired, there will be a status poll with the latest receipt
		 */
	}

	public boolean hasAndroidSubscription() {
		return hasAndroidSubscription(getAccountId());
	}
	
	public  boolean hasAndroidSubscription(long accountId) {
		long now = System.currentTimeMillis();
		if(now < androidRepo.getExpiresDateInMs(accountId))
			return true;
		else
			return false;
	}

	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}
}

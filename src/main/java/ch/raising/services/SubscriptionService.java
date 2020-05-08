package ch.raising.services;

import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AndroidSubscriptionRepository;
import ch.raising.data.IOSSubscriptionRepository;
import ch.raising.models.AccountDetails;

/**
 * The class that is used to talk to the repositories and retreiving data. 
 * @author manus
 *
 */
@Service
public class SubscriptionService {

	private final IOSSubscriptionRepository iosRepo;
	private final AndroidSubscriptionRepository androidRepo;
	
	public SubscriptionService(IOSSubscriptionRepository iosRepo, AndroidSubscriptionRepository androidRepo) {
		this.iosRepo = iosRepo;
		this.androidRepo = androidRepo;
	}
	
	public void updateIOSSubscription(String receipt)  throws DataAccessException {
		long accountId = getAccountId();
		if(iosRepo.hasEntry(accountId)) {
			iosRepo.updateReceipt(receipt, accountId);
		}else {
			iosRepo.addNewReceipt(receipt, accountId);
		}
	}
	
	public void updateAndroidSubscription(String receipt) throws DataAccessException {
		long accountId = getAccountId();
		if(androidRepo.hasEntry(accountId)) {
			androidRepo.updateReceipt(receipt, accountId);
		}else {
			androidRepo.addNewReceipt(receipt, accountId);
		}
	}
	
	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}
}

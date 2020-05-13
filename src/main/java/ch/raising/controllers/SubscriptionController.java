package ch.raising.controllers;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.services.SubscriptionService;
import ch.raising.utils.InvalidSubscriptionException;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
	
	public final SubscriptionService subService;
	
	public SubscriptionController(SubscriptionService subService) {
		this.subService = subService;
	}
	
	@PatchMapping("/ios")
	public ResponseEntity<?> updateReceipt(@RequestBody Map<String, String> json) throws DataAccessException, InvalidSubscriptionException {
		subService.verifyIOSSubscription(json.get("receipt"));
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/ios/verify")
	public ResponseEntity<?> verifyReceipt(){
		if(subService.hasIOSSubscription())
			return ResponseEntity.ok().build();
		else
			return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build();
	}
	@GetMapping("/ios")
	public ResponseEntity<?> getSubscriptionInfo() throws DataAccessException, SQLException{
		return ResponseEntity.ok(subService.getIOSInfo());
	}
	
	@PatchMapping("/android")
	public ResponseEntity<?> updatePurchaseToken(@RequestBody Map<String, String> json) {
		subService.updateAndroidSubscription(json.get("purchaseToken"),json.get("subscriptionId"));
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/android/verify")
	public ResponseEntity<?> verifyPurchaseToken() {
		if(subService.hasAndroidSubscription())
			return ResponseEntity.ok().build();
		else
			return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build();
	}
	
	@GetMapping("/android")
	public ResponseEntity<?> getSubscriptionInfoToken() {
//		resp.put("subscriptionId","ch.swissef.raisingapp.subscription1y");
//		resp.put("expiresDate", "2020-05-10T09:53:42.028+0000");
//		resp.put("purchaseToken", "dbkaldngninmfnkoninbhope.AO-J1Ow2JVFxbA4PZzmQBpJecHKgY4osvzBHMnFMgzAQYRzD1GqoQZFGmHu4gi-wmXXbCB_ZAQECeAl6_nvUpxSyRYfhaOsVD05aWzJ46GHD8_w1csewojvtFO-Gz2t10jO9hS8B30GdS52hPpbYpWu5pSGQy3oebQ");
		return ResponseEntity.ok(subService.getAndroidInfo());

	}
}

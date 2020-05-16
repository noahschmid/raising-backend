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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ch.raising.models.AndroidSubscription;
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
	public ResponseEntity<?> updatePurchaseToken(@RequestBody Map<String, String> json) throws InvalidSubscriptionException {
		subService.verifyAndroidSubscription(json.get("purchaseToken"),json.get("subscriptionId"));
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/android/notify")
	public ResponseEntity<?> notifyAndroid(@RequestBody Map<String, Object> json) throws InvalidSubscriptionException, JsonMappingException, JsonProcessingException {
		subService.processAndroidPush(json);
		return ResponseEntity.ok().build();
	}
	
	//@PostMapping("/ios/notify")
	public ResponseEntity<?> notifyIOS(@RequestBody Map<String, String> json) throws InvalidSubscriptionException, DataAccessException, JsonMappingException, JsonProcessingException {
		subService.processIOSPush(json);
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
		AndroidSubscription sub = subService.getAndroidInfo();
		if(System.currentTimeMillis() < sub.getExpiresDate().getTime())
			return ResponseEntity.ok(sub);
		else
			return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build();

	}
}

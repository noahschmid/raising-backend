package ch.raising.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.services.SubscriptionService;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
	
	public final SubscriptionService subService;
	
	public SubscriptionController(SubscriptionService subService) {
		this.subService = subService;
	}
	
	@PatchMapping("/ios")
	public ResponseEntity<?> updateReceipt(@RequestBody Map<String, String> json) {
		subService.updateIOSSubscription(json.get("receipt"));
		return ResponseEntity.ok().build();
	}
	
	@PatchMapping("/android")
	public ResponseEntity<?> updatePurchaseToken(@RequestBody Map<String, String> json) {
		subService.updateAndroidSubscription(json.get("purchaseToken"));
		return ResponseEntity.ok().build();
	}

	
}

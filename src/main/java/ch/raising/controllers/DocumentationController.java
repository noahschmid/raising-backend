package ch.raising.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.raising.models.Account;
import ch.raising.models.ErrorResponse;
import ch.raising.models.Investor;
import ch.raising.models.Startup;

@Controller
@RequestMapping("/documentation")
public class DocumentationController {

	@GetMapping("/startup")
	public ResponseEntity<?> getStartupModel(){
		return ResponseEntity.ok().body(new ErrorResponse(new Startup()));
	}
	@GetMapping("/startup/update")
	public ResponseEntity<?> getStartupModelUpdate(){
		return ResponseEntity.ok().body(new ErrorResponse(Startup.startupBuilder().build()));
	}
	@GetMapping("/investor")
	public ResponseEntity<?> getInvestorModel(){
		return ResponseEntity.ok(new ErrorResponse(new Investor()));
	}
	@GetMapping("/investor/update")
	public ResponseEntity<?> getInvestorModelUpdate(){
		return ResponseEntity.ok(new ErrorResponse(Investor.investorBuilder().build()));
	}
	@GetMapping("/account")
	public ResponseEntity<?> getAccountModel(){
		return ResponseEntity.ok(new ErrorResponse(new Account()));
	}
}

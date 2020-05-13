package ch.raising.controllers;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.raising.models.LoginRequest;
import ch.raising.services.IOSService;
import ch.raising.services.AssignmentTableService;
import ch.raising.services.GooglePlayService;
import ch.raising.utils.InvalidSubscriptionException;

@Controller
@RequestMapping("/public")
public class PublicInformationController {
	
	private final AssignmentTableService publicInformationService;
	private final PasswordEncoder encoder;
	private final ObjectMapper mapper;
	private final GooglePlayService andService;
	
	 
	public PublicInformationController(AssignmentTableService pis, PasswordEncoder encoder, MappingJackson2HttpMessageConverter mapper, GooglePlayService andService) {
		this.publicInformationService =pis;
		this.encoder = encoder;
		this.mapper = mapper.getObjectMapper();
		this.andService = andService;
	}
	
	@PostMapping("/getHash")
	public ResponseEntity<?> getHash(@RequestBody LoginRequest hashes){
		String emailHash = encoder.encode(hashes.getEmail());
		String pwHash = encoder.encode(hashes.getPassword());
		return ResponseEntity.ok(new LoginRequest(emailHash, pwHash));
	}
	
	@PostMapping("/test")
	public ResponseEntity<?> test(@RequestBody Map<String, String> req) throws IOException, InvalidSubscriptionException{
		andService.verifyPurchaseToken(req.get("purchaseToken"), req.get("subscriptionId"));
		return ResponseEntity.ok().build();
	}
	
	@GetMapping
	public ResponseEntity<?> getAll() throws DataAccessException, SQLException, JsonProcessingException {
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(publicInformationService.getAllTables());
	}
	
	@GetMapping("/ticketsize")
	public ResponseEntity<?> ticketsize() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAll("ticketsize"));
	}
	
	@GetMapping("/continent")
	public ResponseEntity<?> getContinent() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAll("continent"));
	}
	
	@GetMapping("/country")
	public ResponseEntity<?> getCountry() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAll("country"));
	}
	
	@GetMapping("/industry")
	public ResponseEntity<?> getIndustry() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAllWithIcon("industry"));
	}
	
	@GetMapping("/investmentphase")
	public ResponseEntity<?> getInvestmentPhase() throws DataAccessException, SQLException{
		return ResponseEntity.ok( publicInformationService.getAllWithIcon("investmentphase"));
	}

	@GetMapping("/investortype")
	public ResponseEntity<?> getInvestorType() throws DataAccessException, SQLException{
		return ResponseEntity.ok( publicInformationService.getAllWithIcon("investortype"));
	}
	
	@GetMapping("/label")
	public ResponseEntity<?> getLabel() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAllWithIcon("label"));
	}
	
	@GetMapping("/support")
	public ResponseEntity<?> getSupport() throws DataAccessException, SQLException{
		return ResponseEntity.ok().body( publicInformationService.getAllWithIcon("support"));
	}
	
	@GetMapping("/corporatebody")
	public ResponseEntity<?> getCorporatebody() throws DataAccessException, SQLException{
		return ResponseEntity.ok().body(publicInformationService.getAll("corporatebody"));
	}
	
	@GetMapping("/financetype")
	public ResponseEntity<?> getFinanceType() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAll("financetype"));
	}
	
	@GetMapping("/revenue")
	public ResponseEntity<?> getRevenueSteps() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAll("revenue"));
	}
	
	@GetMapping("/boardmembertype")
	public ResponseEntity<?> getBoardmemberTypes() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAll("boardmemberType"));
	}
	
	@GetMapping("/subscriptions")
	public ResponseEntity<?> getSubscriptions(){
		List<String> list = new ArrayList<String>();
		list.add("ch.swissef.raisingapp.subscription1y");
		list.add("ch.swissef.raisingapp.subscription6m");
		list.add("ch.swissef.raisingapp.subscription3m");
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		map.put("subscriptions", list);
		return ResponseEntity.ok(map);
	}
	
 }

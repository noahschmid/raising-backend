package ch.raising.controllers;


import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.raising.models.LoginRequest;
import ch.raising.services.AssignmentTableService;

@Controller
@RequestMapping("/public")
public class PublicInformationController {
	
	private final AssignmentTableService publicInformationService;
	private final PasswordEncoder encoder;
	private final ObjectMapper mapper;
	
	 
	public PublicInformationController(AssignmentTableService pis, PasswordEncoder encoder, MappingJackson2HttpMessageConverter mapper) {
		this.publicInformationService =pis;
		this.encoder = encoder;
		this.mapper = mapper.getObjectMapper();
	}
	
	@PostMapping("/getHash")
	public ResponseEntity<?> getHash(@RequestBody LoginRequest hashes){
		String emailHash = encoder.encode(hashes.getEmail());
		String pwHash = encoder.encode(hashes.getPassword());
		return ResponseEntity.ok(new LoginRequest(emailHash, pwHash));
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
	
 }

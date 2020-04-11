package ch.raising.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.services.AssignmentTableService;

@Controller
@RequestMapping("/public")
public class PublicInformationController {
	
	
	AssignmentTableService publicInformationService;
	
	@Autowired
	public PublicInformationController(AssignmentTableService pis) {
		this.publicInformationService =pis;
	}
	
	@GetMapping
	public ResponseEntity<?> getAll() throws DataAccessException, SQLException {
		return ResponseEntity.ok(publicInformationService.getAllTables());
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
		return ResponseEntity.ok(publicInformationService.getAllCountries());
	}
	
	@GetMapping("/industry")
	public ResponseEntity<?> getIndustry() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAll("industry"));
	}
	
	@GetMapping("/investmentphase")
	public ResponseEntity<?> getInvestmentPhase() throws DataAccessException, SQLException{
		return ResponseEntity.ok( publicInformationService.getAllWithDescription("investortype"));
	}

	@GetMapping("/investortype")
	public ResponseEntity<?> getInvestorType() throws DataAccessException, SQLException{
		return ResponseEntity.ok( publicInformationService.getAllWithDescription("investortype"));
	}
	
	@GetMapping("/label")
	public ResponseEntity<?> getLabel() throws DataAccessException, SQLException{
		return ResponseEntity.ok(publicInformationService.getAllWithDescription("label"));
	}
	
	@GetMapping("/support")
	public ResponseEntity<?> getSupport() throws DataAccessException, SQLException{
		return ResponseEntity.ok().body( publicInformationService.getAll("support"));
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
		return ResponseEntity.ok(publicInformationService.getAll("financetype"));
	}
	
 }

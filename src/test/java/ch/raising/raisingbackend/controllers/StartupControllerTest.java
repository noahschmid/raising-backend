package ch.raising.raisingbackend.controllers;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.raising.models.Account;
import ch.raising.models.Boardmember;
import ch.raising.models.CorporateShareholder;
import ch.raising.models.Founder;
import ch.raising.models.Media;
import ch.raising.models.PrivateShareholder;
import ch.raising.utils.JwtUtil;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class StartupControllerTest extends AccountControllerTestBaseClass{

	private long investmentPhaseId = 12;
	private int boosts = 123;
	private int breakEvenYear = 2021;
	private int numberOfFte = 12;
	private int preMoneyValuation = 1111;
	private Date closingTime = new Date(System.currentTimeMillis());
	private int revenueMaxId = 5;
	private int revenueMinId = 7;
	private int scope = 3;
	private String uId = "CH-99";
	private int foundingYear = 2020;
	private long financeTypeId = 3;
	private int raised = 3214;
	private long videoId = -1;
	
	private List<Long> investorTypes;
	private List<Long> labels;
	private List<Boardmember> boardmembers;
	private List<Founder> founders;
	private List<PrivateShareholder> privateShareholders;
	private List<CorporateShareholder> corporateShareholders;

	@Autowired
	public StartupControllerTest(WebApplicationContext wac, JdbcTemplate jdbc, ObjectMapper objectMapper,
			BCryptPasswordEncoder encoder, JwtUtil jwt) {
		super(wac, jdbc, objectMapper, encoder, jwt);
	}
	
	
	@Override
	@BeforeAll
	public void setup() throws SQLException {
		super.setup();
		//TODO set up for startup
		
		
		
		insertStartupData();
	}
	
	@AfterAll
	public void insertStartupData() {
		//add startupsepcific data
	}
	
	@Override
	@AfterAll
	public void cleanup() {
		super.cleanup();
	}
	
	@Test
	public void testLogin() {
		//assert that startup is true and investor is false
		
	}
}

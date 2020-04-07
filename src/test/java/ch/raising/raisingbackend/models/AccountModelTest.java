package ch.raising.raisingbackend.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;
import ch.raising.models.Account;
import ch.raising.raisingbackend.data.RepositoryTestConfig;

@ContextConfiguration(classes = { RepositoryTestConfig.class })
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class AccountModelTest {

	protected long accountId = 1234;
	protected String company = "SoReal";
	protected String name = "Aloysius Pendergast";
	private String password = "secure";
	private String roles = "ROLE_TESTER";
	private String email = "so@real.ch";
	private String pitch = "diligent";
	private String description = "still diligent";
	private int ticketMinId = 10;
	private int ticketMaxId = 15;
	private String website = "testsite";

	private List<AssignmentTableModel> countries;
	private List<AssignmentTableModel> continents;
	private List<AssignmentTableModel> support;
	private List<AssignmentTableModel> industries;
	
	@Test
	public void testModelFromRepositoryWithBuilder() {
		Account acc = getFullRepoAccount();
		
		assertFalse(acc.isInComplete());
		assertEquals(accountId, acc.getAccountId());
		assertEquals(company, acc.getCompanyName());
		assertEquals(password, acc.getPassword());
		assertEquals(roles, acc.getRoles());
		assertEquals(email, acc.getEmail());
		assertEquals(pitch, acc.getPitch());
		assertEquals(description, acc.getDescription());
		assertEquals(ticketMaxId, acc.getTicketMaxId());
		assertEquals(ticketMinId, acc.getTicketMinId());
		assertEquals(website, acc.getWebsite());
	}
	@Test
	public void testBuilderNotAllFieldsInitialized() {
		Account acc = Account.accountBuilder().accountId(accountId).companyName(company).password(password)
				.roles(roles).email(email).description(description).ticketMinId(ticketMinId)
				.ticketMaxId(ticketMaxId).build();
		
		assertTrue(acc.isInComplete());
		assertEquals(accountId, acc.getAccountId());
		assertEquals(company, acc.getCompanyName());
		assertEquals(password, acc.getPassword());
		assertEquals(roles, acc.getRoles());
		assertEquals(email, acc.getEmail());
		assertNull( acc.getPitch());
		assertEquals(description, acc.getDescription());
		assertEquals(ticketMaxId, acc.getTicketMaxId());
		assertEquals(ticketMinId, acc.getTicketMinId());
	}

	@Test 
	public void testModelForFrontend() {
		Account acc = getFullRepoAccount();
		countries = new ArrayList<AssignmentTableModel>();
		continents = new ArrayList<AssignmentTableModel>();
		support = new ArrayList<AssignmentTableModel>();
		industries = new ArrayList<AssignmentTableModel>();
		
		countries.add(new Country("testland", 1, 1));
		continents.add(new AssignmentTableModel());
		support.add(new AssignmentTableModel());
		industries.add(new AssignmentTableModel());
		
		Account fullAcc = new Account(acc);
		fullAcc.setContinents(continents);
		fullAcc.setCountries(countries);
		fullAcc.setSupport(support);
		fullAcc.setIndustries(industries);
		
		assertNotNull(fullAcc.getContinents());
		assertNotNull(fullAcc.getCountries());
		assertNotNull(fullAcc.getIndustries());
		assertNotNull(fullAcc.getSupport());
		

		assertFalse(fullAcc.isInComplete());
		assertEquals(accountId, fullAcc.getAccountId());
		assertEquals(company, fullAcc.getCompanyName());
		assertEquals(password, fullAcc.getPassword());
		assertEquals(roles, fullAcc.getRoles());
		assertEquals(email, fullAcc.getEmail());
		assertEquals(pitch, fullAcc.getPitch());
		assertEquals(description, fullAcc.getDescription());
		assertEquals(ticketMaxId, fullAcc.getTicketMaxId());
		assertEquals(ticketMinId, fullAcc.getTicketMinId());
		assertEquals(website, fullAcc.getWebsite());
	}
	@Test
	public void testIfAdminNoInfoRequired() {
		Account acc = Account.accountBuilder().roles("ROLE_ADMIN").build();
		assertFalse(acc.isInComplete());
	}
	
	private Account getFullRepoAccount() {
		Account acc = Account.accountBuilder().accountId(accountId).companyName(company).password(password)
				.roles(roles).email(email).pitch(pitch).description(description).ticketMinId(ticketMinId)
				.ticketMaxId(ticketMaxId).website(website).build();
		
		List<AssignmentTableModel> countries = new ArrayList<AssignmentTableModel>();
		List<AssignmentTableModel> continents= new ArrayList<AssignmentTableModel>();
		List<AssignmentTableModel> support= new ArrayList<AssignmentTableModel>();
		List<AssignmentTableModel> industries= new ArrayList<AssignmentTableModel>();
		
		countries.add(new Country("testland", 1, 1));
		continents.add(new AssignmentTableModel());
		support.add(new AssignmentTableModel());
		industries.add(new AssignmentTableModel());
		
		acc.setCountries(countries);
		acc.setContinents(continents);
		acc.setSupport(support);
		acc.setIndustries(industries);
		return acc;
	}

}

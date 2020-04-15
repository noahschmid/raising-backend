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
import ch.raising.raisingbackend.data.TestConfig;

@ContextConfiguration(classes = { TestConfig.class })
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class AccountModelTest {

	protected long accountId = 1234;
	private String firstName = "Moby";
	private String lastName = "Dick";
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
	private long profilePictureId = 12;

	private List<Long> countries;
	private List<Long> continents;
	private List<Long> support;
	private List<Long> industries;

	@Test
	public void testModelFromRepositoryWithBuilder() {
		Account acc = getFullRepoAccount();

		assertTrue(acc.isComplete());
		assertEquals(firstName, acc.getFirstName());
		assertEquals(lastName, acc.getLastName());
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
		assertEquals(profilePictureId, acc.getProfilePictureId());
	}

	@Test
	public void testBuilderNotAllFieldsInitialized() {
		Account acc = Account.accountBuilder().accountId(accountId).companyName(company).password(password).roles(roles)
				.email(email).description(description).ticketMinId(ticketMinId).ticketMaxId(ticketMaxId).build();

		assertFalse(acc.isComplete());
		assertEquals(accountId, acc.getAccountId());
		assertEquals(company, acc.getCompanyName());
		assertEquals(password, acc.getPassword());
		assertEquals(roles, acc.getRoles());
		assertEquals(email, acc.getEmail());
		assertNull(acc.getPitch());
		assertEquals(description, acc.getDescription());
		assertEquals(ticketMaxId, acc.getTicketMaxId());
		assertEquals(ticketMinId, acc.getTicketMinId());
	}

	@Test
	public void testModelForFrontend() {
		Account acc = getFullRepoAccount();
		countries = new ArrayList<Long>();
		continents = new ArrayList<Long>();
		support = new ArrayList<Long>();
		industries = new ArrayList<Long>();

		countries.add(1L);
		continents.add(2l);
		support.add(3l);
		industries.add(4l);

		Account fullAcc = new Account(acc);
		fullAcc.setContinents(continents);
		fullAcc.setCountries(countries);
		fullAcc.setSupport(support);
		fullAcc.setIndustries(industries);

		assertNotNull(fullAcc.getContinents());
		assertNotNull(fullAcc.getCountries());
		assertNotNull(fullAcc.getIndustries());
		assertNotNull(fullAcc.getSupport());

		assertTrue(fullAcc.isComplete());
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
		Account acc = new Account();
		acc.setRoles("ROLE_ADMIN");
		assertFalse(acc.isComplete());
		acc.setEmail("asdf");
		acc.setPassword("asdfasdf");
		assertTrue(acc.isComplete());
	}

	private Account getFullRepoAccount() {
		Account acc = Account.accountBuilder().accountId(accountId).firstName(firstName).lastName(lastName)
				.profilePictureId(profilePictureId).companyName(company).password(password).roles(roles).email(email)
				.pitch(pitch).description(description).ticketMinId(ticketMinId).ticketMaxId(ticketMaxId)
				.website(website).build();

		List<Long> countries = new ArrayList<Long>();
		List<Long> continents = new ArrayList<Long>();
		List<Long> support = new ArrayList<Long>();
		List<Long> industries = new ArrayList<Long>();

		countries.add(1l);
		continents.add(1l);
		support.add(1l);
		industries.add(1l);

		acc.setCountries(countries);
		acc.setContinents(continents);
		acc.setSupport(support);
		acc.setIndustries(industries);
		return acc;
	}

}

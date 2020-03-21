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

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.Account;
import ch.raising.raisingbackend.data.TestConfig;

@ContextConfiguration(classes = { TestConfig.class })
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
	private int investmentMin = 10;
	private int investmentMax = 15;

	private List<IAssignmentTableModel> countries;
	private List<IAssignmentTableModel> continents;
	private List<IAssignmentTableModel> support;
	private List<IAssignmentTableModel> industries;
	
	@Test
	public void testModelFromRepositoryWithBuilder() {
		Account acc = getFullRepoAccount();
		
		assertFalse(acc.isInComplete());
		assertEquals(accountId, acc.getAccountId());
		assertEquals(company, acc.getCompany());
		assertEquals(password, acc.getPassword());
		assertEquals(roles, acc.getRoles());
		assertEquals(email, acc.getEmail());
		assertEquals(pitch, acc.getPitch());
		assertEquals(description, acc.getDesciription());
		assertEquals(investmentMax, acc.getInvestmentMax());
		assertEquals(investmentMin, acc.getInvestmentMin());
		assertEquals(name, acc.getName());
	}
	@Test
	public void testBuilderNotAllFieldsInitialized() {
		Account acc = Account.accountBuilder().accountId(accountId).company(company).name(null).password(password)
				.roles(roles).email(email).description(description).investmentMin(investmentMin)
				.investmentMax(investmentMax).build();
		
		assertTrue(acc.isInComplete());
		assertEquals(accountId, acc.getAccountId());
		assertEquals(company, acc.getCompany());
		assertEquals(password, acc.getPassword());
		assertEquals(roles, acc.getRoles());
		assertEquals(email, acc.getEmail());
		assertNull( acc.getPitch());
		assertEquals(description, acc.getDesciription());
		assertEquals(investmentMax, acc.getInvestmentMax());
		assertEquals(investmentMin, acc.getInvestmentMin());
		assertNull(acc.getName());
	}
	
	@Test 
	public void testModelForFrontend() {
		Account acc = getFullRepoAccount();
		countries = new ArrayList<IAssignmentTableModel>();
		continents = new ArrayList<IAssignmentTableModel>();
		support = new ArrayList<IAssignmentTableModel>();
		industries = new ArrayList<IAssignmentTableModel>();
		
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
		assertEquals(company, fullAcc.getCompany());
		assertEquals(password, fullAcc.getPassword());
		assertEquals(roles, fullAcc.getRoles());
		assertEquals(email, fullAcc.getEmail());
		assertEquals(pitch, fullAcc.getPitch());
		assertEquals(description, fullAcc.getDesciription());
		assertEquals(investmentMax, fullAcc.getInvestmentMax());
		assertEquals(investmentMin, fullAcc.getInvestmentMin());
		assertEquals(name, fullAcc.getName());
	}
	
	private Account getFullRepoAccount() {
		return Account.accountBuilder().accountId(accountId).company(company).name(name).password(password)
				.roles(roles).email(email).pitch(pitch).description(description).investmentMin(investmentMin)
				.investmentMax(investmentMax).build();
	}

}
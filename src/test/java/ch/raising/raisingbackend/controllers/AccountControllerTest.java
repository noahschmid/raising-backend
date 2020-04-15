package ch.raising.raisingbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import ch.raising.models.Account;
import ch.raising.models.ForgotPasswordRequest;
import ch.raising.models.FreeEmailRequest;
import ch.raising.models.LoginRequest;
import ch.raising.models.LoginResponse;
import ch.raising.models.PasswordResetRequest;
import ch.raising.utils.MapUtil;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class AccountControllerTest extends AccountControllerTestBaseClass {

	@Autowired
	public AccountControllerTest(WebApplicationContext wac, JdbcTemplate jdbc, ObjectMapper objectMapper,
			BCryptPasswordEncoder encoder) {
		super(wac,jdbc,objectMapper,encoder);
	}
	
	@Override
	@BeforeAll
	public void setup() throws SQLException {
		super.setup();
	}

	@Override
	@AfterAll
	public void cleanup() {
		super.cleanup();
	}
	
	@Test
	public void testLogin() throws Exception {
		UserDetails udet = mock(UserDetails.class);
		when(udet.getUsername()).thenReturn(email);
		LoginRequest login = new LoginRequest(account.getEmail(), account.getPassword());
		MvcResult req = mockMvc.perform(post("/account/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(login))).andExpect(status().is(200)).andReturn();
		LoginResponse result = objectMapper.readValue(req.getResponse().getContentAsString(), LoginResponse.class);
		assertEquals("" + accountId, "" + result.getId());
		assertEquals(false, result.isInvestor());
		assertEquals(false, result.isStartup());
		assertNull(result.getAccount());
		assertEquals(true, jwt.validateToken(result.getToken(), udet));
	}

	// @Test
	public void testForgot() throws JsonProcessingException, Exception {
		ForgotPasswordRequest fw = new ForgotPasswordRequest();
		fw.setEmail(email);
		mockMvc.perform(post("/account/forgot").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(fw))).andExpect(status().is(200));
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbc, "resetcode"));
	}

	// @Test
	public void testReset() throws JsonProcessingException, Exception {
		String sql = "INSERT INTO resetcode(code, expiresat, accountId) VALUES(?,?, ?)";
		jdbc.execute(sql, new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setInt(c++, 11111111);
				ps.setTimestamp(c++, new Timestamp(System.currentTimeMillis() + 60000));
				ps.setLong(c++, accountId);
				return ps.execute();
			}
		});
		PasswordResetRequest pwreset = new PasswordResetRequest("" + 11111111, "secure");
		MvcResult res = mockMvc.perform(post("/account/reset").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pwreset))).andExpect(status().is(200)).andReturn();
		// make sure that res contains loginresponse

		sql = "SELECT password FROM account WHERE id = " + accountId;
		String newPw = jdbc.queryForObject(sql, this::mapRowToPassword);
		assertEquals(true, encoder.matches("secure", newPw));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void getAccounts() throws Exception {
		Account expected = new Account(account);
		expected.setRoles("ROLE_USER");
		expected.setPassword(passwordHash);
		expected.setEmail(emailHash);
		MvcResult res = mockMvc.perform(get("/account")).andExpect(status().is(200)).andReturn();
		Account[] foundAccounts = objectMapper.readValue(res.getResponse().getContentAsString(), Account[].class);
		assertNotNull(foundAccounts);
		assertEquals(1, foundAccounts.length);
		Account found = foundAccounts[0];
		assertEquals(account.getAccountId(), found.getAccountId());
		assertEquals(account.getCompanyName(), found.getCompanyName());
		assertEquals(passwordHash, found.getPassword());
		assertEquals("ROLE_USER", found.getRoles());
		assertEquals(emailHash, found.getEmail());
		assertEquals(account.getDescription(), found.getDescription());
		assertEquals(account.getPitch(), found.getPitch());
		assertEquals(account.getTicketMaxId(), found.getTicketMaxId());
		assertEquals(account.getTicketMinId(), found.getTicketMinId());
		assertEquals(account.getCountryId(), found.getCountryId());
		assertEquals(account.getWebsite(), found.getWebsite());
//		assertEquals(account.getProfilePictureId(), found.getProfilePictureId());
		
	}
	@Test
	public void testIsEmailFree() throws JsonProcessingException, Exception {
		FreeEmailRequest freeEmail = new FreeEmailRequest();
		freeEmail.setEmail("emailIst@sicherNochFrei.ch");
		mockMvc.perform(post("/account/valid").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(freeEmail))).andExpect(status().is(200));

	}

	@Test
	public void testIsEmailTaken() throws JsonProcessingException, Exception {
		FreeEmailRequest email = new FreeEmailRequest();
		email.setEmail(account.getEmail());
		mockMvc.perform(post("/account/valid").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(email))).andExpect(status().is(400));
	}

	@Test
	public void testRegisterAccount() throws JsonProcessingException, Exception {
		Account expected = new Account(account);
		expected.setEmail("newtest@mail.ch");
		expected.setRoles("ROLE_SUPER_USER");
		MvcResult res = mockMvc.perform(post("/account/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(expected))).andExpect(status().is(200)).andReturn();
		LoginResponse login = objectMapper.readValue(res.getResponse().getContentAsString(), LoginResponse.class);

		assertNotNull(login);
		assertNotNull(login.getAccount());
		Account found = login.getAccount();
		expected.setAccountId(found.getAccountId());

		assertEquals(expected, found);
		
		cleanup();
		insertData();
	}

	@Test
	@WithUserDetails(email)
	public void testGetAccountById() throws Exception {
		MvcResult res = mockMvc.perform(get("/account/" + accountId)).andExpect(status().is(200)).andReturn();
		Account found = objectMapper.readValue(res.getResponse().getContentAsString(), Account.class); // does not work,
																										// dont know why yet
		assertNotNull(found);

	}

	@Test
	@WithUserDetails(email)
	public void testDeleteAccount() throws Exception {
		mockMvc.perform(delete("/account/" + accountId)).andExpect(status().is(200));
		JdbcTestUtils.countRowsInTable(jdbc, "account");
		insertData();

	}

	@Test
	@WithUserDetails(email)
	public void updateAccount() throws Exception {
		Account update = new Account(account);
		update.setAccountId(12);
		update.setCompanyName("NewComp");
		update.setPassword("secure");
		update.setRoles("SUPERUSERAD");
		update.setEmail("new@mail.co");
		update.setPitch("newpitch");
		update.setDescription("Lorem ipsum");
		update.setTicketMaxId(2);
		update.setTicketMaxId(5);
		update.setCountryId(12);
		update.setWebsite("suppenkopf.ch");
		
	
		MvcResult res= mockMvc.perform(patch("/account/" + accountId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(update))).andReturn();
		assertEquals(res.getResponse().getContentAsString(),200, res.getResponse().getStatus());
	Account updated = jdbc.queryForObject("SELECT * FROM account WHERE id = ?", new Object[] { accountId },
				MapUtil::mapRowToAccount);
		assertEquals(accountId, updated.getAccountId());
		assertNotEquals(updated.getAccountId(), update.getAccountId());
		assertEquals(update.getCompanyName(), updated.getCompanyName());
		assertNotEquals(update.getPassword(), updated.getPassword());
		assertEquals(null, updated.getPassword());
		assertEquals("ROLE_USER", updated.getRoles());
		assertNotEquals(update.getRoles(), updated.getRoles());
		assertEquals(true, encoder.matches(update.getEmail(), updated.getEmail()));
		assertEquals(update.getPitch(), updated.getPitch());
		assertEquals(update.getDescription(), updated.getDescription());
		assertEquals(update.getTicketMaxId(), updated.getTicketMaxId());
		assertEquals(update.getTicketMinId(), updated.getTicketMinId());
		assertEquals(update.getCountryId(), updated.getCountryId());
		assertEquals(update.getWebsite(), updated.getWebsite());
		cleanup();
		insertData();
	}

	@Test
	@WithUserDetails(email)
	public void testAddCountryToAccount() throws JsonProcessingException, Exception {
		List<Long> newCountry = new ArrayList<Long>();
		newCountry.add(6l);
		newCountry.add(7L);

		MvcResult res = mockMvc.perform(post("/account/country").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCountry))).andReturn();
		
		assertEquals(200, res.getResponse().getStatus());
		
		String sql = "select countryid from countryassignment where accountid = " + accountId;
		List<Long> found = jdbc.query(sql, MapUtil::mapRowToFirstEntry);
		for(Long l : newCountry) {
			assertTrue(found.contains(l));
		}
	}

	@Test
	@WithUserDetails(email)
	public void testAddContinentToAccount() throws JsonProcessingException, Exception {
		List<Long> newContinent = new ArrayList<Long>();
		newContinent.add(1l);
		newContinent.add(2L);

		MvcResult res = mockMvc.perform(post("/account/continent").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newContinent))).andReturn();
		
		assertEquals(200, res.getResponse().getStatus());
		
		String sql = "select continentid from continentassignment where accountid = " + accountId;
		List<Long> found = jdbc.query(sql, MapUtil::mapRowToFirstEntry);
		for(Long l : newContinent) {
			assertTrue(found.contains(l));
		}
	}

	@Test
	@WithUserDetails(email)
	public void testAddSupportToAccount() throws JsonProcessingException, Exception {
		List<Long> newSupport = new ArrayList<Long>();
		newSupport.add(1l);

		MvcResult res = mockMvc.perform(post("/account/support").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newSupport))).andReturn();
		
		assertEquals(200, res.getResponse().getStatus());
		
		String sql = "select supportid from supportassignment where accountid = " + accountId;
		List<Long> found = jdbc.query(sql, MapUtil::mapRowToFirstEntry);
		for(Long l : newSupport) {
			assertTrue(found.contains(l));
		}
	}

	@Test
	@WithUserDetails(email)
	public void testAddIndustryToAccount()  throws JsonProcessingException, Exception{
		List<Long> newIndustry = new ArrayList<Long>();
		newIndustry.add(9l);
		newIndustry.add(8l);

		MvcResult res = mockMvc.perform(post("/account/industry").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newIndustry))).andReturn();
		
		assertEquals(200, res.getResponse().getStatus());
		
		String sql = "select industryid from industryassignment where accountid = " + accountId;
		List<Long> found = jdbc.query(sql, MapUtil::mapRowToFirstEntry);
		for(Long l : newIndustry) {
			assertTrue(found.contains(l));
		}
	}

	@Test
	@WithUserDetails(email)
	public void testDeleteCountryToAccount() throws JsonProcessingException, Exception {
		List<Long> delCountry = new ArrayList<Long>();
		delCountry.add(countries.get(0));
		delCountry.add(countries.get(0));

		MvcResult res = mockMvc.perform(post("/account/country/delete").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(delCountry))).andReturn();
		
		assertEquals(200, res.getResponse().getStatus());
		
		String sql = "select countryid from countryassignment where accountid = " + accountId;
		List<Long> found = jdbc.query(sql, MapUtil::mapRowToFirstEntry);
		for(Long l : delCountry) {
			assertFalse(found.contains(l));
		}
	}

	@Test
	@WithUserDetails(email)
	public void testDeleteContinentToAccount() throws JsonProcessingException, Exception {
		List<Long> oldContinent = new ArrayList<Long>();
		oldContinent.add(continents.get(0));
		oldContinent.add(continents.get(0));

		MvcResult res = mockMvc.perform(post("/account/continent/delete").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(oldContinent))).andReturn();
		
		assertEquals(200, res.getResponse().getStatus());
		
		String sql = "select continentid from continentassignment where accountid = " + accountId;
		List<Long> found = jdbc.query(sql, MapUtil::mapRowToFirstEntry);
		for(Long l : oldContinent) {
			assertFalse(found.contains(l));
		}
	}

	@Test
	@WithUserDetails(email)
	public void testDeleteSupportToAccount() throws JsonProcessingException, Exception {
		List<Long> delpport = new ArrayList<Long>();
		delpport.add(support.get(0));

		MvcResult res = mockMvc.perform(post("/account/support/delete").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(delpport))).andReturn();
		
		assertEquals(200, res.getResponse().getStatus());
		
		String sql = "select supportid from supportassignment where accountid = " + accountId;
		List<Long> found = jdbc.query(sql, MapUtil::mapRowToFirstEntry);
		for(Long l : delpport) {
			assertFalse(found.contains(l));
		}
	}

	@Test
	@WithUserDetails(email)
	public void testDeleteIndustryToAccount() throws JsonProcessingException, Exception {
		List<Long> delIndustry = new ArrayList<Long>();
		delIndustry.add(industries.get(0));
		delIndustry.add(industries.get(1));

		MvcResult res = mockMvc.perform(post("/account/industry/delete").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(delIndustry))).andReturn();
		
		assertEquals(200, res.getResponse().getStatus());
		
		String sql = "select industryid from industryassignment where accountid = " + accountId;
		List<Long> found = jdbc.query(sql, MapUtil::mapRowToFirstEntry);
		for(Long l : delIndustry) {
			assertFalse(found.contains(l));
		}
	}

	// @Test
	void testAccountLifecycle() throws Exception {
		Account account = new Account();
		// empty registration request should return 400
		mockMvc.perform(post("/account/register").contentType("application/json")
				.content(objectMapper.writeValueAsString(account))).andExpect(status().is(500));

		// valid account registration request should return 200
		account.setEmail("email");
		account.setPassword("password");
		mockMvc.perform(post("/account/register").contentType("application/json")
				.content(objectMapper.writeValueAsString(account))).andExpect(status().isOk());
	}

	// @Test
	void testAccountValidEmail() throws Exception {
		FreeEmailRequest freeEmailRequest = new FreeEmailRequest();
		freeEmailRequest.setEmail("test@test.ch");

		Account account = new Account();
		account.setEmail("test@test.ch");
		account.setPassword("12345");

		mockMvc.perform(post("/account/valid").contentType("application/json")
				.content(objectMapper.writeValueAsString(freeEmailRequest))).andExpect(status().is(200));

		mockMvc.perform(post("/account/register").contentType("application/json")
				.content(objectMapper.writeValueAsString(account))).andExpect(status().is(200));

		mockMvc.perform(post("/account/valid").contentType("application/json")
				.content(objectMapper.writeValueAsString(freeEmailRequest))).andExpect(status().is(500));
	}

	public PreparedStatementCallback<Boolean> getAddAccountCallback(Account acc, String emailHash,
			String passwordHash) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setString(c++, acc.getFirstName());
				ps.setString(c++, acc.getLastName());
				ps.setString(c++, acc.getCompanyName());
				ps.setString(c++, passwordHash);
				ps.setString(c++, emailHash);
				ps.setString(c++, acc.getPitch());
				ps.setString(c++, acc.getDescription());
				ps.setInt(c++, acc.getTicketMinId());
				ps.setInt(c++, acc.getTicketMaxId());
				ps.setLong(c++, acc.getCountryId());
				ps.setLong(c++, acc.getProfilePictureId());
				return ps.execute();
			}
		};
	}

	private String mapRowToPassword(ResultSet rs, int row) throws SQLException {
		return rs.getString("password");
	}
}
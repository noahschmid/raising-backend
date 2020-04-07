package ch.raising.raisingbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import ch.raising.data.AccountRepository;
import ch.raising.models.Account;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.FreeEmailRequest;
import ch.raising.models.LoginRequest;
import ch.raising.models.LoginResponse;
import ch.raising.models.Media;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.MapUtil;
import ch.raising.utils.PreparedStatementUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;
import testutils.TestDataUtil;

import org.junit.jupiter.api.TestInstance.Lifecycle;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class AccountControllerTest {

	private MockMvc mockMvc;

	private WebApplicationContext wac;
	private ObjectMapper objectMapper;
	private JdbcTemplate jdbc;

	private BCryptPasswordEncoder encoder;

	protected long accountId = -1l;
	protected String companyName = "Umbrella Corp";
	private String password = "testword";
	private String roles = "ROLE_SUPER_USER";
	private String email = "test@test.ch";
	private String pitch = "testpitch";
	private String description = "testcription";
	private int ticketMinId = 3;
	private int ticketMaxId = 4;
	private long countryId = 123;
	private String website = "testsite.ch";
	private String emailHash;
	private String passwordHash;

	private Media profilePicture;
	private List<Media> gallery;
	private List<AssignmentTableModel> countries;
	private List<AssignmentTableModel> continents;
	private List<AssignmentTableModel> support;
	private List<AssignmentTableModel> industries;

	private final String TABLENAME = "account";
	private Account account;

	@Autowired
	public AccountControllerTest(WebApplicationContext wac, JdbcTemplate jdbc, ObjectMapper objectMapper,
			BCryptPasswordEncoder encoder) {
		this.wac = wac;
		this.jdbc = jdbc;
		this.objectMapper = objectMapper;
		this.encoder = encoder;
		this.emailHash = encoder.encode(email);
		this.passwordHash = encoder.encode(password);
	}

	@BeforeAll
	public void setup() throws SQLException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		account = Account.accountBuilder().accountId(accountId).companyName(companyName).password(password).roles(roles)
				.email(email).pitch(pitch).description(description).ticketMinId(ticketMinId).ticketMaxId(ticketMaxId)
				.countryId(countryId).website(website).build();

		profilePicture = new Media(TestDataUtil.getRandBytes(new Random()));
		gallery = TestDataUtil.getMedia();
		countries = TestDataUtil.getAssignmentTableModelList(6, 248, 10);
		continents = TestDataUtil.getAssignmentTableModelList(1, 7, 3);
		support = TestDataUtil.getAssignmentTableModelList(1, 3, 2);
		industries = TestDataUtil.getAssignmentTableModelList(6, 20, 4);

		account.setGallery(gallery);
		account.setCountries(countries);
		account.setContinents(continents);
		account.setSupport(support);
		account.setIndustries(industries);

		emailHash = encoder.encode(email);
		passwordHash = encoder.encode(password);

		insertData();
	}

	public void insertData() throws SQLException {
		String sql = "INSERT INTO " + TABLENAME
				+ " (companyname, password, emailhash, pitch, description, ticketminid, ticketmaxid, countryid, website) VALUES (?,?,?,?,?,?,?,?,?)";
		// use SQL_STATEMENT + RETURNING ID to get the new added accountid. (accountid
		// for startup, investor;

		PreparedStatement ps = jdbc.getDataSource().getConnection().prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		int c = 1;
		ps.setString(c++, account.getCompanyName());
		ps.setString(c++, passwordHash);
		ps.setString(c++, emailHash);
		ps.setString(c++, account.getPitch());
		ps.setString(c++, account.getDescription());
		ps.setInt(c++, account.getTicketMinId());
		ps.setInt(c++, account.getTicketMaxId());
		ps.setLong(c++, account.getCountryId());
		ps.setString(c++, account.getWebsite());

		if (ps.executeUpdate() > 0) {
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					accountId = rs.getLong(1);
				}
			}
		}

		for (AssignmentTableModel co : countries) {
			jdbc.execute("INSERT INTO countryassignment(accountid, countryid) VALUES (?,?);",
					PreparedStatementUtil.addEntryToAssignmentTableByAccountId(co.getId(), accountId));
		}
		for (AssignmentTableModel co : continents) {
			jdbc.execute("INSERT INTO continentassignment(accountid, continentid) VALUES (?,?);",
					PreparedStatementUtil.addEntryToAssignmentTableByAccountId(co.getId(), accountId));
		}
		for (AssignmentTableModel s : support) {
			jdbc.execute("INSERT INTO supportassignment(accountid, supportid) VALUES (?,?);",
					PreparedStatementUtil.addEntryToAssignmentTableByAccountId(s.getId(), accountId));
		}
		for (AssignmentTableModel i : industries) {
			jdbc.execute("INSERT INTO industryassignment(accountid, industryid) VALUES (?,?);",
					PreparedStatementUtil.addEntryToAssignmentTableByAccountId(i.getId(), accountId));
		}
		for (Media m : gallery) {
			jdbc.execute("INSERT INTO gallery(accountid, media) VALUES (?,?);",
					PreparedStatementUtil.addMediaByIdCallback(m, accountId));
		}
		jdbc.execute("INSERT INTO profilepicture(accountid, media) VALUES (?,?)",
				PreparedStatementUtil.addMediaByIdCallback(profilePicture, accountId));
	}

	@AfterAll
	public void cleanup() {
		jdbc.execute("DELETE FROM ACCOUNT;");
	}

	@Test
	public void testLogin() throws Exception {
		UserDetails udet = mock(UserDetails.class);
		when(udet.getUsername()).thenReturn(emailHash);
		JwtUtil jwt = new JwtUtil();
		LoginRequest login = new LoginRequest(account.getEmail(), account.getPassword());
		MvcResult req = mockMvc.perform(post("/account/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(login))).andExpect(status().is(200)).andReturn();
		TypeReference<HashMap<String,String>> typeRef = new TypeReference<HashMap<String, String>>(){};
		HashMap<String,String> result = objectMapper.readValue(req.getResponse().getContentAsString(),typeRef );
		assertTrue(result.containsKey("token"));
		assertTrue(result.containsKey("startup"));
		assertTrue(result.containsKey("investor"));
		assertTrue(result.containsKey("id"));
		assertEquals(""+accountId, result.get("id"));
		assertEquals("false", result.get("investor"));
		assertEquals("false", result.get("startup"));
		assertEquals(true, jwt.validateToken(result.get("token"), udet));
	}

	@Test
	public void testForgot() {

	}

	@Test
	public void testReset() {

	}

	@Test
	public void getAccounts() {

	}

	@Test
	public void testIsEmailFree() {

	}

	@Test
	public void testRegisterAccount() throws JsonProcessingException, Exception {

	}

	@Test
	public void testGetAccountById() {

	}

	@Test
	public void testDeleteAccount() {

	}

	@Test
	public void updateAccount() {

	}

	@Test
	public void testAddCountryToAccount() {

	}

	@Test
	public void testAddContinentToAccount() {

	}

	@Test
	public void testAddSupportToAccount() {

	}

	@Test
	public void testAddIndustryToAccount() {

	}

	@Test
	public void testAddGalleryOfAccount() {

	}

	@Test
	public void testDeleteImageFromAccount() {

	}

	@Test
	public void testGetGalleryOfAccount() {

	}

	@Test
	public void testAddProfilePicture() {

	}

	@Test
	public void testDeleteProfilePicture() {

	}

	@Test
	public void testGetProfilePictureOfAccount() {

	}

	//@Test
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

	//@Test
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
				ps.setString(c++, acc.getCompanyName());
				ps.setString(c++, passwordHash);
				ps.setString(c++, emailHash);
				ps.setString(c++, acc.getPitch());
				ps.setString(c++, acc.getDescription());
				ps.setInt(c++, acc.getTicketMinId());
				ps.setInt(c++, acc.getTicketMaxId());
				ps.setLong(c++, acc.getCountryId());
				return ps.execute();
			}
		};
	}
}
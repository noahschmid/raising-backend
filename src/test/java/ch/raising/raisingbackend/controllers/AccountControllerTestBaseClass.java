package ch.raising.raisingbackend.controllers;

import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.raising.models.Account;
import ch.raising.models.Media;
import ch.raising.utils.JwtUtil;
import ch.raising.utils.PreparedStatementUtil;
import io.jsonwebtoken.lang.Collections;
import testutils.TestDataUtil;

public class AccountControllerTestBaseClass {

	
	protected MockMvc mockMvc;
	protected WebApplicationContext wac;
	protected ObjectMapper objectMapper;
	protected JdbcTemplate jdbc;
	protected BCryptPasswordEncoder encoder;
	protected long accountId = -1l;
	private String firstName = "Marty";
	private String lastName = "Burd";
	protected String companyName = "Umbrella Corp";
	protected String password = "testword";
	private String roles = "ROLE_SUPER_USER";
	protected final String email = "test@test.ch";
	private String pitch = "testpitch";
	private String description = "testcription";
	private int ticketMinId = 3;
	private int ticketMaxId = 4;
	private long countryId = 123;
	private String website = "testsite.ch";
	protected final String emailHash;
	protected String passwordHash;
	private Media profilePicture;
	private List<Media> gallery;
	protected List<Long> countries;
	protected List<Long> continents;
	protected List<Long> support;
	protected List<Long> industries;
	protected final String TABLENAME;
	protected Account account;
	protected JwtUtil jwt;

	public AccountControllerTestBaseClass(WebApplicationContext wac, JdbcTemplate jdbc, ObjectMapper objectMapper,
			BCryptPasswordEncoder encoder) {
		this.wac = wac;
		this.jdbc = jdbc;
		this.objectMapper = objectMapper;
		this.encoder = encoder;
		this.emailHash = encoder.encode(email);
		this.passwordHash = encoder.encode(password);
		this.jwt = new JwtUtil();
		this.TABLENAME = "account";
	}

	public void setup() throws SQLException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		account = Account.accountBuilder().accountId(accountId).firstName(firstName).lastName(lastName)
				.companyName(companyName).password(password).roles(roles).email(email).pitch(pitch)
				.description(description).ticketMinId(ticketMinId).ticketMaxId(ticketMaxId).countryId(countryId)
				.website(website).build();

		profilePicture = new Media(TestDataUtil.getRandBytes(new Random()), "image/jpeg");
		gallery = TestDataUtil.getMedia();
		countries = Collections.arrayToList(new long[] { 11, 111, 232, 123, 132, 141, 212, 67 });
		continents = Collections.arrayToList(new Long[] { 4l, 5l, 6l });
		support = Collections.arrayToList(new Long[] { 2l, 3l });
		industries = Collections.arrayToList(new Long[] { 11l, 12l, 17l, 19l, 20l });

		account.setProfilePictureId(insertPicture(profilePicture, "profilepicture"));
		account.setGallery(insertGallery("gallery"));

		account.setCountries(countries);
		account.setContinents(continents);
		account.setSupport(support);
		account.setIndustries(industries);

		insertData();
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbc, "account"));
	}

	private List<Long> insertGallery(String tableName) throws SQLException {
		List<Long> ids = new ArrayList<Long>();
		for (Media m : gallery) {
			ids.add(insertPicture(m, tableName));
		}
		return ids;
	}

	private long insertPicture(Media picture, String tableName) throws SQLException, DataAccessException {
		long picId = -1;
		String sql = "INSERT INTO " + tableName + "(media) VALUES(?)";
		PreparedStatement ps = jdbc.getDataSource().getConnection().prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		int c = 1;
		ps.setBytes(c++, picture.getMedia());
		if (ps.executeUpdate() > 0) {
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				picId = rs.getLong(1);
			}
		}
		ps.close();
		ps.getConnection().close();
		return picId;
	}

	public void insertData() throws SQLException {
		String sql = "INSERT INTO " + TABLENAME
				+ " (firstname, lastname, companyname, password, emailhash, pitch, description, ticketminid, ticketmaxid, countryid, website, profilepictureid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement ps = jdbc.getDataSource().getConnection().prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		int c = 1;
		ps.setString(c++, account.getFirstName());
		ps.setString(c++, account.getLastName());
		ps.setString(c++, account.getCompanyName());
		ps.setString(c++, passwordHash);
		ps.setString(c++, emailHash);
		ps.setString(c++, account.getPitch());
		ps.setString(c++, account.getDescription());
		ps.setInt(c++, account.getTicketMinId());
		ps.setInt(c++, account.getTicketMaxId());
		ps.setLong(c++, account.getCountryId());
		ps.setString(c++, account.getWebsite());
		ps.setObject(c++, null);
		if (ps.executeUpdate() > 0) {
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				accountId = rs.getLong(1);
				account.setAccountId(accountId);
			}
		}
		ps.close();
		ps.getConnection().close();

		jdbc.execute("UPDATE profilepicture SET accountid = ? WHERE id = ?",
				PreparedStatementUtil.addEntryToAssignmentTableByAccountId(c, account.getProfilePictureId()));
		
		for(long g : account.getGallery()) {
			jdbc.execute("UPDATE gallery SET accountid = ? WHERE id = ?",
					PreparedStatementUtil.addEntryToAssignmentTableByAccountId(c, account.getProfilePictureId()));
		}
		
		
		for (Long co : countries) {
			jdbc.execute("INSERT INTO countryassignment(accountid, countryid) VALUES (?,?);",
					PreparedStatementUtil.addEntryToAssignmentTableByAccountId(co, accountId));
		}
		for (Long co : continents) {
			jdbc.execute("INSERT INTO continentassignment(accountid, continentid) VALUES (?,?);",
					PreparedStatementUtil.addEntryToAssignmentTableByAccountId(co, accountId));
		}
		for (Long s : support) {
			jdbc.execute("INSERT INTO supportassignment(accountid, supportid) VALUES (?,?);",
					PreparedStatementUtil.addEntryToAssignmentTableByAccountId(s, accountId));
		}
		for (Long i : industries) {
			jdbc.execute("INSERT INTO industryassignment(accountid, industryid) VALUES (?,?);",
					PreparedStatementUtil.addEntryToAssignmentTableByAccountId(i, accountId));
		}
	}

	public void cleanup() {
		jdbc.execute("DELETE FROM ACCOUNT;");
	}

}
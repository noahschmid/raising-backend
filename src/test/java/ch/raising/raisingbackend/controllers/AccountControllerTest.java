package ch.raising.raisingbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import ch.raising.models.Account;
import ch.raising.models.FreeEmailRequest;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;
import org.junit.jupiter.api.TestInstance.Lifecycle;


@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class AccountControllerTest {
    
    private MockMvc mockMvc;
  
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    long id = -1;
	Account account;
	Account account2;
	String tableName;
	String emailhash;
	String email;
    String name;
    
    @Autowired
	JdbcTemplate jdbc;

   //@BeforeAll
	public void setup() {
		tableName = "account";

		String createTable = QueryBuilder.getInstance().tableName(tableName).pair("id", Type.SERIAL)
				.pair("pitch", Type.VARCHAR).pair("description", Type.VARCHAR).pair("company", Type.VARCHAR)
				.pair("name", Type.VARCHAR).pair("password", Type.VARCHAR).pair("roles", Type.VARCHAR)
				.pair("emailhash", Type.VARCHAR).pair("ticketminid", Type.INT).pair("ticketmaxid", Type.INT)
				.createTable();

		jdbc.execute(createTable);
		
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

	}

	//@AfterAll
	public void cleanup() {
		String sql = QueryBuilder.getInstance().dropTable(tableName);
		jdbc.execute(sql);
	}

   //@Test
    void testAccountLifecycle() throws Exception {
        Account account = new Account();
        // empty registration request should return 400
        mockMvc.perform(post("/account/register")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(account)))
        .andExpect(status().is(500));

        // valid account registration request should return 200
        account.setEmail("email");
        account.setName("name");
        account.setPassword("password");
        mockMvc.perform(post("/account/register")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(account)))
        .andExpect(status().isOk());
    }

   //@Test
    void testAccountValidEmail() throws Exception {
        FreeEmailRequest freeEmailRequest = new FreeEmailRequest();
        freeEmailRequest.setEmail("test@test.ch");

        Account account = new Account();
        account.setEmail("test@test.ch");
        account.setName("Jon Doe");
        account.setPassword("12345");

        mockMvc.perform(post("/account/valid")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(freeEmailRequest)))
        .andExpect(status().is(200));

        mockMvc.perform(post("/account/register")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(account)))
        .andExpect(status().is(200));

        mockMvc.perform(post("/account/valid")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(freeEmailRequest)))
        .andExpect(status().is(500));
    }
}
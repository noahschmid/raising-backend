package ch.rasing.raisingbackend.data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import ch.raising.data.AccountRepository;
import ch.raising.data.BoardmemberRepository;
import ch.raising.models.Boardmember;

@ContextConfiguration(classes = {AccountRepository.class, DatabaseConfig.class})
@SpringBootTest
@ActiveProfiles("test")
public class AccountRepositoryTest {

	@Autowired
	BoardmemberRepository bmemRepo;

	@Autowired
	JdbcTemplate jdbc;
	

	@Test
	public void testGetAccount() {
		
	}
	
	

}

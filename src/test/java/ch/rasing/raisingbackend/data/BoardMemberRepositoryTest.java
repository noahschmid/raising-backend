//package ch.rasing.raisingbackend.data;
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Before;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.jdbc.JdbcTestUtils;
//
//import ch.raising.data.BoardmemberRepository;
//import ch.raising.models.Account;
//import ch.raising.models.Boardmember;
//import ch.raising.models.Startup;
//
//@ContextConfiguration(classes = {BoardmemberRepository.class, TestConfig.class})
//@SpringBootTest
//@ActiveProfiles("test")
//public class BoardMemberRepositoryTest {
//
//	@Autowired
//	BoardmemberRepository bmemRepo;
//
//	@Autowired
//	JdbcTemplate jdbc;
//	
//	
//	@Before
//	public void setup() {
//		
//	}
//
//	@Test
//	public void findABoardmember() {
//		Boardmember bmem = Boardmember.builder().name("test").education("testversity").profession("tester")
//				.pullDownType("test").pullDownDuration(12).build();
//		bmemRepo.addMemberByStartupId(bmem);
//		
//		assertEquals(1,JdbcTestUtils.countRowsInTable(jdbc, "boardmember"));
//	
//	}
//	
//	
//
//}

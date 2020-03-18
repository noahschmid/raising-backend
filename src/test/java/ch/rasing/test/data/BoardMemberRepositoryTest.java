package ch.rasing.test.data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import ch.raising.data.BoardmemberRepository;
import ch.raising.models.Boardmember;

@ContextConfiguration(classes = {BoardmemberRepository.class})
@SpringBootTest
@ActiveProfiles("test")
public class BoardMemberRepositoryTest {

	@Autowired
	BoardmemberRepository bmemRepo;

	

	@Test
	public void findABoardmember() {
		Boardmember bmem = Boardmember.builder().name("test").education("testversity").profession("tester")
				.pullDownType("test").pullDownDuration(12).build();
		bmemRepo.addByMember(bmem);
	
	}

}

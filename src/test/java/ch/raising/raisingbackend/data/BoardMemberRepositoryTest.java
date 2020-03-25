package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import ch.raising.data.BoardmemberRepository;
import ch.raising.models.Boardmember;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { TestConfig.class})
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class BoardMemberRepositoryTest {

	@Autowired
	JdbcTemplate jdbc;
	
	BoardmemberRepository bmemRepo = new BoardmemberRepository(jdbc);

	private long id = -1l;
	private long startupId;
	private String firstName;
	private String lastName;
	private String education;
	private String profession;
	private String position;
	private int membersince;
	private long countryId;
	Boardmember bmem;

	@BeforeAll
	public void setup() {
		bmemRepo = new BoardmemberRepository(jdbc);
		String sql = QueryBuilder.getInstance().tableName("boardmember").pair("id", Type.SERIAL)
				.pair("startupid", Type.BIGINT).pair("firstname", Type.VARCHAR).pair("lastname", Type.VARCHAR)
				.pair("education", Type.VARCHAR).pair("profession", Type.VARCHAR).pair("position", Type.VARCHAR)
				.pair("membersince", Type.VARCHAR).pair("countryId", Type.VARCHAR).createTable();
		jdbc.execute(sql);
	}

	@BeforeEach
	public void addMember() {
		String sql = QueryBuilder.getInstance().tableName("boardmember").attribute("startupid").attribute("firstname")
				.attribute("lastname").attribute("education").attribute("profession").attribute("position")
				.attribute("membersince").attribute("countryId").value(""+2).value("Aloysius").value("Pendergast").value("lawyer")
				.value("Special Agent").value("lawless").value("" + 10).value("" + 123).insert();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName("boardmember").whereEquals("firstname", "Aloysius").select();
		id = jdbc.queryForObject(sql,MapUtil::mapRowToId);
	}

	@AfterAll
	public void cleanup() {
		JdbcTestUtils.dropTables(jdbc, "boardmember");
	}

	@AfterEach
	public void deleteMember() {
		JdbcTestUtils.deleteFromTables(jdbc, "boardmember");
		jdbc.execute("ALTER SEQUENCE boardmember_id_seq RESTART WITH 1");
	}

	@Test
	public void testGetStartupIdByMemberId() {
		long id = bmemRepo.getStartupIdByMemberId(1);
		assertEquals(2, id);
	}

	@Test
	public void testAddMemberByStartupId() {
		Boardmember bmem = Boardmember.builder().startupid(7).firstName("Vincent").lastName("D' Agosta")
				.education("Detective").profession("Detective").membersince(9).coutryId(113).build();
		bmemRepo.addMemberByStartupId(bmem, 7);
		String sql = QueryBuilder.getInstance().tableName("boardmember").whereEquals("startupid", "" + 7).select();
		long id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
		assertEquals(2, id);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, "boardmember"));
	}

	@Test
	public void deleteMemberByStartupId() {
		bmemRepo.deleteMemberByStartupId(2);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, "boardmember"));
	}

	@Test
	public void testFindByStartupId() {
		List<Boardmember> bmems = bmemRepo.findByStartupId(2);
		assertNotNull(bmems);
		assertEquals(1, bmems.size());
		bmem = bmems.get(0);
		assertNotNull(bmem);
		assertEquals(1, bmem.getId());
		assertEquals("Aloysius", bmem.getFirstName());
		assertEquals("Pendergast", bmem.getLastName());
		assertEquals("Special Agent", bmem.getProfession());
		assertEquals("lawyer", bmem.getEducation());
		assertEquals("lawless", bmem.getPosition());
		assertEquals(10, bmem.getMembersince());
		assertEquals(123, bmem.getCountryId());
	}

	@Test
	public void testFind() {
		Boardmember found = bmemRepo.find(1);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals("Aloysius", found.getFirstName());
		assertEquals("Pendergast", found.getLastName());
		assertEquals("Special Agent", found.getProfession());
		assertEquals("lawyer", found.getEducation());
		assertEquals("lawless", found.getPosition());
		assertEquals(10, found.getMembersince());
		assertEquals(123, found.getCountryId());
	}

}

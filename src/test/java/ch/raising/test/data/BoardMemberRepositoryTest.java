package ch.raising.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
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
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import ch.raising.data.BoardmemberRepository;
import ch.raising.models.Boardmember;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = {TestConfig.class})
@SpringBootTest
@ActiveProfiles("RepositoryTest")
@TestInstance(Lifecycle.PER_CLASS)
public class BoardMemberRepositoryTest implements IAdditionalInformationTest{

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

	@Override
	@BeforeEach
	public void setup() {
		bmemRepo = new BoardmemberRepository(jdbc);
		String sql = QueryBuilder.getInstance().tableName("boardmember").pair("id", Type.SERIAL)
				.pair("startupid", Type.BIGINT).pair("firstname", Type.VARCHAR).pair("lastname", Type.VARCHAR)
				.pair("education", Type.VARCHAR).pair("profession", Type.VARCHAR).pair("position", Type.VARCHAR)
				.pair("membersince", Type.VARCHAR).pair("countryId", Type.VARCHAR).createTable();
		jdbc.execute(sql);
		addMember();
	}

	@Override
	public void addMember() {
		String sql = QueryBuilder.getInstance().tableName("boardmember").attribute("startupid").attribute("firstname")
				.attribute("lastname").attribute("education").attribute("profession").attribute("position")
				.attribute("membersince").attribute("countryId").value(""+2).value("Aloysius").value("Pendergast").value("lawyer")
				.value("Special Agent").value("lawless").value("" + 10).value("" + 123).insert();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName("boardmember").whereEquals("firstname", "Aloysius").select();
		id = jdbc.queryForObject(sql,MapUtil::mapRowToId);
	}

	@Override
	@AfterEach
	public void cleanup() {
		JdbcTestUtils.dropTables(jdbc, "boardmember");
	}

	@Test
	@Override
	public void testAddMemberByStartupId() throws DataAccessException, SQLException {
		Boardmember bmem = Boardmember.builder().startupId(7).firstName("Vincent").lastName("D' Agosta")
				.education("Detective").profession("Detective").memberSince(9).coutryId(113).build();
		bmemRepo.addMemberByStartupId(bmem, 7);
		String sql = QueryBuilder.getInstance().tableName("boardmember").whereEquals("startupid", "" + 7).select();
		long id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
		assertEquals(2, id);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, "boardmember"));
	}
	
	@Test
	@Override
	public void testFind() throws DataAccessException, SQLException {
		Boardmember found = bmemRepo.find(1);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals("Aloysius", found.getFirstName());
		assertEquals("Pendergast", found.getLastName());
		assertEquals("Special Agent", found.getProfession());
		assertEquals("lawyer", found.getEducation());
		assertEquals("lawless", found.getPosition());
		assertEquals(10, found.getMemberSince());
		assertEquals(123, found.getCountryId());
	}

	
	@Test
	@Override
	public void testGetStartupIdByMemberId() throws DataAccessException, SQLException {
		assertEquals(2, bmemRepo.getStartupIdByMemberId(1));
	}

	@Override
	@Test
	public void testDeleteMemberById() throws DataAccessException, SQLException {
		bmemRepo.deleteMemberById(1);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, "boardmember"));
	}

	@Override
	@Test
	public void testupdate() throws Exception {
		Boardmember bmem = Boardmember.builder().firstName("Moritz").lastName("Schönbächler")
				.education("Holzfäller").profession("Nope").memberSince(12).coutryId(13).build();
		bmemRepo.update(1, bmem);
		String sql = QueryBuilder.getInstance().tableName("boardmember").whereEquals("id", "1").select();
		Boardmember found = jdbc.queryForObject(sql, bmemRepo::mapRowToModel);
		assertEquals("Moritz", found.getFirstName());
		assertEquals("Schönbächler", found.getLastName());
		assertEquals("Holzfäller", found.getEducation());
		assertEquals("Nope", found.getProfession());
		assertEquals(12, found.getMemberSince());
		assertEquals(13, found.getCountryId());
	}

	@Override
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
		assertEquals(10, bmem.getMemberSince());
		assertEquals(123, bmem.getCountryId());
	}
}

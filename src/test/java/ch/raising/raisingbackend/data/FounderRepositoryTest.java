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

import ch.raising.data.FounderRepository;
import ch.raising.models.Founder;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { TestConfig.class })
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class FounderRepositoryTest implements IAdditionalInformationTest {

	@Autowired
	JdbcTemplate jdbc;

	FounderRepository fr;

	private long id = -1l;
	private long startupId;
	private String firstName;
	private String lastName;
	private String education;
	private String position;
	private long countryId;

	@BeforeAll
	@Override
	public void setup() {
		fr = new FounderRepository(jdbc);
		String sql = QueryBuilder.getInstance().tableName("founder").pair("id", Type.SERIAL)
				.pair("startupid", Type.BIGINT).pair("firstname", Type.VARCHAR).pair("lastname", Type.VARCHAR)
				.pair("education", Type.VARCHAR).pair("position", Type.VARCHAR).pair("countryid", Type.BIGINT)
				.createTable();
		jdbc.execute(sql);
	}

	@BeforeEach
	@Override
	public void addMember() {
		String sql = QueryBuilder.getInstance().tableName("founder")
				.attribute("startupid, firstname, lastname, education, position, countryid").value("" + 2)
				.value("Aloysius").value("Pendergast").value("Lawyer").value("Lawless").value("324").insert();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName("founder").whereEquals("firstname", "Aloysius").select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
	}

	@AfterAll
	@Override
	public void cleanup() {
		JdbcTestUtils.dropTables(jdbc, "founder");
	}

	@AfterEach
	@Override
	public void deleteMember() {
		JdbcTestUtils.deleteFromTables(jdbc, "founder");
		jdbc.execute("ALTER SEQUENCE founder_id_seq RESTART WITH 1");
	}

	@Test
	@Override
	public void testGetStartupByMemberId() {
		long suId = fr.getStartupIdByMemberId(1);
		assertEquals(2, suId);
	}

	@Test
	@Override
	public void testAddMemberByStartupId() {
		Founder founder = Founder.builder().startupid(5).firstName("Vincent").lastName("D' Agosta").education("Plumber")
				.position("Detective").countryId(345).build();
		fr.addMemberByStartupId(founder, 4);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, "founder"));

		String sql = QueryBuilder.getInstance().tableName("founder").whereEquals("firstname", "Vincent").select();
		Founder added = jdbc.queryForObject(sql, MapUtil::mapRowToFounder);
		assertEquals(2, added.getId());
		assertEquals(5, added.getStartupId());
		assertEquals("Vincent", added.getFirstName());
		assertEquals("D' Agosta", added.getLastName());
		assertEquals("Plumber", added.getEducation());
		assertEquals("Detective", added.getPosition());
		assertEquals(345, added.getCountryId());
	}

	@Test
	@Override
	public void testDeleteMemberByStartupId() {
		fr.deleteMemberByStartupId(1);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, "founder"));
	}

	@Test
	@Override
	public void testFindByStartupId() {
		List<Founder> foundList = fr.findByStartupId(2);
		assertNotNull(foundList);
		assertEquals(1, foundList.size());
		Founder found = foundList.get(0);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Aloysius", found.getFirstName());
		assertEquals("Pendergast", found.getLastName());
		assertEquals("Lawyer", found.getEducation());
		assertEquals("Lawless", found.getPosition());
		assertEquals(324, found.getCountryId());
	}

	@Test
	@Override
	public void testFind() {
		Founder found = fr.find(1);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Aloysius", found.getFirstName());
		assertEquals("Pendergast", found.getLastName());
		assertEquals("Lawyer", found.getEducation());
		assertEquals("Lawless", found.getPosition());
		assertEquals(324, found.getCountryId());
	}

}
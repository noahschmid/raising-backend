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

import ch.raising.data.ContactRepository;
import ch.raising.data.PrivateShareholderRepository;
import ch.raising.models.Contact;
import ch.raising.models.PrivateShareholder;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { TestConfig.class })
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class PrivateShareholderRepositoryTest implements IAdditionalInformationTest {

	@Autowired
	JdbcTemplate jdbc;

	PrivateShareholderRepository psr;

	private long id = -1l;
	private long startupId;
	private String firstName;
	private String lastName;
	private String city;
	private int equityShare;
	private long investortypeId;
	private long countryId;

	@BeforeAll
	@Override
	public void setup() {
		psr = new PrivateShareholderRepository(jdbc);
		String sql = QueryBuilder.getInstance().tableName("privateshareholder").pair("id", Type.SERIAL)
				.pair("startupid", Type.BIGINT).pair("firstname", Type.VARCHAR).pair("lastname", Type.VARCHAR)
				.pair("city", Type.VARCHAR).pair("equityshare", Type.INT).pair("investortypeid", Type.BIGINT)
				.pair("countryid", Type.BIGINT).createTable();
		jdbc.execute(sql);
	}

	@BeforeEach
	@Override
	public void addMember() {
		String sql = QueryBuilder.getInstance().tableName("privateshareholder")
				.attribute("startupid, firstname, lastname, city, equityShare, investortypeid, countryid").value("2")
				.value("Aloysius").value("Pendergast").value("New York").value("100").value("12").value("456").insert();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName("privateshareholder").whereEquals("firstname", "Aloysius").select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
	}

	@AfterAll
	@Override
	public void cleanup() {
		JdbcTestUtils.dropTables(jdbc, "privateshareholder");
	}

	@AfterEach
	@Override
	public void deleteMember() {
		JdbcTestUtils.deleteFromTables(jdbc, "privateshareholder");
		jdbc.execute("ALTER SEQUENCE privateshareholder_id_seq RESTART WITH 1");
	}

	@Test
	@Override
	public void testGetStartupIdByMemberId() {
		long suId = psr.getStartupIdByMemberId(1);
		assertEquals(2, suId);
	}

	@Test
	@Override
	public void testAddMemberByStartupId() {
		PrivateShareholder psh = PrivateShareholder.builder().startupid(7).firstName("Vincent").lastName("D' Agosta")
				.city("New York").equityShare(99).investortypeId(1).countryId(768).build();
		psr.addMemberByStartupId(psh, 7);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, "privateshareholder"));

		String sql = QueryBuilder.getInstance().tableName("privateshareholder").whereEquals("firstname", "Vincent")
				.select();
		PrivateShareholder added = jdbc.queryForObject(sql, MapUtil::mapRowToPrivateShareholder);
		assertEquals(2, added.getId());
		assertEquals(7, added.getStartupId());
		assertEquals("Vincent", added.getFirstName());
		assertEquals("D' Agosta", added.getLastName());
		assertEquals("New York", added.getCity());
		assertEquals(99, added.getEquityShare());
		assertEquals(1, added.getInvestortypeId());
		assertEquals(768, added.getCountryId());
	}

	@Test
	@Override
	public void testDeleteMemberByStartupId() {
		psr.deleteMemberByStartupId(1);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, "privateshareholder"));
	}

	@Test
	@Override
	public void testFindByStartupId() {
		List<PrivateShareholder> foundList = psr.findByStartupId(2);
		assertNotNull(foundList);
		assertEquals(1, foundList.size());
		PrivateShareholder found = foundList.get(0);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Aloysius", found.getFirstName());
		assertEquals("Pendergast", found.getLastName());
		assertEquals("New York", found.getCity());
		assertEquals(100, found.getEquityShare());
		assertEquals(12, found.getInvestortypeId());
		assertEquals(456, found.getCountryId());
	}

	@Test
	@Override
	public void testFind() {
		PrivateShareholder found = psr.find(1);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Aloysius", found.getFirstName());
		assertEquals("Pendergast", found.getLastName());
		assertEquals("New York", found.getCity());
		assertEquals(100, found.getEquityShare());
		assertEquals(12, found.getInvestortypeId());
		assertEquals(456, found.getCountryId());
	}

	@Override
	public void testupdate() throws Exception {
		PrivateShareholder psh = PrivateShareholder.builder().id(123).startupid(1000).firstName("Moritz").lastName("schönbächler")
				.city("Shangnau").equityShare(32).investortypeId(5).countryId(45).build();
		psr.update(1, psh);
		String sql = QueryBuilder.getInstance().tableName("boardmember").whereEquals("id", "1").select();
		PrivateShareholder found = jdbc.queryForObject(sql, psr::mapRowToModel);
		assertEquals(1,found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Moritz", found.getFirstName());
		assertEquals("schönbächler", found.getLastName());
		assertEquals("Shangnau", found.getCity());
		assertEquals(32, found.getEquityShare());
		assertEquals(5, found.getInvestortypeId());
		assertEquals(45, found.getCountryId());
	}

}

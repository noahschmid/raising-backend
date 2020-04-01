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
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import ch.raising.data.ContactRepository;
import ch.raising.models.Contact;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { RepositoryTestConfig.class })
@SpringBootTest
@ActiveProfiles("RepositoryTest")
@TestInstance(Lifecycle.PER_CLASS)
public class ContactRepositoryTest implements IAdditionalInformationTest {

	@Autowired
	JdbcTemplate jdbc;

	ContactRepository cr;

	private long id = -1l;
	private long startupId;
	private String firstName;
	private String lastName;
	private String position;
	private String email;
	private String phone;

	@BeforeEach
	@Override
	public void setup() {
		cr = new ContactRepository(jdbc);
		String sql = QueryBuilder.getInstance().tableName("contact").pair("id", Type.SERIAL)
				.pair("startupid", Type.BIGINT).pair("firstname", Type.VARCHAR).pair("lastname", Type.VARCHAR)
				.pair("position", Type.VARCHAR).pair("email", Type.VARCHAR).pair("phone", Type.VARCHAR).createTable();
		jdbc.execute(sql);
		addMember();
	}

	
	@Override
	public void addMember() {
		String sql = QueryBuilder.getInstance().tableName("contact")
				.attribute("startupid, firstname, lastname, position, email, phone").value("2").value("Aloysius")
				.value("Pendergast").value("Lawyer").value("so@real.ch").value("1234567890").insert();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName("contact").whereEquals("firstname", "Aloysius").select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
	}

	@AfterEach
	@Override
	public void cleanup() {
		JdbcTestUtils.dropTables(jdbc, "contact");
	}

	@Test
	@Override
	public void testGetStartupIdByMemberId() {
		long suId = cr.getStartupIdByMemberId(1);
		assertEquals(2, suId);
	}

	@Test
	@Override
	public void testAddMemberByStartupId() {
		Contact contact = Contact.builder().startupid(7).firstName("Vincent").lastName("D' Agosta")
				.position("Detective").email("da@gosta.ch").phone("9128471943891").build();
		cr.addMemberByStartupId(contact, 7);
		assertEquals(2, JdbcTestUtils.countRowsInTable(jdbc, "contact"));

		String sql = QueryBuilder.getInstance().tableName("contact").whereEquals("firstname", "Vincent").select();
		Contact added = jdbc.queryForObject(sql, MapUtil::mapRowToContact);
		assertEquals(2, added.getId());
		assertEquals(7, added.getStartupId());
		assertEquals("Vincent", added.getFirstName());
		assertEquals("D' Agosta", added.getLastName());
		assertEquals("da@gosta.ch", added.getEmail());
		assertEquals("Detective", added.getPosition());
		assertEquals("9128471943891", added.getPhone());
	}

	@Test
	@Override
	public void testDeleteMemberByStartupId() {
		cr.deleteMemberByStartupId(1);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, "contact"));
	}

	@Test
	@Override
	public void testFindByStartupId() {
		List<Contact> foundList = cr.findByStartupId(2);
		assertNotNull(foundList);
		assertEquals(1, foundList.size());
		Contact found = foundList.get(0);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Aloysius", found.getFirstName());
		assertEquals("Pendergast", found.getLastName());
		assertEquals("so@real.ch", found.getEmail());
		assertEquals("Lawyer", found.getPosition());
		assertEquals("" + 1234567890, found.getPhone());
	}

	@Test
	@Override
	public void testFind() {
		Contact found = cr.find(1);
		assertNotNull(found);
		assertEquals(1, found.getId());
		assertEquals(2, found.getStartupId());
		assertEquals("Aloysius", found.getFirstName());
		assertEquals("Pendergast", found.getLastName());
		assertEquals("so@real.ch", found.getEmail());
		assertEquals("Lawyer", found.getPosition());
		assertEquals("" + 1234567890, found.getPhone());
	}

	@Override
	public void testupdate() throws Exception {
		Contact contact = Contact.builder().startupid(99).firstName("Moritz").lastName("Schönbächler").position("CEO")
				.email("so@real.ch").phone("1234").build();
		cr.update(1, contact);
		String sql = QueryBuilder.getInstance().tableName("contact").whereEquals("id", "1").select();
		Contact found = jdbc.queryForObject(sql, cr::mapRowToModel);
		assertEquals(2, found.getStartupId());
		assertEquals("Moritz", found.getFirstName());
		assertEquals("Schönbächler", found.getLastName());
		assertEquals("CEO", found.getPosition());
		assertEquals("so@real.ch", found.getEmail());
		assertEquals("123", found.getPhone());
	}

}

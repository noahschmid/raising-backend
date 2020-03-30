package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotEquals;
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

import ch.raising.data.AssignmentTableRepository;
import ch.raising.models.AssignmentTableModel;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { TestConfig.class })
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class AssignmentTableTest {
	
	AssignmentTableRepository repo;

	@Autowired
	JdbcTemplate jdbc;
	
	String tableName;
	String tableIdName;
	String accountId = "accountid";
	int accountIdValue = 99;
	String assignmentTableName;
	String name;
	long tableEntryId;

	
	@BeforeAll
	public void setup() {
		tableName = "continent";
		name = "testcontinent";
		tableIdName = tableName + "Id";
		assignmentTableName = tableName + "assignment";
		
		repo = AssignmentTableRepository.getInstance(jdbc).withTableName(tableName);		
		
		String sql = QueryBuilder.getInstance().tableName(tableName).pair("id", Type.SERIAL).pair("name", Type.VARCHAR)
				.createTable();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName(assignmentTableName).pair(tableIdName, Type.BIGINT)
				.pair(accountId, Type.BIGINT).createTable();
		jdbc.execute(sql);
	}

	@BeforeEach
	public void addEntries() {
		String sql = QueryBuilder.getInstance().tableName(tableName).attribute("name").value(name).insert();
		jdbc.execute(sql);

		tableEntryId = getIdFor("name", tableName);
		sql = QueryBuilder.getInstance().tableName(assignmentTableName).attribute(accountId).attribute(tableIdName)
				.value("" + accountIdValue).value("" + tableEntryId).insert();
		
		jdbc.execute(sql);
	}
	
	public long getIdFor(String attribute, String nameOfTable) {
		long id;
		String sql = QueryBuilder.getInstance().tableName(nameOfTable).whereEquals(attribute, name).select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
		assertNotNull(id);
		return id;
	}
	
	@AfterAll
	public void cleanUp() {
		JdbcTestUtils.dropTables(jdbc, tableName);
		JdbcTestUtils.dropTables(jdbc, assignmentTableName);

	}

	@AfterEach
	public void deleteEntries() {
		JdbcTestUtils.deleteFromTables(jdbc, tableName);
		jdbc.execute("ALTER SEQUENCE "+tableName+"_id_seq RESTART WITH 1");
	}
	
	@Test
	public void testFind() {
		AssignmentTableModel model = repo.find(tableEntryId);
		assertEquals(name, model.getName());
	}

	@Test
	public void testFindAll() {
		List<AssignmentTableModel> models = repo.findAll();
		assertNotNull(models);
		assertEquals(1, models.size());
	}

	@Test
	public void testFindByAccountId() {
		List<AssignmentTableModel> models = repo.findByAccountId(99);
		assertNotNull(models);
		assertNotEquals(0, models.size());
		assertEquals(name, models.get(0).getName());
	}

	@Test
	public void testAddEntryToAccountById() {
		repo.addEntryToAccountById(tableEntryId, accountIdValue);
		int count = JdbcTestUtils.countRowsInTable(jdbc, assignmentTableName);
		assertEquals(2, count);
	}

	@Test
	public void deleteEntryFromAccountById() {
		repo.deleteEntryFromAccountById(tableEntryId, accountIdValue);
		int count = JdbcTestUtils.countRowsInTable(jdbc, assignmentTableName);
		assertEquals(0, count);
	}


}

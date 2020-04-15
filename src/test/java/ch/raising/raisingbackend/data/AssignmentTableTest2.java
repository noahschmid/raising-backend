package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotEquals;
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

import ch.raising.data.AssignmentTableRepository;
import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.AssignmentTableModel;
import ch.raising.utils.MapUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { RepositoryTestConfig.class })
@SpringBootTest
@ActiveProfiles("RepositoryTest")
@TestInstance(Lifecycle.PER_CLASS)
public class AssignmentTableTest2 {

	AssignmentTableRepository repo;

	
	JdbcTemplate jdbc;

	String tableName;
	String tableIdName;
	String accountId = "accountid";
	int accountIdValue = 99;
	String assignmentTableName;
	String name;
	long tableEntryId;

	
	@Autowired
	public AssignmentTableTest2(JdbcTemplate jdbc) {
		tableName = "continent";
		name = "testcontinent";
		tableIdName = tableName + "Id";
		assignmentTableName = tableName + "assignment";
		accountId = "startupid";
		this.jdbc = jdbc;
		repo = new AssignmentTableRepository(jdbc, tableName, accountId).withRowMapper(MapUtil::mapRowToAssignmentTableWithDescription);
	}

	@BeforeEach
	public void setup() {
		createTable();
		addEntries();
	}

	@AfterEach
	public void cleanUp() {
		JdbcTestUtils.dropTables(jdbc, tableName);
		JdbcTestUtils.dropTables(jdbc, assignmentTableName);

	}
	private void createTable() {
		String sql = QueryBuilder.getInstance().tableName(tableName).pair("description", Type.VARCHAR)
				.pair("id", Type.SERIAL).pair("name", Type.VARCHAR).createTable();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName(assignmentTableName).pair(tableIdName, Type.BIGINT)
				.pair(accountId, Type.BIGINT).createTable();
		jdbc.execute(sql);
	}

	private void addEntries() {
		String sql = QueryBuilder.getInstance().tableName(tableName).attribute("name").value(name).insert();
		jdbc.execute(sql);

		tableEntryId = getIdFor("name", tableName);
		sql = QueryBuilder.getInstance().tableName(assignmentTableName).attribute(accountId).attribute(tableIdName)
				.value("" + accountIdValue).value("" + tableEntryId).insert();

		jdbc.execute(sql);
	}

	private long getIdFor(String attribute, String nameOfTable) {
		long id;
		String sql = QueryBuilder.getInstance().tableName(nameOfTable).whereEquals(attribute, name).select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
		assertNotNull(id);
		return id;
	}

	@Test
	public void testFind() throws DataAccessException, SQLException {
		AssignmentTableModel model = repo.find(tableEntryId);
		assertEquals(name, model.getName());
	}

	@Test
	public void testFindAll() throws DataAccessException, SQLException {
		List<IAssignmentTableModel> models = repo.findAll();
		assertNotNull(models);
		assertEquals(1, models.size());
	}

	@Test
	public void testFindByAccountId() throws DataAccessException, SQLException {
		List<AssignmentTableModel> models = repo.findByAccountId(99);
		assertNotNull(models);
		assertNotEquals(0, models.size());
		assertEquals(name, models.get(0).getName());
	}

	@Test
	public void testAddEntryToAccountById() throws DataAccessException, SQLException {
		repo.addEntryToAccountById(tableEntryId, accountIdValue);
		int count = JdbcTestUtils.countRowsInTable(jdbc, assignmentTableName);
		assertEquals(2, count);
	}

	@Test
	public void deleteEntryFromAccountById() throws DataAccessException, SQLException {
		repo.deleteEntryFromAccountById(tableEntryId, accountIdValue);
		int count = JdbcTestUtils.countRowsInTable(jdbc, assignmentTableName);
		assertEquals(0, count);
	}

}

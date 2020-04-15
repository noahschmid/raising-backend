package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;


import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class AssignmentTableTest {

	AssignmentTableRepository repo;

	JdbcTemplate jdbc;

	String tableName;
	String tableIdName;
	String accountId = "accountid";
	int accountIdValue = 99;
	String assignmentTableName;
	String name;
	long tableEntryId;

	List<Long> entries;

	@Autowired
	public AssignmentTableTest(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		tableName = "continent";
		name = "testcontinent";
		tableIdName = tableName + "Id";
		assignmentTableName = tableName + "assignment";
		repo = new AssignmentTableRepository(jdbc,tableName);
	}

	@BeforeEach
	public void setup() {
		createTable();
		addEntries();
	}

	private void createTable() {
		String sql = QueryBuilder.getInstance().tableName(tableName).pair("id", Type.SERIAL).pair("name", Type.VARCHAR)
				.createTable();
		jdbc.execute(sql);
		sql = QueryBuilder.getInstance().tableName(assignmentTableName).pair(tableIdName, Type.BIGINT)
				.pair(accountId, Type.BIGINT).createTable();
		jdbc.execute(sql);
	}

	public void addEntries() {
		String sql = QueryBuilder.getInstance().tableName(tableName).attribute("name").value(name).insert();
		jdbc.execute(sql);

		tableEntryId = getIdFor("name", tableName);
		sql = QueryBuilder.getInstance().tableName(assignmentTableName).attribute(accountId).attribute(tableIdName)
				.value("" + accountIdValue).value("" + tableEntryId).insert();
		jdbc.execute(sql);

		entries = Lists.newArrayList(2l, 3l, 4l, 5l, 7l, 8l);
		sql = "INSERT INTO " + assignmentTableName + " (" + accountId + ", " + tableIdName + ") VALUES ";
		for (long l : entries) {
			sql += "(" + accountIdValue + ", " + l + "),";
		}
		sql = sql.substring(0, sql.length()-1); //Off-by-one-Error
		jdbc.execute(sql);
	}

	public long getIdFor(String attribute, String nameOfTable) {
		long id;
		String sql = QueryBuilder.getInstance().tableName(nameOfTable).whereEquals(attribute, name).select();
		id = jdbc.queryForObject(sql, MapUtil::mapRowToId);
		assertNotNull(id);
		return id;
	}

	@AfterEach
	public void cleanUp() {
		JdbcTestUtils.dropTables(jdbc, tableName);
		JdbcTestUtils.dropTables(jdbc, assignmentTableName);

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
		assertEquals(entries.size() + 2, count);
	}

	@Test
	public void deleteEntryFromAccountById() throws DataAccessException, SQLException {
		repo.deleteEntryFromAccountById(tableEntryId, accountIdValue);
		int count = JdbcTestUtils.countRowsInTable(jdbc, assignmentTableName);
		assertEquals(entries.size(), count);
	}

	@Test
	public void deleteEntriesFromAccount() throws DataAccessException, SQLException {
		repo.deleteEntriesByAccountId(accountIdValue);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, assignmentTableName));
	}

	@Test
	public void addEntriesToAccount() throws DataAccessException, SQLException {
		List<Long> newEntries = Lists.newArrayList(10l,11l,12l,13l);
		repo.addEntriesToAccount(accountIdValue, newEntries);
		String sql = "SELECT " + tableName + "id FROM "+ assignmentTableName +" WHERE accountid = " + accountIdValue;
		List<Long> found = jdbc.query(sql, MapUtil::mapRowToFirstEntry);
		for(long l : newEntries) {
			assertTrue(found.contains(l));
		}
	}

}

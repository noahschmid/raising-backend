package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.interfaces.IAssignmentTableRepository;
import ch.raising.models.AssignmentTableModel;
import ch.raising.utils.MapUtil;
import ch.raising.utils.functionalInterface.RowMapper;

public class AssignmentTableRepository {

	private final JdbcTemplate jdbc;
	private final String TABLENAME;
	private final String TABLE_ASSIGNMENT;
	private final String ACCOUNT_ID_NAME;
	private RowMapper<ResultSet, Integer, AssignmentTableModel> rowMapper;
	private RowMapper<ResultSet, Integer, Long> assignmentRowMapper;

	public AssignmentTableRepository(JdbcTemplate jdbc, String tableName) {
		this.TABLENAME = tableName;
		this.TABLE_ASSIGNMENT = tableName + "assignment";
		this.ACCOUNT_ID_NAME = "accountId";
		this.jdbc = jdbc;
		rowMapper = this::mapRowToAssignmentTable;
		assignmentRowMapper = MapUtil::mapRowToFirstEntry;
	}
	
	public AssignmentTableRepository(JdbcTemplate jdbc, String tableName, String accountIdName) {
		this.TABLENAME = tableName;
		this.TABLE_ASSIGNMENT = tableName + "assignment";
		this.ACCOUNT_ID_NAME = accountIdName;
		this.jdbc = jdbc;
		rowMapper = this::mapRowToAssignmentTable;
		assignmentRowMapper = MapUtil::mapRowToFirstEntry;
	}

	public AssignmentTableRepository withRowMapper(RowMapper<ResultSet, Integer, AssignmentTableModel> rowMapper) {
		this.rowMapper = rowMapper;
		return this;
	}

	public AssignmentTableModel find(long id) throws SQLException, DataAccessException {
		final String QUERY = "SELECT * FROM " + TABLENAME + " WHERE id = ?";
		return jdbc.queryForObject(QUERY, new Object[] { id }, rowMapper::mapRowToModel);
	}

	public List<IAssignmentTableModel> findAll() throws SQLException, DataAccessException {
		final String QUERY = "SELECT * FROM " + TABLENAME;
		return jdbc.query(QUERY, rowMapper::mapRowToModel);
	}

	public List<AssignmentTableModel> findByAccountId(long accountId) throws SQLException, DataAccessException {
		final String QUERY = "SELECT * FROM " + TABLE_ASSIGNMENT + " INNER JOIN " + TABLENAME + " ON " + TABLE_ASSIGNMENT
				+ "." + TABLENAME + "Id = " + TABLENAME + ".id WHERE " + ACCOUNT_ID_NAME + " = ?";
		return jdbc.query(QUERY, new Object[] { accountId }, rowMapper::mapRowToModel);
	}

	public List<Long> findIdByAccountId(long accountId) {
		return jdbc.query("SELECT " + TABLENAME + "id FROM " + TABLE_ASSIGNMENT + " WHERE " + ACCOUNT_ID_NAME + "=?",
				new Object[] { accountId }, assignmentRowMapper::mapRowToModel);
	}

	public void addEntryToAccountById(long id, long accountId) throws SQLException, DataAccessException {
		final String QUERY = "INSERT INTO " + TABLE_ASSIGNMENT + "(" + ACCOUNT_ID_NAME + ", " + TABLENAME
				+ "Id) VALUES (?, ?);";
		jdbc.execute(QUERY, getIdAccountIdCallback(id, accountId));
	}

	public void deleteEntryFromAccountById(long id, long accountId) throws SQLException, DataAccessException {
		final String QUERY = "DELETE FROM " + TABLE_ASSIGNMENT + " WHERE " + ACCOUNT_ID_NAME + " = ? AND " + TABLENAME
				+ "id = ?";
		jdbc.execute(QUERY, getIdAccountIdCallback(id, accountId));
	}

	public void deleteEntriesByAccountId(long accountId) {
		final String QUERY = "DELETE FROM " + TABLE_ASSIGNMENT + " WHERE " + ACCOUNT_ID_NAME + " = ?";
		jdbc.execute(QUERY, getAccountIdCallback(accountId));
	}

	public void addEntriesToAccount(long accountId, List<Long> models) {
		if (models == null || models.isEmpty())
			return;

		String sql = "INSERT INTO " + TABLE_ASSIGNMENT + "(" + ACCOUNT_ID_NAME + ", " + TABLENAME + "Id) VALUES ";
		for (int m = 0; m < models.size(); m++) {
			sql += "(?, ?),";
		}
		sql = sql.substring(0, sql.length() - 1); // Off-by-one-Error
		jdbc.execute(sql, getAddIdsCallback(accountId, models));
	}

	public void updateAssignment(int accountId, List<Long> models) {
		if(models != null && models.size() != 0) {
			deleteEntriesByAccountId(accountId);
			addEntriesToAccount(accountId, models);
		}
	}

	private PreparedStatementCallback<Boolean> getAddIdsCallback(long accountId, List<Long> models) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				for (long m : models) {
					ps.setLong(c++, accountId);
					ps.setLong(c++, m);
				}
				return ps.execute();
			}
		};
	}

	private PreparedStatementCallback<Boolean> getIdAccountIdCallback(long id, long accountId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, accountId);
				ps.setLong(2, id);
				return ps.execute();
			}
		};
	}

	private PreparedStatementCallback<Boolean> getAccountIdCallback(long accountId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, accountId);
				return ps.execute();
			}
		};
	}
	
	private AssignmentTableModel mapRowToAssignmentTable(ResultSet rs, int row) throws SQLException {
		return new AssignmentTableModel(rs.getString("name"), rs.getInt("id"));
	}

}

package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.interfaces.IAssignmentTableRepository;
import ch.raising.models.AssignmentTableModel;
import ch.raising.utils.MapUtil;
import ch.raising.utils.functionalInterface.RowMapper;

public class AssignmentTableRepository {

	private JdbcTemplate jdbc;
	private String tableName = "";
	private String tableAssignment;
	protected RowMapper<ResultSet, Integer, AssignmentTableModel> rowMapper;
	protected RowMapper<ResultSet, Integer, Long> assignmentRowMapper;
	private String accountIdName = "accountId";

	private AssignmentTableRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		rowMapper = MapUtil::mapRowToAssignmentTable;
		assignmentRowMapper = MapUtil::mapRowToAssignmentTableId;
	}

	public static AssignmentTableRepository getInstance(JdbcTemplate jdbc) {
		return new AssignmentTableRepository(jdbc);
	}

	public AssignmentTableRepository withTableName(String tableName) {
		this.tableName = tableName;
		this.tableAssignment = tableName + "assignment";
		return this;
	}

	public AssignmentTableRepository withRowMapper(RowMapper<ResultSet, Integer, AssignmentTableModel> rowMapper) {
		this.rowMapper = rowMapper;
		return this;
	}

	public AssignmentTableRepository withAccountIdName(String accountIdName) {
		this.accountIdName = accountIdName;
		return this;
	}

	public AssignmentTableModel find(long id) throws SQLException, DataAccessException {
		final String QUERY = "SELECT * FROM " + tableName + " WHERE id = ?";
		return jdbc.queryForObject(QUERY, new Object[] { id }, rowMapper::mapRowToModel);
	}

	public List<IAssignmentTableModel> findAll() throws SQLException, DataAccessException {
		final String QUERY = "SELECT * FROM " + tableName;
		return jdbc.query(QUERY, rowMapper::mapRowToModel);
	}

	public List<AssignmentTableModel> findByAccountId(long accountId) throws SQLException, DataAccessException {
		final String QUERY = "SELECT * FROM " + tableAssignment + " INNER JOIN " + tableName + " ON " + tableAssignment
				+ "." + tableName + "Id = " + tableName + ".id WHERE " + accountIdName + " = ?";
		return jdbc.query(QUERY, new Object[] { accountId }, rowMapper::mapRowToModel);
	}

	public List<Long> findIdByAccountId(long accountId) {
		return jdbc.query("SELECT " + tableName + "id FROM " + tableAssignment + " WHERE " + accountIdName + "=?",
				new Object[] { accountId }, assignmentRowMapper::mapRowToModel);
	}

	public void addEntryToAccountById(long id, long accountId) throws SQLException, DataAccessException {
		final String QUERY = "INSERT INTO " + tableAssignment + "(" + accountIdName + ", " + tableName
				+ "Id) VALUES (?, ?);";
		jdbc.execute(QUERY, getIdAccountIdCallback(id, accountId));
	}

	public void deleteEntryFromAccountById(long id, long accountId) throws SQLException, DataAccessException {
		final String QUERY = "DELETE FROM " + tableAssignment + " WHERE " + accountIdName + " = ? AND " + tableName
				+ "id = ?";
		jdbc.execute(QUERY, getIdAccountIdCallback(id, accountId));
	}

	public void deleteEntriesByAccountId(long accountId) {
		final String QUERY = "DELETE FROM " + tableAssignment + " WHERE " + accountIdName + " = ?";
		jdbc.execute(QUERY, getAccountIdCallback(accountId));
	}

	public void addEntriesToAccount(long accountId, List<Long> models) {
		if (models == null)
			return;

		String sql = "INSERT INTO " + tableAssignment + "(" + accountIdName + ", " + tableName + "Id) VALUES ";
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

}

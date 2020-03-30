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
	private String accountIdName = "accountId";
	
	private AssignmentTableRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		rowMapper = MapUtil::mapRowToAssignmentTable;
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

	public AssignmentTableModel find(long id) {
		return jdbc.queryForObject("SELECT * FROM " + tableName + " WHERE id = ?", new Object[] { id },
				rowMapper::mapRowToModel);
	}

	public List<AssignmentTableModel> findAll() {
		return jdbc.query("SELECT * FROM " + tableName, rowMapper::mapRowToModel);
	}

	public List<AssignmentTableModel> findByAccountId(long accountId) {
		return jdbc.query(
				"SELECT * FROM " + tableAssignment + " INNER JOIN " + tableName + " ON " + tableAssignment
						+ "."+tableName+"Id = " + tableName + ".id WHERE "+accountIdName+" = ?",
				new Object[] { accountId }, rowMapper::mapRowToModel);
	}

	public void addEntryToAccountById(long id, long accountId) {
		String query = "INSERT INTO " + tableAssignment + "("+accountIdName+", "+ tableName +"Id) VALUES (?, ?);";
		jdbc.execute(query, getAddEntryToAccountById(id, accountId));
	}

	public void deleteEntryFromAccountById( long id, long accountId) {
		String query = "DELETE FROM "+ tableAssignment +" WHERE "+accountIdName+" = ? AND "+ tableName +"id = ?";
		jdbc.execute(query, getDeleteEntryFromAccountById(id, accountId));
	}
	
	private PreparedStatementCallback<Boolean> getAddEntryToAccountById(long id, long accountId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, accountId);
				ps.setLong(2, id);
				return ps.execute();
			}
		};
	}
	
	private PreparedStatementCallback<Boolean> getDeleteEntryFromAccountById(long id, long accountId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(2, id);
				ps.setLong(1, accountId);
				return ps.execute();
			}
		};
	}

}

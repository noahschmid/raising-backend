package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.interfaces.IAssignmentTableRepository;
import ch.raising.models.AssignmentTableModel;
import ch.raising.utils.MapUtil;
import ch.raising.utils.RowMapper;

public class AssignmentTableRepository {

	private JdbcTemplate jdbc;
	private String tableName;
	private String tableAssignment;
	protected RowMapper<ResultSet, Integer, IAssignmentTableModel> rowMapper;

	/**
	 * uses the default rowmapper
	 * @param jdbc
	 * @param tableName
	 */
	public AssignmentTableRepository(JdbcTemplate jdbc, String tableName) {
		this.jdbc = jdbc;
		this.tableName = tableName;
		this.tableAssignment = tableName + "assignment";
		this.rowMapper = MapUtil::mapRowToAssignmentTable;
	}
	/**
	 * Is used if an assignmenttable contains more fields than name and id. then a custom rowmapper can be added.
	 * @param jdbc
	 * @param tableName
	 * @param rowMapper method from {@link MapUtil}
	 */
	public AssignmentTableRepository(JdbcTemplate jdbc, String tableName, RowMapper<ResultSet, Integer, IAssignmentTableModel> rowMapper) {
		this.jdbc = jdbc;
		this.tableName = tableName;
		this.tableAssignment = tableName + "assignment";
		this.rowMapper = rowMapper;
	}

	public IAssignmentTableModel find(long id) {
		return jdbc.queryForObject("SELECT * FROM " + tableName + " WHERE id = ?", new Object[] { id },
				rowMapper::mapRowToModel);
	}

	public List<IAssignmentTableModel> findAll() {
		return jdbc.query("SELECT * FROM " + tableName, rowMapper::mapRowToModel);
	}

	public List<IAssignmentTableModel> findByAccountId(long accountId) {
		return jdbc.query(
				"SELECT * FROM " + tableAssignment + " INNER JOIN " + tableName + " ON " + tableAssignment
						+ "."+tableName+"Id = " + tableName + ".id WHERE accountId = ?",
				new Object[] { accountId }, rowMapper::mapRowToModel);
	}

	public void addEntryToAccountById(long id, long accountId) {
		String query = "INSERT INTO " + tableAssignment + "(accountId, "+ tableName +"Id) VALUES (?, ?);";
		jdbc.execute(query, getAddEntryToAccountById(id, accountId));
	}

	public void deleteEntryFromAccountById(long id, long accountId) {
		String query = "DELETE FROM "+ tableAssignment +" WHERE accountid = ? AND "+ tableName +"id = ?";
		jdbc.execute(query, getDeleteEntryFromAccountById(id, accountId));
	}
	
	protected PreparedStatementCallback<Boolean> getAddEntryToAccountById(long id, long accountId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

				ps.setLong(1, accountId);
				ps.setLong(2, id);

				return ps.execute();
			}
		};
	}
	
	protected PreparedStatementCallback<Boolean> getDeleteEntryFromAccountById(long id, long accountId) {
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

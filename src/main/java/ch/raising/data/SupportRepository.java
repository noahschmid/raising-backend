package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.models.Support;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class SupportRepository implements IRepository<Support, Support> {
	private JdbcTemplate jdbc;

	public SupportRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Find support by id
	 * 
	 * @param id id of the desired support instance
	 * @return instance of the found support
	 */
	public Support find(long id) {
		return jdbc.queryForObject("SELECT * FROM support WHERE id = ?", new Object[] { id }, this::mapRowToModel);
	}

	/**
	 * Find supports which are assigned to certain account
	 */
	public List<Support> findByAccountId(long id) {
		return jdbc.query(
				"SELECT * FROM supportAssignment INNER JOIN support ON "
						+ "supportAssignment.supportId = support.id WHERE accountId = ?",
				new Object[] { id }, this::mapRowToModel);
	}

	/**
	 * Map a row of a result set to an support instance
	 * 
	 * @param rs     result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Support instance of the result set
	 * @throws SQLException
	 */
	@Override
	public Support mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return new Support(rs.getLong("id"), rs.getString("name"));
	}

	/**
	 * Update support
	 * 
	 * @param id  the id of the industry to update
	 * @param req request containing fields to update
	 */
	public void update(long id, Support req) throws Exception {
		UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("support", id, this);
		updateQuery.setJdbc(jdbc);
		updateQuery.addField(req.getName(), "name");
		updateQuery.execute();

	}

	/**
	 * add supportid and accountid to supportassignment
	 * @param accountId
	 * @param supportId
	 * @throws Exception
	 */
	public void addSupportToAccountById(long accountId, long supportId) {
		String query = "INSERT INTO supportAssignment(accountId, supportId) VALUES (?, ?);";
		jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

				ps.setLong(1, accountId);
				ps.setLong(2, supportId);

				return ps.execute();
			}
		});
	}

	/**
	 * delete form entry with both ids from supportassingment
	 * @param supportId
	 * @param accountId
	 */
	public void deleteSupportFromAccountById(long supportId, long accountId) {
		String query = "DELETE FROM supportassignment WHERE accountId = ? AND supportId = ?";
		jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

				ps.setLong(1, accountId);
				ps.setLong(2, supportId);

				return ps.execute();
			}
		});
	}

	public List<Support> getAllSupports() {
		return jdbc.query("SELECT * FROM support", this::mapRowToModel);
	}
}
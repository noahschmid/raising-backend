package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
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
	 * @param id id of the desired support instance
	 * @return instance of the found support
	 */
	public Support find(int id) {
		return jdbc.queryForObject("SELECT * FROM support WHERE id = ?", new Object[] { id }, this::mapRowToSupport);
	}

	/**
	 * Find supports which are assigned to certain account
	 */
	public List<Support> findByAccountId(int id) {
		return jdbc.query("SELECT * FROM supportAssignment INNER JOIN support ON " +
						   "supportAssignment.supportId = support.id WHERE accountId = ?",
						   new Object[] { id }, this::mapRowToSupport);
	}

    /**
	 * Map a row of a result set to an support instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Support instance of the result set
	 * @throws SQLException
	 */
	private Support mapRowToSupport(ResultSet rs, int rowNum) throws SQLException {
		return new Support(rs.getInt("id"), 
			rs.getString("name"));
	}

	/**
	 * Update support
	 * @param id the id of the industry to update
	 * @param req request containing fields to update
	 */
	public void update(int id, Support req) throws Exception {
		try {
			UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("support", id, this);
			updateQuery.setJdbc(jdbc);
			updateQuery.addField(req.getName(), "name");
			updateQuery.execute();
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

import ch.raising.models.Support;

public class SupportRepository {
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
}
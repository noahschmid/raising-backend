package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import ch.raising.models.Industry;

public class IndustryRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public IndustryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
	 * Find industry by id
	 * @param id id of the desired industry
	 * @return instance of the found industry
	 */
	public Industry find(int id) {
		return jdbc.queryForObject("SELECT * FROM industry WHERE id = ?", new Object[] { id }, this::mapRowToIndustry);
	}

    /**
	 * Map a row of a result set to an Industry instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Industry instance of the result set
	 * @throws SQLException
	 */
	private Industry mapRowToIndustry(ResultSet rs, int rowNum) throws SQLException {
		return new Industry(rs.getInt("id"), 
			rs.getString("name"));
	}

}
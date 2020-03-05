package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import ch.raising.models.Continent;

public class ContinentRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public ContinentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
	 * Find continent by id
	 * @param id id of the desired continent
	 * @return instance of the found continent
	 */
	public Continent find(int id) {
		return jdbc.queryForObject("SELECT * FROM continent WHERE id = ?", new Object[] { id }, this::mapRowToContinent);
	}

    /**
	 * Map a row of a result set to an Continent instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Continent instance of the result set
	 * @throws SQLException
	 */
	private Continent mapRowToContinent(ResultSet rs, int rowNum) throws SQLException {
		return new Continent(rs.getInt("id"), 
			rs.getString("name"));
	}
}
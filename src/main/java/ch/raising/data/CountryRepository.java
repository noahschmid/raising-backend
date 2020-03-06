package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.raising.models.Country;

@Repository
public class CountryRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public CountryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
	 * Find country by id
	 * @param id id of the desired country
	 * @return instance of the found country
	 */
	public Country find(int id) {
		return jdbc.queryForObject("SELECT * FROM industry WHERE id = ?", new Object[] { id }, this::mapRowToCountry);
	}

	/**
	 * Find countries which are assigned to certain account
	 */
	public List<Country> findByAccountId(int id) {
		return jdbc.query("SELECT * FROM countryAssignment INNER JOIN country ON " +
						   "countryAssignment.countryId = country.id WHERE accountId = ?",
						   new Object[] { id }, this::mapRowToCountry);
	}

    /**
	 * Map a row of a result set to an Country instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Country instance of the result set
	 * @throws SQLException
	 */
	private Country mapRowToCountry(ResultSet rs, int rowNum) throws SQLException {
		return new Country(rs.getInt("id"), 
			rs.getString("name"));
	}
}
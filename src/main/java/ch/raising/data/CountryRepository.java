package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.raising.models.Country;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class CountryRepository implements IRepository<Country, Country> {
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
	public List<Country> findByAccountId(int accountId) {
		return jdbc.query("SELECT * FROM countryAssignment INNER JOIN country ON " +
						   "countryAssignment.countryId = country.id WHERE accountId = ?",
						   new Object[] { accountId }, this::mapRowToCountry);
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

	/**
	 * Update country
	 * @param id the id of the country to update
	 * @param req request containing fields to update
	 */
	public void update(int id, Country req) throws Exception {
		try {
			UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("country", id, this);
			updateQuery.setJdbc(jdbc);
			updateQuery.addField(req.getName(), "name");
			updateQuery.execute();
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
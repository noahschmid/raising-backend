package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.number.PercentStyleFormatter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
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
	 * 
	 * @param id id of the desired country
	 * @return instance of the found country
	 */
	public Country find(long id) {
		return jdbc.queryForObject("SELECT * FROM industry WHERE id = ?", new Object[] { id }, this::mapRowToModel);
	}

	/**
	 * Find countries which are assigned to certain account
	 */

	public List<Country> findByAccountId(long accountId) {
		return jdbc.query("SELECT * FROM countryAssignment INNER JOIN country ON " +
						   "countryAssignment.countryId = country.id WHERE accountId = ?",
						   new Object[] { accountId }, this::mapRowToModel);
	}

	/**
	 * Map a row of a result set to an Country instance
	 * 
	 * @param rs     result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Country instance of the result set
	 * @throws SQLException
	 */
	@Override
	public Country mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return new Country(rs.getLong("id"), 
			rs.getString("name"));
	}

	/**
	 * Update country
	 * 
	 * @param id  the id of the country to update
	 * @param req request containing fields to update
	 */
	public void update(long id, Country req) throws Exception {
		try {
			UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("country", id, this);
			updateQuery.setJdbc(jdbc);
			updateQuery.addField(req.getName(), "name");
			updateQuery.execute();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Add country to account
	 * 
	 * @param accountId id of the account
	 * @param countryId id of the country
	 */
	public void addCountryToAccountById(long accountId, long countryId){
			String query = "INSERT INTO countryAssignment(accountId, countryId) VALUES (?, ?);"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
					ps.setLong(1,accountId);  
					ps.setLong(2,countryId);
						
					return ps.execute();  
				}  
			});  
	}
	/**
	 * Delete entry in countryassignment with both specified ids
	 * @param countryId
	 * @param id
	 */

	public void deleteCountryFromAccountById(long countryId, long id) {
		String query = "DELETE FROM countryassignment WHERE accountid = ? AND countryid = ?";
		jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
			
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				ps.setLong(1, id);
				ps.setLong(2, countryId);
				return ps.execute();
			}
		});
	}
	
	/**
	 * 
	 * @return all countries in the countrytable
	 */
	
	public List<Country> getAllCountries() {
		return jdbc.query("SELECT * FROM country", this::mapRowToModel);
	}
}
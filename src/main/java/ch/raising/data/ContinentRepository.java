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

import ch.raising.models.Continent;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class ContinentRepository implements IAssignmentTableRepository<Continent>{
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
    
	public Continent find(long id) {
		return jdbc.queryForObject("SELECT * FROM continent WHERE id = ?", new Object[] { id }, this::mapRowToModel);
	}

	/**
	 * Find continents which are assigned to certain account
	 */
	@Override
	public List<Continent> findByAccountId(long id) {
		return jdbc.query("SELECT * FROM continentAssignment INNER JOIN continent ON " +
						   "continentAssignment.continentId = continent.id WHERE accountId = ?",
						   new Object[] { id }, this::mapRowToModel);
	}

    /**
	 * Map a row of a result set to an Continent instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Continent instance of the result set
	 * @throws SQLException
	 */
	@Override
	public Continent mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return new Continent(rs.getLong("id"), 
			rs.getString("name"));
	}
	/**
	 * Add continent to account
	 * @param continentId id of the account
	 * @param accId id of the continent
	 */
	@Override
	public void addEntryToAccountById(long continentId, long accId) {
		try {
			String query = "INSERT INTO continentAssignment(accountId, continentId) VALUES (?, ?);"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
					ps.setLong(1,continentId);  
					ps.setLong(2,accId);
						
					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}
	/**
	 * delete entry with both ids from continentrepository
	 * @param continentId
	 * @param id
	 */

	@Override
	public void deleteEntryFromAccountById(long continentId, long id) {
		String query = "DELETE FROM countryassignment WHERE accountid = ? AND continentid = ?";
		jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
			
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				ps.setLong(1, id);
				ps.setLong(2, continentId);
				return ps.execute();
			}
		});
	}

	@Override
	public List<Continent> getAll() {
		return jdbc.query("SELECT * FROM continent", this::mapRowToModel);
	}
}
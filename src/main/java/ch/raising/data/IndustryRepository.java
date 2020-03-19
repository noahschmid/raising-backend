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

import ch.raising.models.Industry;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class IndustryRepository implements IAssignmentTableRepository<Industry> {
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
    @Override
	public Industry find(long id) {
		return jdbc.queryForObject("SELECT * FROM industry WHERE id = ?", new Object[] { id }, this::mapRowToModel);
	}

	/**
	 * Find industries which are assigned to certain account
	 */
    @Override
	public List<Industry> findByAccountId(long id) {
		return jdbc.query("SELECT * FROM industryAssignment INNER JOIN industry ON " +
						   "industryAssignment.industryId = industry.id WHERE accountId = ?",
						   new Object[] { id }, this::mapRowToModel);
	}

    /**
	 * Map a row of a result set to an Industry instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Industry instance of the result set
	 * @throws SQLException
	 */
	@Override
	public Industry mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return new Industry(rs.getLong("id"), 
			rs.getString("name"));
	}

	/**
	 * add entry with industryid and accountid into industryassingment
	 * @param accountId
	 * @param industryId
	 * @throws SQLException
	 */
	@Override
	public void addEntryToAccountById(long accountId, long industryId) {
		String sql = "INSERT INTO industryassignment(accountId, industryId) VALUES (?,?)";
		jdbc.execute(sql,new PreparedStatementCallback<Boolean>(){  
			@Override  
			public Boolean doInPreparedStatement(PreparedStatement ps)  
					throws SQLException, DataAccessException {  
					
				ps.setLong(1,accountId);  
				ps.setLong(2,industryId);
					
				return ps.execute();  
			}  
		});  
		
	}

	/**
	 * delete entry with both ids from industryassignment
	 * @param industryId
	 * @param accountId
	 */
	@Override
	public void deleteEntryFromAccountById(long industryId, long accountId) {
		String sql = "DELETE FROM industryassignment WHERE industryid = ? AND accountid = ?";
		jdbc.execute(sql,new PreparedStatementCallback<Boolean>(){  
			@Override  
			public Boolean doInPreparedStatement(PreparedStatement ps)  
					throws SQLException, DataAccessException {  
					
				ps.setLong(1,industryId);  
				ps.setLong(2,accountId);
					
				return ps.execute();  
			}  
		});  
	}
	@Override
	public List<Industry> getAll() {
		return jdbc.query("SELECT * FROM industry", this::mapRowToModel);
	}
}
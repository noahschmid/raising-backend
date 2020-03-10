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
public class IndustryRepository implements IRepository<Industry, Industry> {
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
	 * Find industries which are assigned to certain account
	 */
	public List<Industry> findByAccountId(int id) {
		return jdbc.query("SELECT * FROM industryAssignment INNER JOIN industry ON " +
						   "industryAssignment.industryId = industry.id WHERE accountId = ?",
						   new Object[] { id }, this::mapRowToIndustry);
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

	/**
	 * Update industry
	 * @param id the id of the industry to update
	 * @param req request containing fields to update
	 */
	public void update(int id, Industry req) throws Exception {
		try {
			UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("industry", id, this);
			updateQuery.setJdbc(jdbc);
			updateQuery.addField(req.getName(), "name");
			updateQuery.execute();
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void addIndustryToAccount(int accountId, int industryId) throws SQLException {
		String sql = "INSERT INTO industryassignment(accountId, industryId) VALUES (?,?)";
		jdbc.execute(sql,new PreparedStatementCallback<Boolean>(){  
			@Override  
			public Boolean doInPreparedStatement(PreparedStatement ps)  
					throws SQLException, DataAccessException {  
					
				ps.setInt(1,accountId);  
				ps.setInt(2,industryId);
					
				return ps.execute();  
			}  
		});  
		
	}
}
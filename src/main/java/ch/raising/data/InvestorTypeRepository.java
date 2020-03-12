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

import ch.raising.models.InvestorType;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class InvestorTypeRepository implements IRepository<InvestorType, InvestorType> {
    private JdbcTemplate jdbc;

    @Autowired
    public InvestorTypeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
	 * Find investor type by id
	 * @param id id of the desired investor type
	 * @return instance of the found investor type
	 */
	public InvestorType find(long id) {
		return jdbc.queryForObject("SELECT * FROM investorType WHERE id = ?", new Object[] { id }, this::mapRowToInvestorType);
	}

	/**
	 * Find investor types by startup id
	 * @param startupId id of the desired startup
	 * @return list of the found investor types
	 */
	public List<InvestorType> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM investorType INNER JOIN investorTypeAssignment " +
						"ON investorType.id = investorTypeAssignment.investorTypeId " +
						"WHERE startupId = ?", new Object[] { startupId }, 
						this::mapRowToInvestorType);
	}


    /**
	 * Map a row of a result set to an InvestorType instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return InvestorType instance of the result set
	 * @throws SQLException
	 */
	private InvestorType mapRowToInvestorType(ResultSet rs, int rowNum) throws SQLException {
		return new InvestorType(rs.getLong("id"), 
            rs.getString("name"),
            rs.getString("description"));
	}

	/**
	 * Update industry
	 * @param id the id of the industry to update
	 * @param req request containing fields to update
	 */
	public void update(long id, InvestorType req) throws Exception {
		try {
			UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("investorType", id, this);
			updateQuery.setJdbc(jdbc);
			updateQuery.addField(req.getName(), "name");
			updateQuery.addField(req.getDescription(), "description");
			updateQuery.execute();
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	/**
	 * adds an entry in the investortypeassignment table with the specified ids
	 * @param startupId
	 * @param investorTypeId
	 * @throws SQLException
	 */

	public void addInvestorTypeToStartup(long startupId, long investorTypeId) throws SQLException {
		String sql = "INSERT INTO investortypeassignment(startupid, investortypeid) VALUES (?,?)";
		jdbc.execute(sql,new PreparedStatementCallback<Boolean>(){  
			@Override  
			public Boolean doInPreparedStatement(PreparedStatement ps)  
					throws SQLException, DataAccessException {  
					
				ps.setLong(1, startupId);  
				ps.setLong(2,investorTypeId);
					
				return ps.execute();  
			}  
		});  
	}
	/**
	 * deletes the entry in investortypeassignment with the specified ids
	 * @param investorTypeId
	 * @param startupId
	 */
	public void deleteInvestorTypeOfStartupId(long investorTypeId, long startupId) {
		String sql = "DELETE FROM investortypeassignment(startupid, invetortypeid) VALUES (?, ?)";
		jdbc.execute(sql,new PreparedStatementCallback<Boolean>(){  
			@Override  
			public Boolean doInPreparedStatement(PreparedStatement ps)  
					throws SQLException, DataAccessException {  

				ps.setLong(1, investorTypeId);  
				ps.setLong(2,investorTypeId);
					
				return ps.execute();  
			}  
		});  
	}

	public Object getAllInvestorTypes() {
		return jdbc.query("SELECT * FROM investortype", this::mapRowToInvestorType);
	}
	
}
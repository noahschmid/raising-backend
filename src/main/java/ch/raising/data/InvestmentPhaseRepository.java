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

import ch.raising.models.InvestmentPhase;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class InvestmentPhaseRepository implements IAssignmentTableRepository<InvestmentPhase> {
    private JdbcTemplate jdbc;

    @Autowired
    InvestmentPhaseRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    /**
	 * Find investment phase by id
	 * @param id id of the desired investment phase
	 * @return instance of the found investment phase
	 */
    @Override
	public InvestmentPhase find(long id) {
		return jdbc.queryForObject("SELECT * FROM investmentPhase WHERE id = ?", new Object[] { id }, this::mapRowToModel);
	}

	/**
	 * Find investment phases which are assigned to certain investor
	 */
    @Override
	public List<InvestmentPhase> findByAccountId(long id) {
		return jdbc.query("SELECT * FROM investmentPhaseAssignment INNER JOIN investmentPhase ON " +
						   "investmentPhaseAssignment.investmentPhaseId = investmentPhase.id WHERE investorId = ?",
						   new Object[] { id }, this::mapRowToModel);
	}

    /**
	 * Map a row of a result set to an InvestmentPhase instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return InvestmentPhase instance of the result set
	 * @throws SQLException
	 */
	@Override
	public InvestmentPhase mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return new InvestmentPhase(rs.getLong("id"), 
			rs.getString("name"));
	}
	
	/**
	 * adds an entry in investortypeassignments with the ids
	 * @param investorId
	 * @param investmentPhaseId
	 * @throws SQLException
	 */
	@Override
	public void addEntryToAccountById(long investorId, long investmentPhaseId) {
		String sql = "INSERT INTO investmentphaseassignment(investorid, investmentphaseid) VALUES (?,?)";
		jdbc.execute(sql,new PreparedStatementCallback<Boolean>(){  
			@Override  
			public Boolean doInPreparedStatement(PreparedStatement ps)  
					throws SQLException, DataAccessException {  
					
				ps.setLong(1,investorId);  
				ps.setLong(2,investmentPhaseId);
					
				return ps.execute();  
			}  
		});  
	}
	@Override
	public void deleteEntryFromAccountById(long accountId, long invPhaseId) {
		String sql = "DELETE FROM investmentphaseassignment WHERE investorid  = ? AND investmentphaseid = ?";
		jdbc.execute(sql,new PreparedStatementCallback<Boolean>(){  
			@Override  
			public Boolean doInPreparedStatement(PreparedStatement ps)  
					throws SQLException, DataAccessException {  
				ps.setLong(1, accountId);  
				ps.setLong(2, invPhaseId);
				return ps.execute();  
			}  
		}); 
	}
	@Override
	public List<InvestmentPhase> getAll() {
		return jdbc.query("SELECT * FROM investmentphase", this::mapRowToModel);
	}
}
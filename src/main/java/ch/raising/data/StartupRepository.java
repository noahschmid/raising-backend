package ch.raising.data;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.PreparedStatementCallback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import ch.raising.interfaces.IRepository;
import ch.raising.models.Startup;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MapUtil;
import ch.raising.utils.UidUtil;
import ch.raising.utils.UpdateQueryBuilder;
/**
 * 
 * @author noahs, manus
 *
 */
@Repository
public class StartupRepository implements IRepository<Startup> {
	private JdbcTemplate jdbc;
	private final String FIND_BY_ID;
	
	@Autowired
	public StartupRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.FIND_BY_ID = "SELECT * FROM startup WHERE accountId = ?";
	}

	/**
	 * Find startup by tableEntryId
	 * 
	 * @param accountId tableEntryId of the desired startup account
	 * @return instance of the found startup
	 * @throws DatabaseOperationException 
	 */
	public Startup find(long accountId){
			
		return jdbc.queryForObject(FIND_BY_ID, new Object[] { accountId }, this::mapRowToModel);
			
	}

	/**
	 * Get all startups
	 * 
	 * @return list of all startups
	 */
	public List<Startup> getAll() throws DataAccessException, SQLException{
		String getAll = "SELECT * FROM startup";
		List<Startup> users = jdbc.query(getAll, this::mapRowToModel);
		return users;
	}

	/**
	 * Update startup
	 * 
	 * @param tableEntryId the tableEntryId of the startup to update
	 * @param req          instance of the update request
	 * @throws Exception
	 */
	@Override
	public void update(long id, Startup req) throws SQLException, DataAccessException {
		String uid = req.getUId();
		if(!UidUtil.isValidUId(uid)) {
			uid = null;
		}
		UpdateQueryBuilder update = new UpdateQueryBuilder(jdbc, "startup", id, "accountid");
		update.addField(req.getInvestmentPhaseId(), "investmentphaseid");
		update.addField(req.getBreakEvenYear(), "breakevenyear");
		update.addField(req.getNumberOfFte(), "numberoffte");
		update.addField(req.getPreMoneyValuation(), "premoneyvaluation");
		update.addField(req.getClosingTime(), "closingtime");
		update.addField(req.getRevenueMaxId(), "revenuemaxid");
		update.addField(req.getRevenueMinId(), "revenueMinId");
		update.addField(req.getScope(), "scope");
		update.addField(req.getUId(), "uid");
		update.addField(req.getFoundingYear(), "foundingyear");
		update.addField(req.getFinanceTypeId(), "financetypeid");
		update.addField(req.getRaised(), "raised");
		update.execute();
	}
	/**
	 * Map a row of a result set to an account instance
	 * 
	 * @param rs     result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Account instance of the result set
	 * @throws SQLException
	 */
	public Startup mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return Startup.startupBuilder().accountId(rs.getLong("accountId"))
				.investmentPhaseId(rs.getLong("investmentphaseid")).boosts(rs.getInt("boosts"))
				.numberOfFte(rs.getInt("numberOfFte")).breakEvenYear(rs.getInt("breakevenYear"))
				.investmentPhaseId(rs.getLong("investmentPhaseId")).revenueMaxId(rs.getInt("revenuemaxid"))
				.revenueMinId(rs.getInt("revenueminid")).scope(rs.getInt("scope")).uId(rs.getString("uid"))
				.foundingYear(rs.getInt("foundingYear")).financeTypeId(rs.getLong("financetypeid"))
				.closingTime(rs.getDate("closingTime")).preMoneyEvaluation(rs.getInt("premoneyvaluation"))
				.raised(rs.getInt("raised")).videoId(rs.getLong("videoid")).build();
	}

	/**
	 * Add a new startup to the database
	 * 
	 * @param startup the startup to add
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public void add(Startup su) throws SQLException, DataAccessException {

		String query = "INSERT INTO startup(accountid, boosts, numberoffte,"
				+ " breakevenyear, premoneyvaluation, closingtime, financetypeid, investmentphaseid, "
				+ "revenuemaxid, revenueminid, scope, uid, foundingyear, raised, videoid) VALUES (?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setLong(c++, su.getAccountId());
				ps.setInt(c++, 0);
				ps.setInt(c++, su.getNumberOfFte());
				ps.setInt(c++, su.getBreakEvenYear());
				ps.setInt(c++, su.getPreMoneyValuation());
				ps.setDate(c++, su.getClosingTime());
				ps.setInt(c++, (int) su.getFinanceTypeId());
				ps.setInt(c++, (int) su.getInvestmentPhaseId());
				ps.setInt(c++, su.getRevenueMaxId());
				ps.setInt(c++, su.getRevenueMinId());
				ps.setInt(c++, su.getScope());
				ps.setString(c++, su.getUId());
				ps.setInt(c++, su.getFoundingYear());
				ps.setInt(c++, su.getRaised());
				if(su.getVideoId() != -1)
					ps.setLong(c++, su.getVideoId());
				else
					ps.setNull(c++, java.sql.Types.BIGINT);
				return ps.execute();
			}
		});
	}

	public long getInvestmentPhase(long accountId) {
		return jdbc.queryForObject("SELECT investmentphaseid from startup where accountid = ?", new Object[] {accountId}, MapUtil::mapRowToFirstEntry);
	}
}
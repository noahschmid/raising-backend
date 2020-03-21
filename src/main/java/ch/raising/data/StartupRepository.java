package ch.raising.data;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.PreparedStatementCallback;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import ch.raising.interfaces.IRepository;
import ch.raising.models.Startup;
import ch.raising.utils.NotImplementedException;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class StartupRepository implements IRepository<Startup, Startup> {
	private JdbcTemplate jdbc;

	@Autowired
	public StartupRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Find startup by tableEntryId
	 * 
	 * @param accountId tableEntryId of the desired startup account
	 * @return instance of the found startup
	 */
	public Startup find(long accountId) {
        try {
            String sql = "SELECT * FROM startup WHERE accountId = ?";
            return jdbc.queryForObject(sql, new Object[] { accountId }, this::mapRowToModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
	
	/**
	 * Get all startups
	 * 
	 * @return list of all startups
	 */
	public List<Startup> getAll() {
		String getAll = "SELECT * FROM account";
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
	public void update(long id, Startup req) throws Exception {
		throw new NotImplementedException();
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
				.numberOfFTE(rs.getInt("numberOfFTE")).turnover(rs.getInt("turnover")).street(rs.getString("street"))
				.breakEvenYear(rs.getInt("breakevenYear")).city(rs.getString("city")).zipCode(rs.getString("zipCode"))
				.website(rs.getString("website")).investmentPhaseId(rs.getLong("investmentPhaseId"))
				.revenueMax(rs.getInt("revenuemax")).revenueMin(rs.getInt("revenuemin")).scope(rs.getInt("scope"))
				.uId(rs.getString("uid")).foundingyear(rs.getInt("foundingyear"))
				.financeTypeId(rs.getLong("financetype")).closingtime(rs.getDate("closingtime"))
				.preMoneyevaluation(rs.getInt("premoneyvaluation")).build();
	}

	/**
	 * Add a new investor to the database
	 * 
	 * @param startup the startup to add
	 */
	public void add(Startup su) throws Exception {

		try {
			String query = "INSERT INTO startup(accountid, investmentphaseid, boosts, numberoffte, turnover, "
					+ "street, city, website, breakevenyear, zipcode, premoneyevaluation, revenueMax, financetypeid, "
					+ "closingtime, revenuemin, scope, uid, foundingyear) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					int c = 1;
					ps.setLong(c++, su.getAccountId());
					ps.setLong(c++, su.getInvestmentPhaseId());
					ps.setInt(c++, su.getBoosts());
					ps.setInt(c++, su.getNumberOfFTE());
					ps.setInt(c++, su.getTurnover());
					ps.setString(c++, su.getStreet());
					ps.setString(c++, su.getCity());
					ps.setString(c++, su.getWebsite());
					ps.setInt(c++, su.getBreakEvenYear());
					ps.setString(c++, su.getZipCode());
					ps.setInt(c++, su.getPreMoneyevaluation());
					ps.setInt(c++, su.getRevenueMax());
					ps.setLong(c++, su.getFinanceTypeId());
					ps.setDate(c++, (Date) su.getClosingtime());
					ps.setInt(c++, su.getRevenueMin());
					ps.setInt(c++, su.getScope());
					ps.setInt(c++, su.getFoundingyear());
					
					return ps.execute();
				}
			});
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}
}
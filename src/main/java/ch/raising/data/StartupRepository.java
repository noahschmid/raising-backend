package ch.raising.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import ch.raising.models.Startup;
import ch.raising.models.StartupUpdateRequest;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class StartupRepository implements IRepository<Startup, StartupUpdateRequest> {
	private JdbcTemplate jdbc;

	@Autowired
	public StartupRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Find startup by id
	 * 
	 * @param accountId id of the desired startup account
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
	 * Update startup
	 * 
	 * @param id  the id of the startup to update
	 * @param req instance of the update request
	 * @throws Exception
	 */
	public void update(long id, StartupUpdateRequest req) throws Exception {
		try {
			UpdateQueryBuilder update = new UpdateQueryBuilder("investor", id, this, "accountId");
			update.setJdbc(jdbc);
			update.addField(req.getName(), "name");
			update.addField(req.getDescription(), "description");
			update.addField(req.getInvestmentMax(), "investmentMax");
			update.addField(req.getInvestmentMin(), "investmentMin");
			update.addField(req.getInvestmentPhaseId(), "investmentPhaseId");
			update.addField(req.getBoosts(), "boosts");
			update.addField(req.getNumberOfFTE(), "numberoffte");
			update.addField(req.getTurnover(), "turnover");
			update.addField(req.getStreet(), "street");
			update.addField(req.getCity(), "city");
			update.addField(req.getWebsite(), "website");
			update.addField(req.getBreakEvenYear(), "breakevenyear");
			update.addField(req.getZipCode(), "zipcode");
			update.execute();
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	/**
	 * Map a row of a result set to an account instance
	 * 
	 * @param rs     result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Account instance of the result set
	 * @throws SQLException
	 */
	@Override
	public Startup mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return Startup.startupBuilder().accountId(rs.getLong("accountId")).boosts(rs.getInt("boosts"))
				.breakEvenYear(rs.getInt("breakevenYear")).city(rs.getString("city")).zipCode(rs.getString("zipCode"))
				.website(rs.getString("website")).street(rs.getString("street"))
				.investmentPhaseId(rs.getLong("investmentPhaseId")).turnover(rs.getInt("turnover"))
				.numberOfFTE(rs.getInt("numberOfFTE")).revenue(rs.getString("revenue"))
				.financeTypeId(rs.getLong("financetype")).closingtime(rs.getDate("closingtime"))
				.preMoneyevaluation(rs.getInt("premoneyvaluation")).build();
	}

	/**
	 * Add a new investor to the database
	 * 
	 * @param startup the startup to add
	 */
	public void add(Startup startup) throws Exception {

		try {
			String query = "INSERT INTO startup(investmentphaseid, boosts, numberoffte, turnover, street, city, website,"
					+ "breakevenyear, zipcode, premoeyevaluation, closingtime, revenue, financetype) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					ps.setLong(1, startup.getInvestmentPhaseId());
					ps.setInt(2, startup.getBoosts());
					ps.setInt(3, startup.getNumberOfFTE());
					ps.setInt(4, startup.getTurnover());
					ps.setString(5, startup.getStreet());
					ps.setString(6, startup.getWebsite());
					ps.setInt(7, startup.getBreakEvenYear());
					ps.setString(8, startup.getZipCode());
					ps.setInt(9, startup.getPreMoneyevaluation());
					ps.setDate(10, (Date) startup.getClosingtime());
					ps.setString(11, startup.getRevenue());
					ps.setLong(12, startup.getFinanceTypeId());
					return ps.execute();
				}
			});
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}
}
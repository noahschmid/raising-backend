package ch.raising.data;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IRepository;
import ch.raising.models.Account;
import ch.raising.models.Investor;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MapUtil;
import ch.raising.utils.UpdateQueryBuilder;
/**
 * repository for the investor
 * @author noahs, manus
 *
 */
@Repository
public class InvestorRepository implements IRepository<Investor> {
	private JdbcTemplate jdbc;

	private final String FIND_BY_ID;

	@Autowired
	public InvestorRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.FIND_BY_ID = "SELECT * FROM investor WHERE accountId = ?";
	}

	/**
	 * Find investor by id
	 * 
	 * @param id id of the desired investor
	 * @return instance of the found investor
	 * @throws DatabaseOperationException
	 */
	public Investor find(long id) {

		return jdbc.queryForObject(FIND_BY_ID, new Object[] { id }, this::mapRowToModel);

	}

	/**
	 * Get all investors
	 * 
	 * @return list of all investors
	 */
	public List<Investor> getAll() throws DataAccessException, SQLException {
		String getAll = "SELECT * FROM investor";
		List<Investor> users = jdbc.query(getAll, this::mapRowToModel);
		return users;
	}

	/**
	 * Update investor
	 * 
	 * @param id     the id of the investor to update
	 * @param update instance of the update request
	 * @throws Exception
	 */
	public void update(long id, Investor inv) throws SQLException, DataAccessException {
		UpdateQueryBuilder update = new UpdateQueryBuilder(jdbc, "investor", id, "accountid");
		System.out.println(inv.getInvestorTypeId());
		update.addField(inv.getInvestorTypeId(), "investortypeid");
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
	@Override
	public Investor mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return Investor.investorBuilder().accountId(rs.getLong("accountId")).investorTypeId(rs.getInt("investorTypeId"))
				.build();
	}

	/**
	 * Add a new investor to the database
	 * 
	 * @param investor the investor to add
	 */
	public void add(Investor investor) {

		try {
			String query = "INSERT INTO investor(accountid, investorTypeId) VALUES (?, ?);";
			jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					int c = 1;
					ps.setLong(c++, investor.getAccountId());
					ps.setLong(c++, investor.getInvestorTypeId());
					return ps.execute();
				}
			});
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}

	public long getInvestorType(long accountId) {
		return jdbc.queryForObject("SELECT investortypeid FROM investor WHERE accountid = ?", new Object[] {accountId}, MapUtil::mapRowToFirstEntry);
	}
}
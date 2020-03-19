package ch.raising.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import ch.raising.models.Account;
import ch.raising.models.InvestmentPhase;
import ch.raising.models.Investor;
import ch.raising.models.InvestorType;
import ch.raising.models.InvestorUpdateRequest;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class InvestorRepository implements IRepository<Investor, InvestorUpdateRequest> {
	private JdbcTemplate jdbc;

	@Autowired
	public InvestorRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Find investor by id
	 * 
	 * @param id id of the desired investor
	 * @return instance of the found investor
	 */
	public Investor find(long id) {
		try {
			String sql = "SELECT * FROM investor WHERE accountId = ?";
			return jdbc.queryForObject(sql, new Object[] { id }, this::mapRowToModel);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Update investor
	 * 
	 * @param id     the id of the investor to update
	 * @param update instance of the update request
	 * @throws Exception
	 */
	public void update(long id, InvestorUpdateRequest update) throws Exception {
		try {
			UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("investor", id, this, "accountId");
			updateQuery.setJdbc(jdbc);
			updateQuery.addField(update.getDescription(), "description");
			updateQuery.addField(update.getName(), "name");
			updateQuery.addField(update.getInvestmentMax(), "investmentMax");
			updateQuery.addField(update.getInvestmentMin(), "investmentMin");
			updateQuery.addField(update.getInvestorTypeId(), "investorTypeId");
			updateQuery.execute();
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
	public Investor mapRowToModel(ResultSet rs, int rowNum) throws SQLException {
		return Investor.investorBuilder().accountId(rs.getLong("accountId")).description(rs.getString("description"))
				.investorTypeId(rs.getInt("investorTypeId")).build();
	}

	/**
	 * Add a new investor to the database
	 * 
	 * @param investor the investor to add
	 */
	public void add(Investor investor) {

		try {
			String query = "INSERT INTO investor(accountid, description, investorTypeId) VALUES (?, ?, ?);";
			jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
					int c = 1;
					ps.setLong(c++, investor.getAccountId());
					ps.setString(c++, investor.getDescription());
					ps.setLong(c++, investor.getInvestorTypeId());
					return ps.execute();
				}
			});
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}
}
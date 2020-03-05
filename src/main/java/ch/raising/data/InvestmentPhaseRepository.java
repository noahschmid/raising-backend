package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import ch.raising.models.InvestmentPhase;

public class InvestmentPhaseRepository {
    private JdbcTemplate jdbc;

    @Autowired
    InvestmentPhaseRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    /**
	 * Find user account by id
	 * @param id id of the desired account
	 * @return instance of the found account
	 */
	public InvestmentPhase find(int id) {
		return jdbc.queryForObject("SELECT * FROM investmentPhase WHERE id = ?", new Object[] { id }, this::mapRowToInvestmentPhase);
	}

    /**
	 * Map a row of a result set to an account instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return InvestmentPhase instance of the result set
	 * @throws SQLException
	 */
	private InvestmentPhase mapRowToInvestmentPhase(ResultSet rs, int rowNum) throws SQLException {
		return new InvestmentPhase(rs.getInt("id"), 
			rs.getString("name"));
	}
}
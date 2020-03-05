package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.raising.models.InvestmentPhase;

@Repository
public class InvestmentPhaseRepository {
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
	public InvestmentPhase find(int id) {
		return jdbc.queryForObject("SELECT * FROM investmentPhase WHERE id = ?", new Object[] { id }, this::mapRowToInvestmentPhase);
	}

	/**
	 * Find investment phases which are assigned to certain investor
	 */
	public List<InvestmentPhase> findByInvestorId(int id) {
		return jdbc.query("SELECT * FROM investmentPhaseAssignment INNER JOIN investmentPhase ON " +
						   "investmentPhaseAssignment.investmentPhaseId = investmentPhase.id WHERE investorId = ?",
						   new Object[] { id }, this::mapRowToInvestmentPhase);
	}

    /**
	 * Map a row of a result set to an InvestmentPhase instance
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
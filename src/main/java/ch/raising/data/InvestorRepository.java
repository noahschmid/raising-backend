package ch.raising.data;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.raising.models.Account;
import ch.raising.models.Investor;
import ch.raising.data.AccountRepository;

@Repository
public class InvestorRepository {
    private JdbcTemplate jdbc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    public InvestorRepository(JdbcTemplate jdbc, AccountRepository accountRepository) {
        this.jdbc = jdbc;
        this.accountRepository = accountRepository;
    }

    /**
	 * Find investor by id
	 * @param id id of the desired investor
	 * @return instance of the found investor
	 */
	public Investor find(int id) {
		return jdbc.queryForObject("SELECT * FROM investor WHERE id = ? INNER JOIN investorType ON investor.typeId = investorType.id", new Object[] { id }, this::mapRowToInvestor);
	}

    /**
	 * Map a row of a result set to an account instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Account instance of the result set
	 * @throws SQLException
	 */
	private Investor mapRowToInvestor(ResultSet rs, int rowNum) throws SQLException {
        Investor investor = new Investor();
        Account account = accountRepository.find(rs.getInt("accountId"));
        investor.setId(rs.getInt("id"));
        investor.setAccount(account);
        investor.setInvestmentMax(rs.getInt("investmentMax"));
        investor.setInvestmentMin(rs.getInt("investmentMin"));

        // TODO: set all required fields
        
        return investor;
	}
}
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
	 * @param id id of the desired investor
	 * @return instance of the found investor
	 */
	public Investor find(int id) {
        try {
            String sql = "SELECT * FROM investor WHERE id = ?";
            return jdbc.queryForObject(sql, new Object[] { id }, this::mapRowToInvestor);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    /**
     * Update investor
     * @param id the id of the investor to update
     * @param update instance of the update request
     * @throws Exception 
     */
    public void update(int id, InvestorUpdateRequest update) throws Exception {
        try{
            UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("investor", id, this);
            updateQuery.setJdbc(jdbc);
            updateQuery.addField(update.getDescription(), "description");
            updateQuery.addField(update.getName(), "name");
            updateQuery.addField(update.getInvestmentMax(), "investmentMax");
            updateQuery.addField(update.getInvestmentMin(), "investmentMin");
            updateQuery.addField(update.getInvestorTypeId(), "investorTypeId");
            updateQuery.execute();
        } catch(Exception e) {
            throw new Error(e);
        }
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

        investor.setId(rs.getInt("id"));
        investor.setAccountId(rs.getInt("accountId"));
        investor.setInvestmentMax(rs.getInt("investmentMax"));
        investor.setInvestmentMin(rs.getInt("investmentMin"));
        investor.setInvestorTypeId(rs.getInt("investorTypeId"));
        investor.setName(rs.getString("name"));
        investor.setDescription(rs.getString("description"));

        return investor;
    }
    
    /**
	 * Add a new investor to the database
	 * @param investor the investor to add
	 */
	public void add(Investor investor) throws Exception {
		try {
            String query = "INSERT INTO investor(accountId, name, description, investmentMin," +
                            "investmentMax, investorTypeId) VALUES (?, ?, ?, ?, ?, ?);"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
                    ps.setInt(1,investor.getAccountId()); 
                    ps.setString(2, investor.getName());
                    ps.setString(3, investor.getDescription());
                    ps.setInt(4, investor.getInvestmentMin());
                    ps.setInt(5, investor.getInvestmentMax());
                    ps.setInt(6, investor.getInvestorTypeId());

					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}
}
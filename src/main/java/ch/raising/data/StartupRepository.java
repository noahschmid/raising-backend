package ch.raising.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

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
	 * @param accountId id of the desired startup account
	 * @return instance of the found startup
	 */
	public Startup find(int accountId) {
        try {
            String sql = "SELECT * FROM startup WHERE accountId = ?";
            return jdbc.queryForObject(sql, new Object[] { accountId }, this::mapRowToStartup);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    /**
     * Update startup
     * @param id the id of the startup to update
     * @param req instance of the update request
     * @throws Exception 
     */
    public void update(int id, StartupUpdateRequest req) throws Exception {
        try{
            UpdateQueryBuilder update = new UpdateQueryBuilder("investor", id, this);
            update.setJdbc(jdbc);
            update.addField(req.getName(), "name");
            update.addField(req.getDescription(), "description");
            update.addField(req.getInvestmentMax(), "investmentMax");
            update.addField(req.getInvestmentMin(), "investmentMin");
            update.addField(req.getBoosts(), "boosts");
            update.addField(req.getNumberOfFTE(), "numberoffte");
            update.addField(req.getTurnover(), "turnover");
            update.addField(req.getStreet(), "street");
            update.addField(req.getCity(), "city");
            update.addField(req.getWebsite(), "website");
            update.addField(req.getBreakEvenYear(), "breakevenyear");
            update.addField(req.getZipCode(), "zipcode");
            update.execute();
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
	private Startup mapRowToStartup(ResultSet rs, int rowNum) throws SQLException {
        Startup startup = new Startup();

        startup.setAccountId(rs.getInt("accountId"));
        startup.setInvestmentMax(rs.getInt("investmentMax"));
        startup.setInvestmentMin(rs.getInt("investmentMin"));
		startup.setBoosts(rs.getInt("boosts"));
		startup.setBreakEvenYear(rs.getInt("breakevenYear"));
		startup.setCity(rs.getString("city"));
		startup.setName(rs.getString("name"));
		startup.setZipCode(rs.getString("zipCode"));
		startup.setWebsite(rs.getString("website"));
		startup.setStreet(rs.getString("street"));
		startup.setInvestmentPhaseId(rs.getInt("investmentPhaseId"));
		startup.setTurnover(rs.getInt("turnover"));
		startup.setNumberOfFTE(rs.getInt("numberOfFTE"));

        return startup;
	}

	/**
	 * Add a new investor to the database
	 * @param startup the startup to add
	 */
	public void add(Startup startup) throws Exception {
		try {
            String query = "INSERT INTO investor(accountId, name, city, investmentMax," +
							"investmentMin, street, zipCode, website, numberOfFTE, breakEvenYear," +
							"turnover, investmentPhaseId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
                    ps.setInt(1,startup.getAccountId()); 
                    ps.setString(2, startup.getName());
                    ps.setString(3, startup.getCity());
                    ps.setInt(4, startup.getInvestmentMax());
                    ps.setInt(5, startup.getInvestmentMin());
					ps.setString(6, startup.getStreet());
					ps.setString(7, startup.getZipCode());
					ps.setString(8, startup.getWebsite());
					ps.setInt(9, startup.getNumberOfFTE());
					ps.setInt(10, startup.getBreakEvenYear());
					ps.setInt(11, startup.getTurnover());
					ps.setInt(12, startup.getInvestmentPhaseId());

					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}
}
package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IRepository;
import ch.raising.models.Relationship;
import ch.raising.models.RelationshipState;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class RelationshipRepository implements IRepository<Relationship> {
    private JdbcTemplate jdbc;

    @Autowired
    public RelationshipRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
	 * Find relationship by id
	 * @param id id of the desired investor
	 * @return instance of the found investor
	 */
	public Relationship find(long id) {
        try {
            String sql = "SELECT * FROM relationship WHERE id = ?";
            return jdbc.queryForObject(sql, new Object[] { id }, this::mapRowToModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Check whether given relationship already exists in the database
     * @param relationship
     * @return
     */
    public boolean exists(Relationship relationship) {
        try {
            String sql = "SELECT * FROM relationship WHERE investorId = ? AND startupId = ?";
            Relationship result = jdbc.queryForObject(sql, new Object[] { 
                relationship.getInvestorId(), 
                relationship.getStartupId() 
            }, this::mapRowToModel);
            
            if(result != null)
                return true;
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    /**
     * Update relationship
     * @param id the id of the relationship to update
     * @param update instance of the update request
     * @throws SQLException 
     * @throws DataAccessException 
     */
    public void update(long id, Relationship update) throws DataAccessException, SQLException {
        try {    
            UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("relationship", id, jdbc);
            updateQuery.addField(update.getInvestorId(), "investorId");
            updateQuery.addField(update.getStartupId(), "startupId");
            updateQuery.addField(update.getMatchingScore(), "matchingScore");
            updateQuery.addField(update.getState().toString(), "state");
            updateQuery.execute();
        } catch(Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Update specific relationship
     * @param update
     * @throws Exception
     */
    public void update(Relationship update) throws Exception {
        UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("relationship", 
            "startupId = " + update.getStartupId() + " AND investorId = " + 
            update.getInvestorId(), jdbc);
        updateQuery.addField(update.getMatchingScore(), "matchingScore");
        updateQuery.addField(update.getState().toString(), "state");
        updateQuery.execute(); 
    }


    /**
     * Update state of relationship
     * @param id the id of the relationship
     * @param state the new state
     */
    public void updateState(long id, RelationshipState state) throws Exception {
        UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("relationship", id, jdbc);
        updateQuery.addField(state.toString(), "state");
        updateQuery.execute();
    }

    /**
	 * Map a row of a result set to an relationship instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return Relationship instance of the result set
	 * @throws SQLException
	 */
	public Relationship mapRowToModel(ResultSet rs, int row) throws SQLException {
        Relationship relationship = new Relationship();

        relationship.setId(rs.getLong("id"));
        relationship.setInvestorId(rs.getLong("investorId"));
        relationship.setStartupId(rs.getLong("startupId"));
        relationship.setMatchingScore(rs.getInt("matchingScore"));
        relationship.setState(RelationshipState.valueOf(rs.getString("state")));

        return relationship;
    }
    
    /**
	 * Add a new relationship to the database
	 * @param relationship the relationship to add
	 */
	public void add(Relationship relationship) throws Exception {
		try {
            String query = "INSERT INTO relationship(investorId, startupId, matchingScore, state) VALUES (?, ?, ?, ?);"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
                    ps.setLong(1,relationship.getInvestorId()); 
                    ps.setLong(2, relationship.getStartupId());
                    ps.setInt(3, relationship.getMatchingScore());
                    ps.setString(4, relationship.getState() + "");

					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
    }
    
    /**
     * Get relationships by accountId
     * @param accountId id of the account to search for
     * @return list of all relationships containing given account
     */
    public List<Relationship> getByAccountId(long accountId) {
        try {
            String sql = "SELECT * FROM relationship WHERE startupId = ? OR investorId = ?;";
            return jdbc.query(sql, new Object[] { accountId, accountId }, this::mapRowToModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Get relationship by account and filter by given state
     * @param accountId the account to search for
     * @param state the state to search for
     * @return list of relationships
     */
    public List<Relationship> getByAccountIdAndState(long accountId, RelationshipState state) {
        try {
            String sql = "SELECT * FROM relationship WHERE (startupId = ? OR investorId = ?) AND " +
                         "state = ?;";
            return jdbc.query(sql, new Object[] { accountId, accountId, state.name() }, this::mapRowToModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
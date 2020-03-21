package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IRepository;
import ch.raising.models.Relationship;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class RelationshipRepository implements IRepository<Relationship, Relationship> {
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
     * Update relationship
     * @param id the id of the relationship to update
     * @param update instance of the update request
     * @throws Exception 
     */
    public void update(long id, Relationship update) throws Exception {
        try{
            UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("relationship", id, this);
            updateQuery.setJdbc(jdbc);
            updateQuery.addField(update.getInvestorId(), "investorId");
            updateQuery.addField(update.getStartupId(), "startupId");
            updateQuery.addField(update.getMatchingScore(), "matchingScore");
            updateQuery.addField(update.getState(), "state");
            updateQuery.execute();
        } catch(Exception e) {
            throw new Error(e);
        }
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
        relationship.setState(rs.getString("state"));

        return relationship;
    }
    
    /**
	 * Add a new relationship to the database
	 * @param relationship the relationship to add
	 */
	public void add(Relationship relationship) throws Exception {
		try {
            String query = "INSERT INTO relationship(investorId, startupId, matchingScore, state) VALUES (?, ?, ?, ?, ?, ?);"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
                    ps.setLong(1,relationship.getInvestorId()); 
                    ps.setLong(2, relationship.getStartupId());
                    ps.setInt(3, relationship.getMatchingScore());
                    ps.setString(4, relationship.getState());

					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}
}
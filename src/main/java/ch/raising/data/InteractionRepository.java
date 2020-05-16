package ch.raising.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ch.raising.models.Interaction;
import ch.raising.models.enums.InteractionType;
import ch.raising.models.enums.State;
import ch.raising.utils.DatabaseOperationException;

@Repository
public class InteractionRepository {

	private final JdbcTemplate jdbc;
	private final static String FIND_ALL = "SELECT * FROM interaction WHERE startupid = ? OR investorid = ?";
	private final static String INSERT_INTERACTION = "INSERT INTO interaction(relationshipid, startupid, investorid, interaction, startupstate, investorstate) VALUES (?,?,?,?,?,?)";
	private final static String FIND_BY_ACCOUNTID_AND_ID = "SELECT * FROM interaction WHERE id = ? AND (startupid =? OR investorid=?)";
	private final static String STARTUP_UPDATE = "UPDATE interaction SET startupstate = ?, acceptedat = now() WHERE id = ? AND startupid = ?";
	private final static String INVESTOR_UPDATE = "UPDATE interaction SET investorstate = ?, acceptedat = now() WHERE id = ? AND investorid = ?";
	private final static String DELETE_BY_INTERACTION_ID = "DELETE FROM interaction WHERE id = ?";
	private final static String FIND_BY_STARTUP_AND_INVESTOR_ID = "SELECT * FROM interaction WHERE investorid = ? AND startupid = ?";
	private final static String FIND_BY_REALTIONSHIP_ID = "SELECT * FROM interaction WHERE relationshipid = ?";


	private final RowMapper<Interaction> interactionMapper = new InteractionMapper();

	@Autowired
	public InteractionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Interaction> findAllByAccountId(long accountId) {
		return jdbc.query(FIND_ALL, new Object[] { accountId, accountId }, this.interactionMapper);
	}

	public List<Interaction> findByInvestorAndStartup(long investorId, long startupId) {
		return jdbc.query(FIND_BY_STARTUP_AND_INVESTOR_ID, new Object[] { investorId, startupId }, this.interactionMapper);
	}

	public long addInteraction(Interaction interaction) {
		return jdbc.execute(new AddAndReturnIdPreparedStatement(), insertInteractionCallback(interaction));
	}

	private PreparedStatementCallback<Long> insertInteractionCallback(Interaction interaction) {
		return new PreparedStatementCallback<Long>() {
			@Override
			public Long doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setInt(c++, interaction.getRelationshipId());
				ps.setLong(c++, interaction.getStartupId());
				ps.setLong(c++, interaction.getInvestorId());
				ps.setString(c++, interaction.getInteraction().name());
				ps.setString(c++, interaction.getStartupState().name());
				ps.setString(c++, interaction.getInvestorState().name());
				if(ps.executeUpdate() > 0) {
					if(ps.getGeneratedKeys().next()) {
						return ps.getGeneratedKeys().getLong("id");
					}
				}
				return -1l;
			};
		};
	}

	public Interaction findByAccountIdAndId(long interactionId, long accountId)
			throws EmptyResultDataAccessException, SQLException {
		return jdbc.queryForObject(FIND_BY_ACCOUNTID_AND_ID, new Object[] { interactionId, accountId, accountId },
				this.interactionMapper);
	}

	public void startupUpdate(State state, long interactionId, long startupId) throws DatabaseOperationException ,DataAccessException{
		int rowsAffected = jdbc.update(STARTUP_UPDATE, new Object[] { state, interactionId, startupId },
				new int[] { Types.VARCHAR, Types.BIGINT, Types.BIGINT });
		if (rowsAffected < 1)
			throw new DatabaseOperationException("the combination interactionid(" + interactionId + ") and startupId("
					+ startupId + ") could not be found");
	}

	public void investorUpdate(State state, long interactionId, long investorId) throws DatabaseOperationException, DataAccessException{
		int rowsAffected = jdbc.update(INVESTOR_UPDATE, new Object[] { state, interactionId, investorId },
				new int[] { Types.VARCHAR, Types.BIGINT, Types.BIGINT });
		if (rowsAffected < 1)
			throw new DatabaseOperationException("the combination interactionid(" + interactionId + ") and investorId("
					+ investorId + ") could not be found");
	}
	
	public void deleteByInteractionId(long interactionId) {
		jdbc.update(DELETE_BY_INTERACTION_ID, new Object[] {interactionId}, new int[] {Types.BIGINT});
	}
	
	public List<Interaction> findByRelationshipId(long rId) {
		return jdbc.query(FIND_BY_REALTIONSHIP_ID, new Object[] {rId}, new InteractionMapper());
	}
	
	private class AddAndReturnIdPreparedStatement implements PreparedStatementCreator{

		@Override
		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			return con.prepareStatement(INSERT_INTERACTION, Statement.RETURN_GENERATED_KEYS);
		}
		
	}
	
	public class InteractionMapper implements RowMapper<Interaction>{
		@Override
		public Interaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			return Interaction.builder()
					.id(rs.getLong("id"))
					.relationshipId(rs.getInt("relationshipid"))
					.startupId(rs.getLong("startupid"))
					.investorId(rs.getLong("investorid"))
					.interaction(InteractionType.valueOf(rs.getString("interaction")))
					.startupState(State.valueOf(rs.getString("startupstate")))
					.investorState(State.valueOf(rs.getString("investorstate")))
					.createdAt(rs.getTimestamp("createdat"))
					.acceptedAt(rs.getTimestamp("acceptedat"))
					.build();
		}
		
	}
}

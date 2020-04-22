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
import ch.raising.models.enums.InteractionTypes;
import ch.raising.models.enums.State;
import ch.raising.utils.DatabaseOperationException;

@Repository
public class InteractionRepository {

	private final JdbcTemplate jdbc;
	private final String FIND_ALL;
	private final String INSERT_INTERACTION;
	private final String FIND_BY_ACCOUNTID_AND_ID;
	private final String STARTUP_UPDATE;
	private final String INVESTOR_UPDATE;
	private final String DELETE_BY_INTERACTION_ID;
	private final RowMapper<Interaction> interactionMapper = new InteractionMapper();

	@Autowired
	public InteractionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.FIND_ALL = "SELECT * FROM interaction WHERE startupid = ? OR investorid = ?";
		this.INSERT_INTERACTION = "INSERT INTO interaction(startupid, investorid, interaction, startupstate, investorstate) VALUES (?,?,?,?,?)";
		this.FIND_BY_ACCOUNTID_AND_ID = "SELECT * FROM interaction WHERE id = ? AND (startupid =? OR investorid=?)";
		this.INVESTOR_UPDATE = "UPDATE interaction SET investorstate = ?, acceptedat = now() WHERE id = ? AND investorid = ?";
		this.STARTUP_UPDATE = "UPDATE interaction SET startupstate = ?, acceptedat = now() WHERE id = ? AND startupid = ?";
		this.DELETE_BY_INTERACTION_ID = "DELETE FROM interaction WHERE id = ?";
	}

	public List<Interaction> findAllByAccountId(long accountId) {
		return jdbc.query(FIND_ALL, new Object[] { accountId, accountId }, this.interactionMapper);
	}

	public long addInteraction(Interaction interaction) {
		return jdbc.execute(new AddInteractionPreparedStatement(), insertInteractionCallback(interaction));
	}

	private PreparedStatementCallback<Long> insertInteractionCallback(Interaction interaction) {
		return new PreparedStatementCallback<Long>() {
			@Override
			public Long doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
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
	
	private class AddInteractionPreparedStatement implements PreparedStatementCreator{

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
					.startupId(rs.getLong("startupid"))
					.investorId(rs.getLong("investorid"))
					.interaction(InteractionTypes.valueOf(rs.getString("interaction")))
					.startupState(State.valueOf(rs.getString("startupstate")))
					.investorState(State.valueOf(rs.getString("investorstate")))
					.createdAt(rs.getTimestamp("createdat"))
					.acceptedAt(rs.getTimestamp("acceptedat"))
					.build();
		}
		
	}

}

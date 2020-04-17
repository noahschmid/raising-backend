package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.models.Interaction;
import ch.raising.models.InteractionState;
import ch.raising.models.State;
import ch.raising.utils.DatabaseOperationException;

@Repository
public class InteractionRepository {

	private final JdbcTemplate jdbc;
	private final String FIND_ALL;
	private final String INSERT_INTERACTION;
	private final String FIND_BY_ACCOUNTID_AND_ID;
	private final String STARTUP_UPDATE;
	private final String INVESTOR_UPDATE;

	@Autowired
	public InteractionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.FIND_ALL = "SELECT * FROM interaction WHERE startupid = ? OR investorid = ?";
		this.INSERT_INTERACTION = "INSERT INTO interaction(startupid, investorid, interaction, startupstate,investorstate) VALUES (?,?,?,?,?)";
		this.FIND_BY_ACCOUNTID_AND_ID = "SELECT * FROM interaction WHERE id = ? AND (startupid =? OR investorid=?)";
		this.INVESTOR_UPDATE = "UPDATE interaction set investorstate = ? WHERE id = ? AND investorid = ?";
		this.STARTUP_UPDATE = "UPDATE interaction set startupstate = ? WHERE id = ? AND startupid = ?";
	}

	public List<Interaction> findAll(long accountId) {
		return jdbc.query(FIND_ALL, new Object[] { accountId, accountId }, this::mapRowToInteraction);
	}

	public Interaction mapRowToInteraction(ResultSet rs, int row) throws SQLException {
		return Interaction.builder().id(rs.getLong("id")).startupId(rs.getLong("startupid"))
				.investorId(rs.getLong("investorid")).interaction(InteractionState.valueOf(rs.getString("interaction")))
				.startupState(State.valueOf(rs.getString("startupstate")))
				.investorState(State.valueOf(rs.getString("investorState"))).build();
	}

	public void addInteraction(Interaction interaction) {
		jdbc.execute(INSERT_INTERACTION, insertInteractionCallback(interaction));
	}

	private PreparedStatementCallback<Boolean> insertInteractionCallback(Interaction interaction) {
		return new PreparedStatementCallback<Boolean>() {

			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setLong(c++, interaction.getStartupId());
				ps.setLong(c++, interaction.getInvestorId());
				ps.setString(c++, interaction.getInteraction().name());
				ps.setString(c++, interaction.getStartupState().name());
				ps.setString(c++, interaction.getInvestorState().name());
				return ps.execute();
			};
		};
	}

	public Interaction findByAccountIdAndId(long interactionId, long accountId)
			throws EmptyResultDataAccessException, SQLException {
		return jdbc.queryForObject(FIND_BY_ACCOUNTID_AND_ID, new Object[] { interactionId, accountId, accountId },
				this::mapRowToInteraction);
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

}

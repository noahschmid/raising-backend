package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.raising.models.Interaction;
import ch.raising.models.InteractionState;
import ch.raising.models.State;

@Repository
public class InteractionRepository {

	private final JdbcTemplate jdbc;
	private final String FIND_ALL;

	@Autowired
	public InteractionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.FIND_ALL = "SELECT * FROM interaction WHERE startupid = ? OR investorid = ?";
	}

	public List<Interaction> findAll(long accountId) {
		return jdbc.query(FIND_ALL, new Object[] { accountId, accountId }, this::mapRowToInteraction);
	}

	private Interaction mapRowToInteraction(ResultSet rs, int row) throws SQLException {
		return Interaction.builder().id(rs.getLong("id")).startupId(rs.getLong("startupid")).investorId(rs.getLong("investorid"))
				.interaction(InteractionState.valueOf(rs.getString("interaction")))
				.startupState(State.valueOf(rs.getString("startupstate")))
				.investorState(State.valueOf(rs.getString("startupstate"))).build();
	}

}

package ch.raising.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.sql.SQLException;
import java.sql.Types;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import ch.raising.data.InteractionRepository;
import ch.raising.models.Interaction;
import ch.raising.models.enums.InteractionType;
import ch.raising.models.enums.State;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@SpringBootTest

@ContextConfiguration(classes = { TestConfig.class })
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("RepositoryTest")
class InteractionRepositoryTest {

	private final JdbcTemplate jdbc;

	private final long id = 11;
	private final long startupId = 123;
	private final long investorId = 43;
	private final State startupState = State.OPEN;
	private final State investorState = State.OPEN;
	private final InteractionType interactionState = InteractionType.COFFEE;
	private Interaction interaction;

	private final InteractionRepository interactionRepo;

	@Autowired
	public InteractionRepositoryTest(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.interactionRepo = new InteractionRepository(jdbc);
	}

	@BeforeEach
	void setup() throws SQLException {
		String create = QueryBuilder.getInstance().tableName("interaction").pair("id", Type.SERIAL)
				.pair("startupid", Type.BIGINT).pair("investorid", Type.BIGINT).pair("interaction", Type.VARCHAR)
				.pair("startupstate", Type.VARCHAR).pair("investorstate", Type.VARCHAR)
				.pair("createdat", Type.TIMESTAMP).pair("acceptedat", Type.TIMESTAMP).pair("relationshipid", Type.BIGINT).createTable();
		jdbc.execute(create);
		insert();
	}

	void insert() throws SQLException {
		String insert = QueryBuilder.getInstance().tableName("interaction")
				.attribute("id, startupid, investorid, interaction, startupstate, investorstate").value(id)
				.value(startupId).value(investorId).value(interactionState.name()).value(startupState.name())
				.value(investorState.name()).insert();
		jdbc.execute(insert);

		interaction = Interaction.builder().id(id).startupId(startupId).investorId(investorId)
				.interaction(interactionState).startupState(startupState).investorState(investorState).build();

	}

	@AfterEach
	void cleanUp() throws SQLException {
		JdbcTestUtils.dropTables(jdbc, "interaction");
	}

	@Test
	void testFindAllStartup() {
		List<Interaction> foundStartup = interactionRepo.findAllByAccountId(startupId);
		assertNotNull(foundStartup);
		assertEquals(1, foundStartup.size());
		Interaction found = foundStartup.get(0);
		assertEquals(interaction.getInteraction(), found.getInteraction());

	}

	@Test
	void testFindAllInvestor() {
		List<Interaction> foundStartup = interactionRepo.findAllByAccountId(investorId);
		assertNotNull(foundStartup);
		assertEquals(1, foundStartup.size());
		Interaction found = foundStartup.get(0);
		assertEquals(interaction.getData(), found.getData());

	}

	@Test
	void addInteraction() {
		Interaction insert = Interaction.builder().id(12).relationshipId(132).startupId(13).investorId(45)
				.interaction(InteractionType.EMAIL).startupState(State.OPEN).investorState(State.OPEN).build();
		interactionRepo.addInteraction(insert);
		Interaction found = jdbc.queryForObject("SELECT * FROM interaction WHERE startupid = 13",
				interactionRepo.new InteractionMapper());
		assertEquals(insert.getRelationshipId(), found.getRelationshipId());
	}

	@Test
	void testFindByAccountIdAndId() throws EmptyResultDataAccessException, SQLException {
		Interaction found = interactionRepo.findByAccountIdAndId(id, startupId);
		assertNotNull(found);
		assertEquals(interaction.getStartupId(), found.getStartupId());
	}

	@Test
	void testStartupUpdate() throws DataAccessException, DatabaseOperationException {
		interactionRepo.startupUpdate(State.ACCEPTED, id, startupId);
		Interaction found = jdbc.queryForObject("SELECT * FROM interaction WHERE id = " + id,
				interactionRepo.new InteractionMapper());
		assertEquals(State.ACCEPTED, found.getStartupState());
	}

	@Test
	void testStartupUpdateWrongId() throws DataAccessException, DatabaseOperationException {
		assertThrows(DatabaseOperationException.class, () -> {
			interactionRepo.startupUpdate(State.ACCEPTED, id, 12313);
		});
	}

	@Test
	void testInvestorUpdate() throws DataAccessException, DatabaseOperationException {
		interactionRepo.investorUpdate(State.ACCEPTED, id, investorId);
		Interaction foundInt = jdbc.queryForObject("SELECT * FROM interaction WHERE id = " + id,
				interactionRepo.new InteractionMapper());

		assertEquals(State.ACCEPTED, foundInt.getInvestorState());
	}

	@Test
	void testInvestorUpdateWrongId() throws DataAccessException, DatabaseOperationException {
		assertThrows(DatabaseOperationException.class, () -> {
			interactionRepo.investorUpdate(State.ACCEPTED, id, 12313);
		});
	}

}

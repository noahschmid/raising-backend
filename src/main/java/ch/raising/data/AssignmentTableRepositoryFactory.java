package ch.raising.data;

import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.raising.utils.MapUtil;

@Repository
public class AssignmentTableRepositoryFactory {

	private final JdbcTemplate jdbc;

	public AssignmentTableRepositoryFactory(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public AssignmentTableRepository getRepository(String table) throws SQLException {

		switch (table) {
		case "country":
			return new AssignmentTableRepository(jdbc, "country").withRowMapper(MapUtil::mapRowToCountry);
		case "continent":
			return new AssignmentTableRepository(jdbc, "continent");
		case "support":
			return new AssignmentTableRepository(jdbc, "support").withRowMapper(MapUtil::mapRowToAssignmentTableWithIcon);
		case "industry":
			return new AssignmentTableRepository(jdbc, "industry").withRowMapper(MapUtil::mapRowToAssignmentTableWithIcon);
		case "revenue":
			return new AssignmentTableRepository(jdbc, "revenue").withRowMapper(MapUtil::mapRowToRevenue);
		case "ticketsize":
			return new AssignmentTableRepository(jdbc, "ticketsize");
		case "investmentphase":
			return new AssignmentTableRepository(jdbc, "investmentphase").withRowMapper(MapUtil::mapRowToAssignmentTableWithIcon);
		case "corporateBody":
			return new AssignmentTableRepository(jdbc, "corporateBody");
		case "financeType":
			return new AssignmentTableRepository(jdbc, "financeType");
		case "boardmemberType":
			return new AssignmentTableRepository(jdbc, "boardmemberType");
		default:
			try {
				return getRepositoryForStartup(table);
			} catch (SQLException e) {
				try {
					return getRepositoryForInvestor(table);
				} catch (SQLException es) {
					throw new SQLException("assignmenttable " + table + " does not exist: " + es.getMessage());
				}
			}
		}

	}

	public AssignmentTableRepository getRepositoryForStartup(String table) throws SQLException {
		switch (table) {
		case "label":
			return new AssignmentTableRepository(jdbc, "label", "startupid")
					.withRowMapper(MapUtil::mapRowToAssignmentTableWithDescriptionAndIcon);
		case "investortype":
			return new AssignmentTableRepository(jdbc, "investortype", "startupid")
					.withRowMapper(MapUtil::mapRowToAssignmentTableWithDescriptionAndIcon);
		default:
			throw new SQLException("assignmenttable " + table + " does not exist for startup");

		}
	}

	public AssignmentTableRepository getRepositoryForInvestor(String table) throws SQLException {
		switch (table) {
		case "investmentphase":
			return new AssignmentTableRepository(jdbc, "investmentphase", "investorid").withRowMapper(MapUtil::mapRowToAssignmentTableWithIcon);
		default:
			throw new SQLException("assignmenttable " + table + " does not exist for investor");
		}
	}

}

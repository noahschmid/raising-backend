package ch.raising.utils;

import java.sql.ResultSet;


import java.sql.SQLException;

import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.Account;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;

public class MapUtil {
	
	public static MapUtil getInstance() {
		return new MapUtil();
	}
	
	public static long mapRowToId(ResultSet rs, int row) throws SQLException {
		return rs.getLong("id");
	}

	public static long mapRowToAccountId(ResultSet rs, int row) throws SQLException {
		return rs.getLong("accountid");
	}

	public static Account mapRowToAccount(ResultSet rs, int row) throws SQLException {
		return Account.accountBuilder().name(rs.getString("name")).email(rs.getString("emailhash"))
				.accountId(rs.getLong("tableEntryId")).investmentMax(rs.getInt("investmentmax"))
				.investmentMin(rs.getInt("investmentmin")).build();
	}
	
	public static AssignmentTableModel mapRowToAssignmentTable(ResultSet rs, int row) throws SQLException{
		return new AssignmentTableModel(rs.getString("name"), rs.getLong("id"));
	}
	
	public static Country mapRowToCountry(ResultSet rs, int row) throws SQLException{
		return new Country(rs.getString("name"), rs.getLong("id"), rs.getLong("continentid"));
	}
	
}
package ch.rasing.raisingbackend.utils;

import java.sql.ResultSet;

import java.sql.SQLException;

import ch.raising.models.Account;

public class MappingUtil {

	public static long mapRowToId(ResultSet rs, int row) throws SQLException {
		return rs.getLong("id");
	}

	public static long mapRowToAccountId(ResultSet rs, int row) throws SQLException {
		return rs.getLong("accountid");
	}

	public static Account mapRowToAccount(ResultSet rs, int row) throws SQLException {
		return Account.accountBuilder().name(rs.getString("name")).email(rs.getString("emailhash"))
				.accountId(rs.getLong("id")).investmentMax(rs.getInt("investmentmax"))
				.investmentMin(rs.getInt("investmentmin")).build();
	}
}
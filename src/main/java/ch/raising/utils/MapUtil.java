package ch.raising.utils;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.Arrays;

import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.Account;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.AssignmentTableModelWithDescription;
import ch.raising.models.Contact;
import ch.raising.models.CorporateShareholder;
import ch.raising.models.Country;
import ch.raising.models.Founder;
import ch.raising.models.Image;
import ch.raising.models.PrivateShareholder;

public class MapUtil {

	public static long mapRowToId(ResultSet rs, int row) throws SQLException {
		return rs.getLong("id");
	}

	public static long mapRowToAccountId(ResultSet rs, int row) throws SQLException {
		return rs.getLong("accountid");
	}

	public static Account mapRowToAccount(ResultSet rs, int row) throws SQLException {
		return Account.accountBuilder().name(rs.getString("name")).email(rs.getString("emailhash")).company(rs.getString("company"))
				.accountId(rs.getLong("id")).ticketMaxId(rs.getInt("ticketmaxid"))
				.ticketMinId(rs.getInt("ticketminid")).build();
	}

	public static AssignmentTableModel mapRowToAssignmentTable(ResultSet rs, int row) throws SQLException {
		return new AssignmentTableModel(rs.getString("name"), rs.getInt("id"));
	}

	public static AssignmentTableModelWithDescription mapRowToAssignmentTableWithDescription(ResultSet rs, int row)
			throws SQLException {
		return new AssignmentTableModelWithDescription(rs.getString("name"), rs.getLong("id"),
				rs.getString("description"));
	}

	public static Country mapRowToCountry(ResultSet rs, int row) throws SQLException {
		return new Country(rs.getString("name"), rs.getLong("id"), rs.getLong("continentid"));
	}

	public static AssignmentTableModel mapRowToRevenue(ResultSet rs, int row) throws SQLException {
		return new AssignmentTableModel(rs.getString("step"), rs.getLong("id"));
	}

	public static Founder mapRowToFounder(ResultSet rs, int row) throws SQLException {
		return Founder.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid"))
				.firstName(rs.getString("firstname")).lastName(rs.getString("lastname"))
				.education(rs.getString("education")).position(rs.getString("position")).build();
	}

	public static Contact mapRowToContact(ResultSet rs, int row) throws SQLException {
		return Contact.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid"))
				.firstName(rs.getString("firstname")).lastName(rs.getString("lastname")).email(rs.getString("email"))
				.position(rs.getString("position")).phone(rs.getString("phone")).build();
	}

	public static PrivateShareholder mapRowToPrivateShareholder(ResultSet rs, int row) throws SQLException {
		return PrivateShareholder.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid"))
				.firstName(rs.getString("firstname")).lastName(rs.getString("lastname")).city(rs.getString("city"))
				.equityShare(rs.getInt("equityshare")).investortypeId(rs.getLong("investortypeid"))
				.countryId(rs.getLong("countryid")).build();
	}
	
	public static CorporateShareholder mapRowToCorporateShareholder(ResultSet rs, int row) throws SQLException {
		return CorporateShareholder.builder().id(rs.getLong("id")).startupId(rs.getLong("startupid"))
				.corpName(rs.getString("name")).website(rs.getString("website"))
				.equityShare(rs.getInt("equityshare")).corporateBodyId(rs.getLong("corporatebodyid"))
				.countryId(rs.getLong("countryid")).build();
	}
	
	public static Image mapRowToImage(ResultSet rs, int row) throws SQLException{
		return Image.builder().id(rs.getLong("id")).accountId(rs.getLong("accountid")).image(new String(rs.getBytes("image"))).build();
	}

}
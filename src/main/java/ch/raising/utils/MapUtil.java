package ch.raising.utils;

import java.sql.ResultSet;

import java.sql.SQLException;

import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.Account;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.AssignmentTableModelWithDescription;
import ch.raising.models.CorporateShareholder;
import ch.raising.models.Country;
import ch.raising.models.Founder;
import ch.raising.models.Media;
import ch.raising.models.Investor;
import ch.raising.models.PrivateShareholder;

public class MapUtil {

	public static long mapRowToId(ResultSet rs, int row) throws SQLException {
		return rs == null ? -1 : rs.getLong("id");
	}

	public static long mapRowToAccountId(ResultSet rs, int row) throws SQLException {
		return rs == null ? -1 : rs.getLong("accountid");
	}

	public static Account mapRowToAccount(ResultSet rs, int row) throws SQLException {
		return Account.accountBuilder().firstName(rs.getString("firstname")).lastName(rs.getString("lastname"))
				.email(rs.getString("emailhash")).description(rs.getString("description")).pitch(rs.getString("pitch"))
				.companyName(rs.getString("companyName")).roles(rs.getString("roles")).accountId(rs.getLong("id"))
				.ticketMaxId(rs.getInt("ticketmaxid")).ticketMinId(rs.getInt("ticketminid"))
				.countryId(rs.getLong("countryid")).website(rs.getString("website"))
				.profilePictureId(rs.getLong("profilepictureid")).build();
	}

	public static AssignmentTableModel mapRowToAssignmentTable(ResultSet rs, int row) throws SQLException {
		return new AssignmentTableModel(rs.getString("name"), rs.getInt("id"));
	}

	public static long mapRowToFirstEntry(ResultSet rs, int row) throws SQLException {
		return rs.getLong(1);
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
				.education(rs.getString("education")).position(rs.getString("position"))
				.countryId(rs.getLong("countryid")).build();
	}

	public static PrivateShareholder mapRowToPrivateShareholder(ResultSet rs, int row) throws SQLException {
		return PrivateShareholder.builder().id(rs.getLong("id")).startupid(rs.getLong("startupid"))
				.firstName(rs.getString("firstname")).lastName(rs.getString("lastname")).city(rs.getString("city"))
				.equityShare(rs.getInt("equityshare")).investorTypeId(rs.getLong("investortypeid"))
				.countryId(rs.getLong("countryid")).build();
	}

	public static CorporateShareholder mapRowToCorporateShareholder(ResultSet rs, int row) throws SQLException {
		return CorporateShareholder.builder().id(rs.getLong("id")).startupId(rs.getLong("startupid"))
				.corpName(rs.getString("name")).website(rs.getString("website")).equityShare(rs.getInt("equityshare"))
				.corporateBodyId(rs.getLong("corporatebodyid")).countryId(rs.getLong("countryid")).build();
	}

	public static Media mapRowToMedia(ResultSet rs, int row) throws SQLException {
		return rs == null ? Media.builder().build()
				: Media.builder().id(rs.getLong("id")).accountId(rs.getLong("accountid")).media(rs.getBytes("media")).contentType(rs.getString("type"))
						.build();
	}

	public static Investor mapRowToInvestor(ResultSet rs, int row) throws SQLException {
		return Investor.investorBuilder().accountId(rs.getLong("accountid"))
				.investorTypeId(rs.getLong("investortypeid")).build();
	}

}
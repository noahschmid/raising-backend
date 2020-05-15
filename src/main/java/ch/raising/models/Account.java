package ch.raising.models;

import java.util.List;

import com.google.api.services.androidpublisher.model.Timestamp;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Account {

	/*
	 * =========================================================================
	 * Stored in the accounttable
	 */
	protected long accountId = -1l;
	private String firstName = "";
	private String lastName = "";
	protected String companyName = "";
	private String password = "";
	private String roles = "";
	private String email = "";
	private String pitch = "";
	private String description = "";
	private int ticketMinId = -1;
	private int ticketMaxId = -1;
	private long countryId = -1;
	private String website = "";
	private long profilePictureId = -1;
	private Timestamp lastChanged;

	/* ========================================================================= */

	/*
	 * =========================================================================
	 * Stored in seperate tables with respective respecitve names
	 */

	private List<Long> gallery;
	private List<Long> countries;
	private List<Long> continents;
	private List<Long> support;
	private List<Long> industries;
	/* ========================================================================= */

	/**
	 * This constructor makes an Account represented by the account table in the
	 * database. That means the country, continent, support, industries lists,
	 * profilepicture and gallery will not be initialized. It should be used by the
	 * {@link ch.raising.test.data.AccountRepository} and
	 * {@link ch.raising.services.AccountService#registerAccount()}.
	 * 
	 * @param accountId
	 * @param name
	 * @param password
	 * @param roles
	 * @param email
	 * @param ticketMinId
	 * @param profilePictureId
	 * @param firstName
	 * @param lastName
	 * @param ticketmentId
	 */

	@Builder(builderMethodName = "accountBuilder")
	public Account(long accountId, long countryId, String password, String roles, String email, int ticketMinId,
			int ticketMaxId, String companyName, String description, String pitch, String website,
			long profilePictureId, String firstName, String lastName) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.companyName = companyName;
		this.accountId = accountId;
		this.password = password;
		this.roles = roles;
		this.email = email;
		this.description = description;
		this.pitch = pitch;
		this.ticketMinId = ticketMinId;
		this.ticketMaxId = ticketMaxId;
		this.countryId = countryId;
		this.website = website;
		this.profilePictureId = profilePictureId;
	}

	/**
	 * this constructor makes a complete Account object
	 * 
	 * @param continents
	 * 
	 * @param support
	 * 
	 * @param accountId
	 * @param name
	 * @param password
	 * @param roles
	 * @param email
	 * @param ticketMinId
	 * @param investmentMax
	 * @param countries
	 * @param continents
	 * @param support
	 * @param continents
	 * @param countries
	 * @param industries
	 * @param industries
	 */
	public Account(Account acc) {
		this.firstName = acc.firstName;
		this.lastName = acc.lastName;
		this.companyName = acc.companyName;
		this.accountId = acc.accountId;
		this.password = acc.password;
		this.roles = acc.roles;
		this.email = acc.email;
		this.description = acc.description;
		this.pitch = acc.pitch;
		this.ticketMinId = acc.ticketMinId;
		this.ticketMaxId = acc.ticketMaxId;
		this.countries = acc.countries;
		this.continents = acc.continents;
		this.support = acc.support;
		this.industries = acc.industries;
		this.pitch = acc.pitch;
		this.description = acc.description;
		this.countries = acc.countries;
		this.continents = acc.continents;
		this.support = acc.support;
		this.industries = acc.industries;
		this.profilePictureId = acc.profilePictureId;
		this.gallery = acc.gallery;
		this.countryId = acc.countryId;
		this.website = acc.website;
	}

	public boolean isComplete() {
		return (roles == "ROLE_ADMIN" && password != "" && email != "") || (firstName != "" && lastName != "" && password != "" && email != ""
				&& ticketMinId != -1 && ticketMaxId != -1 && countryId != -1 && support != null && industries != null
				&& !(countries.isEmpty() && continents.isEmpty()) && !support.isEmpty() && !industries.isEmpty());
	}

	public boolean isStartup() {
		return false;
	}

	public boolean isInvestor() {
		return false;
	}
}

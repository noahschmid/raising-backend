package ch.raising.models;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Account{

	protected long accountId = -1l;
	protected String name;
	private String password;
	private String roles;
	private String email;
	private int investmentMin = -1;
	private int investmentMax = -1;

	private List<Country> countries;
	private List<Continent> continents;
	private List<Support> support;
	private List<Industry> industries;

	/**
	 * This constructor makes an Account represented by the account table in the
	 * database. That means the country, continent, support, industries lists will
	 * not be initialized. It should be used by the
	 * {@link ch.raising.data.AccountRepository} and
	 * {@link ch.raising.services.AccountService#registerAccount()}.
	 * 
	 * @param accountId
	 * @param name
	 * @param password
	 * @param roles
	 * @param email
	 * @param investmentMin
	 * @param investmentMax
	 */

	@Builder(builderMethodName = "accountBuilder")
	public Account(long accountId, String name, String password, String roles, String email, int investmentMin,
			int investmentMax) {

		this.accountId = accountId;
		this.name = name;
		this.password = password;
		this.roles = roles;
		this.email = email;
		this.investmentMin = investmentMin;
		this.investmentMax = investmentMax;

	}

	/**
	 * this constructor makes a complete Account object
	 * 
	 * @param accountId
	 * @param name
	 * @param password
	 * @param roles
	 * @param email
	 * @param investmentMin
	 * @param investmentMax
	 * @param countries
	 * @param continents
	 * @param support
	 * @param industries
	 */
	public Account(Account acc) {
		this.accountId = acc.accountId;
		this.name = acc.name;
		this.password = acc.password;
		this.roles = acc.roles;
		this.email = acc.email;
		this.investmentMin = acc.investmentMin;
		this.investmentMax = acc.investmentMax;
		this.countries = acc.countries;
		this.continents = acc.continents;
		this.support = acc.support;
		this.industries = acc.industries;
	}
	
	public boolean isInComplete() {
		return email == null || password == null || name == null || roles == null
				|| investmentMin == -1 || investmentMax == -1 || countries == null
				|| continents == null || support == null ||industries == null
				||industries.isEmpty() || support.isEmpty() || continents.isEmpty()
				||countries.isEmpty();
	}

}

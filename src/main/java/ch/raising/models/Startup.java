package ch.raising.models;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Startup extends Account{

	private long investmentPhaseId = -1;
	private int boosts = 0;
	private String street;
	private String city;
	private String zipCode;
	private String website;
	private int breakEvenYear = -1;
	private int numberOfFTE = -1;
	private int turnover = -1;
	private List<InvestorType> invTypes;
	private List<Label> labels;
	private Contact contact;
	private List<Founder> founders;
	private String description;

	/**
	 * creates the startup with all lists initialized. should be used to return a
	 * fully initialized startup to the {@link AccountController}.
	 * 
	 * @param account  the account with all lists initialized
	 * @param su       as represented by the startup table in the database
	 * @param invTypes
	 * @param labels
	 * @param contact
	 * @param founders
	 */
	public Startup(Account account, Startup su, List<InvestorType> invTypes, List<Label> labels, Contact contact,
			List<Founder> founders) {
		super(account);
		this.investmentPhaseId = su.getInvestmentPhaseId();
		this.boosts = su.getBoosts();
		this.numberOfFTE = su.getNumberOfFTE();
		this.turnover = su.getTurnover();
		this.street = su.getStreet();
		this.city = su.getCity();
		this.website = su.getWebsite();
		this.breakEvenYear = su.getBreakEvenYear();
		this.zipCode = su.getZipCode();
		this.invTypes = invTypes;
		this.labels = labels;
		this.contact = contact;
		this.founders = founders;
	}

	/**
	 * Builds the startup represented by the startuptable in the database. Should be
	 * used by {@link StartupRepository}
	 * 
	 * @param accountId
	 * @param investmentPhaseId
	 * @param boosts
	 * @param numberOfFTE
	 * @param turnover
	 * @param street
	 * @param city
	 * @param website
	 * @param breakEvenYear
	 * @param zipCode
	 */
	@Builder(builderMethodName = "startupBuilder")
	public Startup(long accountId, long investmentPhaseId, int boosts, int numberOfFTE, int turnover, String street,
			String city, String website, int breakEvenYear, String zipCode) {
		super();
		this.accountId = accountId;
		this.investmentPhaseId = investmentPhaseId;
		this.boosts = boosts;
		this.numberOfFTE = numberOfFTE;
		this.turnover = turnover;
		this.street = street;
		this.city = city;
		this.website = website;
		this.breakEvenYear = breakEvenYear;
		this.zipCode = zipCode;
	}
	
	@Override
	public boolean isInComplete() {
		return super.isInComplete() || investmentPhaseId == -1 || boosts != 0 || street == null
				|| city == null || zipCode == null || website == null || breakEvenYear == -1
				|| numberOfFTE == -1 || turnover == -1 || contact == null
				|| invTypes == null || labels == null || founders == null
				|| invTypes.isEmpty() || labels.isEmpty() || founders.isEmpty();
	}

}
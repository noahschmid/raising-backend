package ch.raising.models;

import java.util.Date;
import java.util.List;

import ch.raising.models.AssignmentTableModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Startup extends Account {

	private long investmentPhaseId = -1;
	private int boosts = 0;
	private String street;
	private String city;
	private int zipCode;
	private String website;
	private int breakEvenYear = -1;
	private int numberOfFTE = -1;
	private int turnover = -1;
	private int preMoneyevaluation = -1;
	private Date closingtime;
	private int revenueMax;
	private int revenueMin;
	private int scope;
	private String uId;
	private int foundingyear;
	private long financeTypeId = -1;

	private List<AssignmentTableModel> investorTypes;
	private List<AssignmentTableModel> labels;
	private Contact contact;
	private List<Founder> founders;
	private List<PrivateShareholder> privateShareholders;
	private List<CorporateShareholder> corporateShareholders;

	/**
	 * creates the startup with all lists initialized. should be used to return a
	 * fully initialized startup to the {@link AccountController}.
	 * 
	 * @param account            the account with all lists initialized
	 * @param su                 as represented by the startup table in the database
	 * @param investorTypes
	 * @param labels
	 * @param contact
	 * @param founders
	 * @param preMoneyevaluation
	 */
	public Startup(Account account, Startup su, List<AssignmentTableModel> invTypes,
			List<AssignmentTableModel> labels, Contact contact, List<Founder> founders,
			List<PrivateShareholder> pShareholders, List<CorporateShareholder> cShareholders) {
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
		this.preMoneyevaluation = su.getPreMoneyevaluation();
		this.revenueMax = su.getRevenueMax();
		this.revenueMin = su.getRevenueMin();
		this.scope = su.getScope();
		this.uId = su.getUId();
		this.foundingyear = su.getFoundingyear();
		this.financeTypeId = su.getFinanceTypeId();
		this.closingtime = su.getClosingtime();
		this.investorTypes = invTypes;
		this.labels = labels;
		this.contact = contact;
		this.founders = founders;
		this.privateShareholders = pShareholders;
		this.corporateShareholders = cShareholders;
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
	 * @param revenueMin
	 * @param scope
	 * @param uId
	 * @param foundingyear
	 */
	@Builder(builderMethodName = "startupBuilder")
	public Startup(long accountId, long investmentPhaseId, int boosts, int numberOfFTE, int turnover, String street,
			String city, String website, int breakEvenYear, int zipCode, int preMoneyevaluation, int revenueMax,
			long financeTypeId, Date closingtime, int revenueMin, int scope, String uId, int foundingyear) {
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
		this.preMoneyevaluation = preMoneyevaluation;
		this.revenueMax = revenueMax;
		this.revenueMin = revenueMin;
		this.scope = scope;
		this.uId = uId;
		this.foundingyear = foundingyear;
		this.financeTypeId = financeTypeId;
		this.closingtime = closingtime;
	}

	@Override
	public boolean isInComplete() {
		return super.isInComplete() ;//|| investmentPhaseId == -1 || boosts != 0 || street == null || city == null
//				|| zipCode == null || website == null || breakEvenYear == -1 || numberOfFTE == -1 || turnover == -1
//				|| financeTypeId == -1 || closingtime == null || contact == null || investorTypes == null || labels == null
//				|| founders == null || investorTypes.isEmpty() || labels.isEmpty() || founders.isEmpty();
	}

}
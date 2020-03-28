package ch.raising.models;

import java.sql.Date;

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
	private String street = "";
	private String city = "";
	private int zipCode;
	private String website = "";
	private int breakEvenYear = -1;
	private int numberOfFte = -1;
	private int turnover = -1;
	private int preMoneyValuation = -1;
	private Date closingTime;

	private int revenueMaxId;
	private int revenueMinId;

	private int scope;
	private String uId = "";
	private int foundingYear;
	private long financeTypeId = -1;
	private String companyName = "";
	private int raised;

	private List<AssignmentTableModel> investorTypes;
	private List<AssignmentTableModel> labels;
	private List<Boardmember> boardmembers;
	private Contact contact;
	private List<Founder> founders;
	private List<PrivateShareholder> privateShareholders;
	private List<CorporateShareholder> corporateShareholders;

	/**
	 * creates the startup with all lists initialized. should be used to return a
	 * fully initialized startup to the {@link AccountController}.
	 * 
	 * @param account           the account with all lists initialized
	 * @param su                as represented by the startup table in the database
	 * @param investorTypes
	 * @param labels
	 * @param contact
	 * @param founders
	 * @param preMoneyValuation
	 */
	public Startup(Account account, Startup su, List<AssignmentTableModel> invTypes, List<AssignmentTableModel> labels,
			Contact contact, List<Founder> founders, List<PrivateShareholder> pShareholders,
			List<CorporateShareholder> cShareholders, List<Boardmember> boardMembers) {
		super(account);
		this.investmentPhaseId = su.getInvestmentPhaseId();
		this.boosts = su.getBoosts();
		this.numberOfFte = su.getNumberOfFte();
		this.turnover = su.getTurnover();
		this.street = su.getStreet();
		this.city = su.getCity();
		this.website = su.getWebsite();
		this.breakEvenYear = su.getBreakEvenYear();
		this.zipCode = su.getZipCode();
		this.preMoneyValuation = su.getPreMoneyValuation();
		this.revenueMaxId = su.getRevenueMaxId();
		this.revenueMinId= su.getRevenueMinId();
		this.scope = su.getScope();
		this.uId = su.getUId();
		this.foundingYear = su.getFoundingYear();
		this.financeTypeId = su.getFinanceTypeId();
		this.closingTime = su.getClosingTime();
		this.company = su.getCompany();
		this.raised = su.getRaised();
		this.investorTypes = invTypes;
		this.labels = labels;
		this.contact = contact;
		this.founders = founders;
		this.privateShareholders = pShareholders;
		this.corporateShareholders = cShareholders;
		this.boardmembers = boardMembers;
	}

	/**
	 * Builds the startup represented by the startuptable in the database. Should be
	 * used by {@link StartupRepository}
	 * 
	 * @param accountId
	 * @param investmentPhaseId
	 * @param boosts
	 * @param numberOfFte
	 * @param turnover
	 * @param street
	 * @param city
	 * @param website
	 * @param breakEvenYear
	 * @param zipCode
	 * @param revenueMinId
	 * @param scope
	 * @param uId
	 * @param foundingYear
	 */
	@Builder(builderMethodName = "startupBuilder")
	public Startup(long accountId, long investmentPhaseId, int boosts, int numberOfFte, int turnover, String street,
			String city, String website, int breakEvenYear, int zipCode, int preMoneyEvaluation, int revenueMaxId,
			long financeTypeId, Date closingTime, int revenueMinId, int scope, String uId, int foundingYear,
			String companyName, int raised) {
		super();

		this.accountId = accountId;
		this.investmentPhaseId = investmentPhaseId;
		this.boosts = boosts;
		this.numberOfFte = numberOfFte;
		this.turnover = turnover;
		this.street = street;
		this.city = city;
		this.website = website;
		this.breakEvenYear = breakEvenYear;
		this.zipCode = zipCode;
		this.preMoneyValuation = preMoneyEvaluation;
		this.revenueMaxId = revenueMaxId;
		this.revenueMinId = revenueMinId;
		this.scope = scope;
		this.uId = uId;
		this.foundingYear = foundingYear;
		this.financeTypeId = financeTypeId;
		this.closingTime = closingTime;
		this.companyName = companyName;
		this.raised = raised;
	}

	@Override
	public boolean isInComplete() {
		return super.isInComplete() || investmentPhaseId == -1 || street == "" || city == "" || zipCode == 0
				|| website == "" || breakEvenYear == -1  || financeTypeId == -1
				|| revenueMaxId == 0 || revenueMinId == 0 || investorTypes == null
				|| founders == null || investorTypes.isEmpty() || founders.isEmpty();
		
	}

	@Override
	public boolean isStartup() {
		return true;
	}

	@Override
	public boolean isInvestor() {
		return false;
	}

}
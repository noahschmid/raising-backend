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

	/*
	 * =========================================================================
	 * Stored in the accounttable
	 */
	private long investmentPhaseId = -1;
	private int boosts = 0;
	private int breakEvenYear = -1;
	private int numberOfFte = -1;
	private int preMoneyValuation = -1;
	private Date closingTime;
	private int revenueMaxId = -1;
	private int revenueMinId = -1;
	private int scope = -1;
	private String uId = "";
	private int foundingYear;
	private long financeTypeId = -1;
	private int raised = -1;
	private long videoId = -1;
	/* ========================================================================= */

	/*
	 * =========================================================================
	 * Stored in the respective tables
	 */
	private List<AssignmentTableModel> investorTypes;
	private List<AssignmentTableModel> labels;
	private List<Boardmember> boardmembers;
	private List<Founder> founders;
	private List<PrivateShareholder> privateShareholders;
	private List<CorporateShareholder> corporateShareholders;
	/* ========================================================================= */

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
			List<Founder> founders, List<PrivateShareholder> pShareholders, List<CorporateShareholder> cShareholders,
			List<Boardmember> boardMembers) {
		super(account);
		this.investmentPhaseId = su.getInvestmentPhaseId();
		this.boosts = su.getBoosts();
		this.numberOfFte = su.getNumberOfFte();
		this.breakEvenYear = su.getBreakEvenYear();
		this.preMoneyValuation = su.getPreMoneyValuation();
		this.revenueMaxId = su.getRevenueMaxId();
		this.revenueMinId = su.getRevenueMinId();
		this.scope = su.getScope();
		this.uId = su.getUId();
		this.foundingYear = su.getFoundingYear();
		this.financeTypeId = su.getFinanceTypeId();
		this.closingTime = su.getClosingTime();
		this.raised = su.getRaised();
		this.videoId = su.getVideoId();
		this.investorTypes = invTypes;
		this.labels = labels;
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
	public Startup(long accountId, long investmentPhaseId, int boosts, int numberOfFte, int breakEvenYear,
			int preMoneyEvaluation, int revenueMaxId, long financeTypeId, Date closingTime, int revenueMinId, int scope,
			String uId, int foundingYear, int raised, long videoId) {
		super();

		this.accountId = accountId;
		this.investmentPhaseId = investmentPhaseId;
		this.boosts = boosts;
		this.numberOfFte = numberOfFte;

		this.breakEvenYear = breakEvenYear;

		this.preMoneyValuation = preMoneyEvaluation;
		this.revenueMaxId = revenueMaxId;
		this.revenueMinId = revenueMinId;
		this.scope = scope;
		this.uId = uId;
		this.foundingYear = foundingYear;
		this.financeTypeId = financeTypeId;
		this.closingTime = closingTime;
		this.raised = raised;
		this.videoId = videoId;
	}

	@Override
	public boolean isInComplete() {
		return super.isInComplete() || companyName == "" || foundingYear == -1 || revenueMaxId == -1
				|| revenueMinId == -1 || closingTime == null || scope == -1 || investmentPhaseId == -1 || breakEvenYear == -1 || financeTypeId == -1 || numberOfFte == -1
				|| investorTypes == null || founders == null
				|| investorTypes.isEmpty() || founders.isEmpty();

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
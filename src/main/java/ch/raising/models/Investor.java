package ch.raising.models;

import java.util.List;

import ch.raising.interfaces.IAssignmentTableModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Investor extends Account{
	private long investorTypeId = -1;
	private List<IAssignmentTableModel> invPhases;

	/**
	 * makes the investor represented for the fronted, with all lists initialised
	 * and all data from the account.
	 * 
	 * @param account   a complete account with countries, continents, support,
	 *                  industries initialized
	 * @param inv       represent by the database table investor
	 * @param invPhases
	 */
	public Investor(Account account, Investor inv, List<IAssignmentTableModel> invPhases) {

		super(account);
		this.investorTypeId = inv.getInvestorTypeId();
		this.invPhases = invPhases;

	}

	/**
	 * Builds the investor as found in the investor table in the database. Should be
	 * used by {@link InvestorRepository}.
	 * 
	 * @param accountId
	 * @param name
	 * @param description
	 * @param investorTypeId
	 */

	@Builder(builderMethodName = "investorBuilder")
	public Investor(long accountId, String company, long investorTypeId) {
		this.company = company;
		this.accountId = accountId;
		this.investorTypeId = investorTypeId;
	}
	
	@Override
	public boolean isInComplete() {
		return super.isInComplete() || investorTypeId == -1
				||invPhases == null || invPhases.isEmpty();
	}

}
package ch.raising.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.raising.interfaces.IAssignmentTableModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Investor extends Account{
	
	private long investorTypeId = -1;
	private List<AssignmentTableModel> investmentPhases;

	/**
	 * makes the investor represented for the fronted, with all lists initialised
	 * and all data from the account.
	 * 
	 * @param account   a complete account with countries, continents, support,
	 *                  industries initialized
	 * @param inv       represent by the database table investor
	 * @param investmentPhases
	 */
	public Investor(Account account, Investor inv, List<AssignmentTableModel> invPhases) {

		super(account);
		this.investorTypeId = inv.getInvestorTypeId();
		this.investmentPhases = invPhases;

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
				||investmentPhases == null || investmentPhases.isEmpty();
	}
	
	public boolean isStartup() {
		return false;
	}
	
	
	public boolean isInvestor() {
		return true;
	}

}
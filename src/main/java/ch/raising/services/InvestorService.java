package ch.raising.services;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.AssignmentTableRepository;
import ch.raising.data.InvestorRepository;
import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.ErrorResponse;
import ch.raising.models.Investor;
import ch.raising.utils.MailUtil;
import ch.raising.utils.ResetCodeUtil;

@Service
public class InvestorService extends AccountService {

	private AssignmentTableRepository investmentPhaseRepository;

	@Autowired
	private InvestorRepository investorRepository;

	@Autowired
	public InvestorService(AccountRepository accountRepository, InvestorRepository investorRepository,
			MailUtil mailUtil, ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc) {

		super(accountRepository, mailUtil, resetCodeUtil, jdbc);

		this.investmentPhaseRepository = new AssignmentTableRepository(jdbc, "investmentphase");
		this.investorRepository = investorRepository;
	}

	@Override
	protected long registerAccount(Account requestInvestor) throws Exception {

		Investor invReq = (Investor) requestInvestor;

		checkRequestValid(invReq);

		long accountId = super.registerAccount(invReq);
		Investor inv = Investor.investorBuilder().accountId(accountId).company(invReq.getCompany())
				.investorTypeId(invReq.getInvestorTypeId()).build();

		investorRepository.add(inv);
		invReq.getInvPhases()
				.forEach(phase -> investmentPhaseRepository.addEntryToAccountById(accountId, phase.getId()));

		return accountId;
	}

	@Override
	protected Investor getAccount(long id) {

		Account acc = super.getAccount(id);
		List<IAssignmentTableModel> invPhase = investmentPhaseRepository.findByAccountId(id);
		Investor inv = investorRepository.find(id);

		return new Investor(acc, inv, invPhase);
	}

	/**
	 * Update investor profile
	 * 
	 * @param request the data to update
	 * @return response entity with status code and message
	 */
	public ResponseEntity<?> updateInvestor(int id, Investor inv) {
		return ResponseEntity.status(500).body(new ErrorResponse("not implemented yet"));
	}

	public ResponseEntity<?> addInvestmentPhaseByIvestorId(long id) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investmentPhaseRepository.addEntryToAccountById(accdet.getId(), id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	public ResponseEntity<?> deleteInvestmentPhaseByIvestorId(long id) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investmentPhaseRepository.deleteEntryFromAccountById(accdet.getId(), id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}
}
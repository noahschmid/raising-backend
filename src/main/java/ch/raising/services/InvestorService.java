package ch.raising.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AccountRepository;
import ch.raising.data.ContinentRepository;
import ch.raising.data.CountryRepository;
import ch.raising.data.InvestmentPhaseRepository;
import ch.raising.data.InvestorRepository;
import ch.raising.data.InvestorTypeRepository;
import ch.raising.data.SupportRepository;
import ch.raising.models.Account;
import ch.raising.models.AccountDetails;
import ch.raising.models.ErrorResponse;
import ch.raising.models.InvestmentPhase;
import ch.raising.models.Investor;
import ch.raising.models.InvestorUpdateRequest;
import ch.raising.models.RegisterAccountRequest;
import ch.raising.utils.MailUtil;
import ch.raising.utils.ResetCodeUtil;
import ch.raising.data.IndustryRepository;

@Service
public class InvestorService extends AccountService {
	@Autowired
	private InvestmentPhaseRepository investmentPhaseRepository;

	@Autowired
	private InvestorRepository investorRepository;

	@Autowired
	public InvestorService(AccountRepository accountRepository, InvestmentPhaseRepository investmentPhaseRepository,
			InvestorTypeRepository investorTypeRepository, IndustryRepository industryRepository,
			SupportRepository supportRepository, ContinentRepository continentRepository,
			CountryRepository countryRepository, InvestorRepository investorRepository, MailUtil mailUtil,
			ResetCodeUtil resetCodeUtil, JdbcTemplate jdbc) {

		super(accountRepository, mailUtil, resetCodeUtil, jdbc, countryRepository, continentRepository,
				supportRepository, industryRepository);

		this.investmentPhaseRepository = investmentPhaseRepository;
		this.investorRepository = investorRepository;
	}

	@Override
	protected long registerAccount(Account requestInvestor) throws Exception {
		
		Investor invReq = (Investor) requestInvestor;
		
		checkRequestValid(invReq);
		
		long accountId = super.registerAccount(invReq);
		Investor inv = Investor.investorBuilder().accountId(accountId).description(invReq.getDescription()).investorTypeId(invReq.getInvestorTypeId()).build();
		
		investorRepository.add(inv);
		invReq.getInvPhases().forEach(phase -> investmentPhaseRepository.addInvestmentPhaseToInvestor(accountId, phase.getId()));
		
		return accountId;
	}

	@Override
	protected Investor getAccount(long id) {

		Account acc = super.getAccount(id);
		List<InvestmentPhase> invPhase = investmentPhaseRepository.findByInvestorId(id);
		Investor inv = investorRepository.find(id);

		return new Investor(acc, inv, invPhase);
	}

	/**
	 * Update investor profile
	 * 
	 * @param request the data to update
	 * @return response entity with status code and message
	 */
	public ResponseEntity<?> updateInvestor(int id, InvestorUpdateRequest request) {
		try {
			investorRepository.update(id, request);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}


	private boolean isIncomplete(Investor investor) {
		return investor.getAccountId() == -1 || investor.getDescription() == null || investor.getInvestmentMax() == -1
				|| investor.getInvestmentMin() == -1 || investor.getInvestorTypeId() == -1
				|| investor.getName() == null;
	}

	public ResponseEntity<?> addInvestmentPhaseByIvestorId(long id) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investmentPhaseRepository.addInvestmentPhaseToInvestor(accdet.getId(), id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	public ResponseEntity<?> deleteInvestmentPhaseByIvestorId(long id) {
		try {
			AccountDetails accdet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			investmentPhaseRepository.deleteInvestmentPhaseOfInvestor(accdet.getId(), id);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}
}
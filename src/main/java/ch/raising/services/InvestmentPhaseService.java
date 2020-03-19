package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.InvestmentPhaseRepository;
import ch.raising.data.LabelRepository;
import ch.raising.models.ErrorResponse;

@Service
public class InvestmentPhaseService {
	@Autowired
	private InvestmentPhaseRepository investmentPhaseRepository;
	
	@Autowired
	public InvestmentPhaseService(InvestmentPhaseRepository investmentPhaseRepository) {
		this.investmentPhaseRepository = investmentPhaseRepository;
	}

	public ResponseEntity<?> getAllinvestmentPhases() {
		try {
			return ResponseEntity.ok().body(investmentPhaseRepository.getAll());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

}

package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.InvestorTypeRepository;
import ch.raising.data.LabelRepository;
import ch.raising.models.ErrorResponse;

@Service
public class InvestorTypeService {
	@Autowired
	private InvestorTypeRepository investorTypeRepository;
	
	@Autowired
	public InvestorTypeService(InvestorTypeRepository investorTypeRepository) {
		this.investorTypeRepository = investorTypeRepository;
	}

	public ResponseEntity<?> getAllInvestorTypes() {
		try {
			return ResponseEntity.ok().body(investorTypeRepository.getAllInvestorTypes());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	
}

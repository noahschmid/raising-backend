package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.FinanceRepository;
import ch.raising.models.ErrorResponse;

@Service
public class FinanceService {
	@Autowired
	private FinanceRepository FinanceRepository;
	
	@Autowired
	public FinanceService(FinanceRepository FinanceRepository) {
		this.FinanceRepository = FinanceRepository;
	}

	public ResponseEntity<?> getAllFinances() {
		try {
			return ResponseEntity.ok().body(FinanceRepository.getAll());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	
}

package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.RevenueRepository;
import ch.raising.models.ErrorResponse;

@Service
public class RevenueService {
	@Autowired
	private RevenueRepository RevenueRepository;
	
	@Autowired
	public RevenueService(RevenueRepository RevenueRepository) {
		this.RevenueRepository = RevenueRepository;
	}

	public ResponseEntity<?> getAllRevenues() {
		try {
			return ResponseEntity.ok().body(RevenueRepository.getAll());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	
}

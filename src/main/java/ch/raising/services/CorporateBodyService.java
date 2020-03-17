package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.CorporateBodyRepository;
import ch.raising.models.ErrorResponse;

@Service
public class CorporateBodyService {
	@Autowired
	private CorporateBodyRepository CorporateBodyRepository;
	
	@Autowired
	public CorporateBodyService(CorporateBodyRepository CorporateBodyRepository) {
		this.CorporateBodyRepository = CorporateBodyRepository;
	}

	public ResponseEntity<?> getAllCorporateBodies() {
		try {
			return ResponseEntity.ok().body(CorporateBodyRepository.getAll());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	
}

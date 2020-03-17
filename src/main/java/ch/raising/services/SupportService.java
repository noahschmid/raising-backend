package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.SupportRepository;
import ch.raising.models.ErrorResponse;

@Service
public class SupportService {
	@Autowired
	private SupportRepository supportRepository;
	
	@Autowired
	public SupportService(SupportRepository supportRepository) {
		this.supportRepository = supportRepository;
	}

	public ResponseEntity<?> getAllSupports() {
		try {
			return ResponseEntity.ok().body(supportRepository.getAllSupports());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	
}

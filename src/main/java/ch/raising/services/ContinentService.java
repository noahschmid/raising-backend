package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.ContinentRepository;
import ch.raising.data.LabelRepository;
import ch.raising.models.ErrorResponse;

@Service
public class ContinentService {
	@Autowired
	private ContinentRepository continentRepository;
	
	@Autowired
	public ContinentService(ContinentRepository continentRepository) {
		this.continentRepository = continentRepository;
	}

	public ResponseEntity<?> getAllContinents() {
		try {
			return ResponseEntity.ok().body(continentRepository.getAllContinents());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	
}

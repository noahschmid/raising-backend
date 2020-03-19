package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.IndustryRepository;
import ch.raising.data.LabelRepository;
import ch.raising.models.ErrorResponse;

@Service
public class IndustryService {
	@Autowired
	private IndustryRepository industryRepository;
	
	@Autowired
	public IndustryService(IndustryRepository industryRepository) {
		this.industryRepository = industryRepository;
	}

	public ResponseEntity<?> getAllIndustries() {
		try {
			return ResponseEntity.ok().body(industryRepository.getAll());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	
}

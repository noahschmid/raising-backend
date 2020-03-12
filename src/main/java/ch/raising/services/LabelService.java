package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.LabelRepository;
import ch.raising.models.ErrorResponse;

@Service
public class LabelService {
	@Autowired
	private LabelRepository labelRepository;
	
	@Autowired
	public LabelService(LabelRepository labelRepository) {
		this.labelRepository = labelRepository;
	}

	public ResponseEntity<?> getAllLabels() {
		try {
			return ResponseEntity.ok().body(labelRepository.getAllLabels());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	
}

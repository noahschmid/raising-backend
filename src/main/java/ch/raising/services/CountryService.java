package ch.raising.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ch.raising.data.CountryRepository;
import ch.raising.data.LabelRepository;
import ch.raising.models.ErrorResponse;

@Service
public class CountryService {
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	public CountryService(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}

	public ResponseEntity<?> getAllCountries() {
		try {
			return ResponseEntity.ok().body(countryRepository.getAll());
		}catch(Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}
	
	
}

package ch.raising.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import ch.raising.data.AssignmentTableRepository;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.ErrorResponse;
import ch.raising.utils.MapUtil;

@Service
public class PublicInformationService {

	private JdbcTemplate jdbc;

	@Autowired
	public PublicInformationService(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public ResponseEntity<?> getAll(String name) {
		try {
			List<AssignmentTableModel> info = new AssignmentTableRepository(jdbc, name).findAll();
			return ResponseEntity.ok().body(info);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (Error e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> getAllWithDescription(String name) {
		try {
			List<AssignmentTableModel> info = new AssignmentTableRepository(jdbc, name,
					MapUtil::mapRowToAssignmentTableWithDescription).findAll();
			return ResponseEntity.ok().body(info);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (Error e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> getAllCountries(String name) {
		try {
			List<AssignmentTableModel> info = new AssignmentTableRepository(jdbc, name, MapUtil::mapRowToCountry)
					.findAll();
			return ResponseEntity.ok().body(info);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (Error e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> getAllRevenueSteps(String name) {
		try {
			List<AssignmentTableModel> info = new AssignmentTableRepository(jdbc, name, MapUtil::mapRowToRevenue)
					.findAll();
			return ResponseEntity.ok().body(info);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (Error e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

}

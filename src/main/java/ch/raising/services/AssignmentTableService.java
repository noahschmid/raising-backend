package ch.raising.services;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AssignmentTableRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.Country;
import ch.raising.models.ErrorResponse;
import ch.raising.utils.MapUtil;

@Service
public class AssignmentTableService {

	private JdbcTemplate jdbc;

	@Autowired
	public AssignmentTableService(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public ResponseEntity<?> getAll(String name) {
		try {
			List<AssignmentTableModel> info = AssignmentTableRepository.getInstance(jdbc).withTableName(name).findAll();
			return ResponseEntity.ok().body(info);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (Error e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> getAllWithDescription(String name) {
		try {
			List<AssignmentTableModel> info = AssignmentTableRepository.getInstance(jdbc).withTableName(name)
					.withRowMapper(MapUtil::mapRowToAssignmentTableWithDescription).findAll();
			return ResponseEntity.ok().body(info);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (Error e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> getAllCountries() {
		try {
			List<AssignmentTableModel> info = AssignmentTableRepository.getInstance(jdbc).withTableName("country")
					.withRowMapper(MapUtil::mapRowToCountry).findAll();

			return ResponseEntity.ok().body(info);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (Error e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	public ResponseEntity<?> getAllRevenueSteps() {
		try {
			List<AssignmentTableModel> info = AssignmentTableRepository.getInstance(jdbc).withTableName("revenue")
					.withRowMapper(MapUtil::mapRowToRevenue).findAll();

			return ResponseEntity.ok().body(info);
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		} catch (Error e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Adds the ids into the table and handles all responses
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	private ResponseEntity<?> addById(AssignmentTableRepository assignmentRepo, List<AssignmentTableModel> models) {
		try {
			Collection<? extends GrantedAuthority> p = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
			assert !p.isEmpty() && p.size() == 1;
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			for (AssignmentTableModel m : models) {
				assignmentRepo.addEntryToAccountById(m.getId(), accDet.getId());
			}
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Add entry in assignmenttable with both ids to account
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addToAccountById(String tableName, List<AssignmentTableModel> models) {
		return addById(AssignmentTableRepository.getInstance(jdbc).withTableName(tableName), models);
	}

	/**
	 * Add entry in assignmenttable with both ids to investor
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addToInvestorById(String tableName, List<AssignmentTableModel> models) {
		return addById(
				AssignmentTableRepository.getInstance(jdbc).withTableName(tableName).withAccountIdName("investorid"),
				models);
	}

	/**
	 * Add entry in assignmenttable with both ids to startup
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> addToStartupById(String tableName, List<AssignmentTableModel> models) {
		return addById(
				AssignmentTableRepository.getInstance(jdbc).withTableName(tableName).withAccountIdName("startupid"),
				models);
	}

	/**
	 * remove entry in assignmenttable specified by those ids
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	private ResponseEntity<?> deleteById(AssignmentTableRepository assignmentRepo, List<AssignmentTableModel> models) {
		try {
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			for (AssignmentTableModel model : models) {
				assignmentRepo.deleteEntryFromAccountById(model.getId(), accDet.getId());
			}

			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));
		}
	}

	/**
	 * remove entry in assignmenttable specified by those ids and the name from
	 * account
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteFromAccountById(String name, List<AssignmentTableModel> models) {
		return deleteById(AssignmentTableRepository.getInstance(jdbc).withTableName(name), models);
	}

	/**
	 * remove entry in assignmenttable specified by those ids and the name from
	 * investor
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteFromInvestorById(String name, List<AssignmentTableModel> models) {
		return deleteById(
				AssignmentTableRepository.getInstance(jdbc).withTableName(name).withAccountIdName("investorid"),
				models);
	}

	/**
	 * remove entry in assignmenttable specified by those ids and the name from
	 * startup
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 */
	public ResponseEntity<?> deleteFromStartupById(String name, List<AssignmentTableModel> models) {
		return deleteById(
				AssignmentTableRepository.getInstance(jdbc).withTableName(name).withAccountIdName("startupid"), models);
	}

}

package ch.raising.services;

import java.sql.SQLException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AssignmentTableRepository;
import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.AccountDetails;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.responses.CompletePublicInformation;
import ch.raising.utils.MapUtil;

@Service
public class AssignmentTableService {

	private JdbcTemplate jdbc;

	@Autowired
	public AssignmentTableService(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<IAssignmentTableModel> getAll(String name) throws DataAccessException, SQLException {
		return AssignmentTableRepository.getInstance(jdbc).withTableName(name).findAll();
	}

	public CompletePublicInformation getAllTables() throws DataAccessException, SQLException {
		CompletePublicInformation pr = new CompletePublicInformation();

		pr.setTicketSizes(getAll("ticketsize"));
		pr.setContinents(getAll("continent"));
		pr.setCountries(getAllCountries());
		pr.setIndustries(getAll("industry"));
		pr.setInvestmentPhases(getAll("investmentphase"));
		pr.setLabels(getAllWithDescription("label"));
		pr.setInvestorTypes(getAllWithDescription("investortype"));
		pr.setSupport(getAll("support"));
		pr.setCorporateBodies(getAll("corporateBody"));
		pr.setFinanceTypes(getAll("financeType"));
		pr.setRevenues(getAllRevenueSteps());

		return pr;
	}

	public List<IAssignmentTableModel> getAllWithDescription(String name) throws DataAccessException, SQLException {
		return AssignmentTableRepository.getInstance(jdbc).withTableName(name)
				.withRowMapper(MapUtil::mapRowToAssignmentTableWithDescription).findAll();
	}

	public List<IAssignmentTableModel> getAllCountries() throws DataAccessException, SQLException {
		return AssignmentTableRepository.getInstance(jdbc).withTableName("country")
				.withRowMapper(MapUtil::mapRowToCountry).findAll();
	}

	public List<IAssignmentTableModel> getAllRevenueSteps() throws DataAccessException, SQLException {
		return AssignmentTableRepository.getInstance(jdbc).withTableName("revenue")
				.withRowMapper(MapUtil::mapRowToRevenue).findAll();
	}

	/**
	 * Adds the ids into the table and handles all responses
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private void addById(AssignmentTableRepository assignmentRepo, List<Long> models)
			throws DataAccessException, SQLException {
		AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		for (Long m : models) {
			assignmentRepo.addEntryToAccountById(m, accDet.getId());
		}
	}

	/**
	 * Add entry in assignmenttable with both ids to account
	 * 
	 * @param countryId
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addToAccountById(String tableName, List<Long> model)
			throws DataAccessException, SQLException {
		addById(AssignmentTableRepository.getInstance(jdbc).withTableName(tableName), model);
	}

	/**
	 * Add entry in assignmenttable with both ids to investor
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addToInvestorById(String tableName, List<Long> models)
			throws DataAccessException, SQLException {
		addById(AssignmentTableRepository.getInstance(jdbc).withTableName(tableName).withAccountIdName("investorid"),
				models);
	}

	/**
	 * Add entry in assignmenttable with both ids to startup
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addToStartupById(String tableName, List<Long> models)
			throws DataAccessException, SQLException {
		addById(AssignmentTableRepository.getInstance(jdbc).withTableName(tableName).withAccountIdName("startupid"),
				models);
	}

	/**
	 * remove entry in assignmenttable specified by those ids
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	private void deleteById(AssignmentTableRepository assignmentRepo, List<Long> models) throws DataAccessException, SQLException {
		
			AccountDetails accDet = (AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			for (Long model : models) {
				assignmentRepo.deleteEntryFromAccountById(model, accDet.getId());
			}
	}

	/**
	 * remove entry in assignmenttable specified by those ids and the name from
	 * account
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public void deleteFromAccountById(String name, List<Long> countries) throws DataAccessException, SQLException {
		deleteById(AssignmentTableRepository.getInstance(jdbc).withTableName(name), countries);
	}

	/**
	 * remove entry in assignmenttable specified by those ids and the name from
	 * investor
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public void deleteFromInvestorById(String name, List<Long> models) throws DataAccessException, SQLException {
		deleteById(
				AssignmentTableRepository.getInstance(jdbc).withTableName(name).withAccountIdName("investorid"),
				models);
	}

	/**
	 * remove entry in assignmenttable specified by those ids and the name from
	 * startup
	 * 
	 * @param id
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	public void deleteFromStartupById(String name, List<Long> models) throws DataAccessException, SQLException {
		deleteById(
				AssignmentTableRepository.getInstance(jdbc).withTableName(name).withAccountIdName("startupid"), models);
	}
	
	public void updateAssignmentTable(String name, List<Long> models) throws DataAccessException, SQLException{
		long accountId = ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
		AssignmentTableRepository ar = AssignmentTableRepository.getInstance(jdbc).withTableName(name);
		ar.deleteEntriesByAccountId(accountId);
		ar.addEntriesToAccount(accountId, models);
	}
	
	
	
}

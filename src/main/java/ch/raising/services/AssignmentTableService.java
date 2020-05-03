package ch.raising.services;

import java.sql.SQLException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.AssignmentTableRepository;
import ch.raising.data.AssignmentTableRepositoryFactory;
import ch.raising.data.IconRepository;
import ch.raising.interfaces.IAssignmentTableModel;
import ch.raising.models.AccountDetails;
import ch.raising.models.AssignmentTableModel;
import ch.raising.models.AssignmentTableWithDescritionAndIcon;
import ch.raising.models.responses.CompletePublicInformation;
import ch.raising.utils.MapUtil;

@Service
public class AssignmentTableService {

	private final AssignmentTableRepositoryFactory atrFactory;
	private final IconService iconService;

	@Autowired
	public AssignmentTableService(AssignmentTableRepositoryFactory atrFactory, IconService iconService) {
		this.atrFactory = atrFactory;
		this.iconService = iconService;
	}

	public List<IAssignmentTableModel> getAll(String name) throws DataAccessException, SQLException {
		return atrFactory.getRepository(name).findAll();
	}
	
	public List<IAssignmentTableModel> getAllWithIcon(String name) throws DataAccessException, SQLException{
		return atrFactory.getRepository(name).findAllWithIcon();
	}

	@Cacheable("completePublicInformation")
	public CompletePublicInformation getAllTables() throws DataAccessException, SQLException {
		CompletePublicInformation pr = CompletePublicInformation.builder()
				.ticketSizes(getAll("ticketsize"))
				.continents(getAll("continent"))
				.countries(getAll("country"))
				.industries(getAllWithIcon("industry"))
				.investmentPhases(getAllWithIcon("investmentphase"))
				.labels(getAllWithIcon("label"))
				.investorTypes(getAllWithIcon("investortype"))
				.support(getAllWithIcon("support"))
				.corporateBodies(getAll("corporateBody"))
				.financeTypes(getAll("financeType"))
				.revenues(getAll("revenue")).build();
		return pr;
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
	public void addToAccountById(String tableName, List<Long> model) throws DataAccessException, SQLException {
		addById(atrFactory.getRepository(tableName), model);
	}

	/**
	 * Add entry in assignmenttable with both ids to investor
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addToInvestorById(String tableName, List<Long> models) throws DataAccessException, SQLException {
		addById(atrFactory.getRepository(tableName), models);
	}

	/**
	 * Add entry in assignmenttable with both ids to startup
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addToStartupById(String tableName, List<Long> models) throws DataAccessException, SQLException {
		addById(atrFactory.getRepositoryForStartup(tableName), models);
	}

	/**
	 * remove entry in assignmenttable specified by those ids
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private void deleteById(AssignmentTableRepository assignmentRepo, List<Long> models)
			throws DataAccessException, SQLException {

		long accDet = getAccountId();
		for (Long model : models) {
			assignmentRepo.deleteEntryFromAccountById(model, accDet);
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
		deleteById(atrFactory.getRepository(name), countries);
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
		deleteById(atrFactory.getRepositoryForInvestor(name), models);
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
		deleteById(atrFactory.getRepositoryForStartup(name), models);
	}

	public void updateAssignmentTable(String name, List<Long> models) throws DataAccessException, SQLException {
		long accountId = getAccountId();
		AssignmentTableRepository ar = atrFactory.getRepository(name);
		ar.deleteEntriesByAccountId(accountId);
		ar.addEntriesToAccount(accountId, models);
	}

	public void updateStartupAssignmentTable(String name, List<Long> models) throws DataAccessException, SQLException {
		long accountId = getAccountId();
		AssignmentTableRepository ar = atrFactory.getRepositoryForStartup(name);
		ar.deleteEntriesByAccountId(accountId);
		ar.addEntriesToAccount(accountId, models);
	}

	public void updateInvestorAssignmentTable(String name, List<Long> models) throws DataAccessException, SQLException {
		long accountId = getAccountId();
		AssignmentTableRepository ar = atrFactory.getRepositoryForInvestor(name);
		ar.deleteEntriesByAccountId(accountId);
		ar.addEntriesToAccount(accountId, models);
	}

	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}

}

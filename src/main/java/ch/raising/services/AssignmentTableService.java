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

	@Autowired
	public AssignmentTableService(AssignmentTableRepositoryFactory atrFactory) {
		this.atrFactory = atrFactory;
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
				.revenues(getAll("revenue"))
				.boardmemberTypes(getAll("boardmemberType")).build();
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
	 * Adds the ids into the table and handles all responses
	 * 
	 * @param id id of account
	 * @param assignmentRepo the repository of the models you want to assign
	 * @param models lit of models you want to assign
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private void addById(long id, AssignmentTableRepository assignmentRepo, List<Long> models)
			throws DataAccessException, SQLException {
		for (Long m : models) {
			assignmentRepo.addEntryToAccountById(m, id);
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
	 * Add entry in assignmenttable with both ids to account
	 * 
	 * @param countryId
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addToAccountById(long accountId, String tableName, List<Long> model) throws DataAccessException, SQLException {
		addById(accountId, atrFactory.getRepository(tableName), model);
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
	 * Add entry in assignmenttable with both ids to investor
	 * 
	 * @param accountId id of account to update
	 * @param tableName name of the table to update
	 * @param models list of models you want to assign 
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addToInvestorById(long accountId, String tableName, List<Long> models) throws DataAccessException, SQLException {
		addById(accountId, atrFactory.getRepository(tableName), models);
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
	 * Add entry in assignmenttable with both ids to startup
	 * 
	 * @param accountId id of account to update
	 * @param tableName name of the table to update
	 * @param models list of models you want to assign 
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addToStartupById(long accountId, String tableName, List<Long> models) throws DataAccessException, SQLException {
		addById(accountId, atrFactory.getRepositoryForStartup(tableName), models);
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
	 * remove entry in assignmenttable specified by those ids
	 * 
	 * @param id account id
	 * @param assignmentRepo repository of assignment table
	 * @param models list of models you want to delete from assignment
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private void deleteById(long id, AssignmentTableRepository assignmentRepo, List<Long> models)
			throws DataAccessException, SQLException {
		for (Long model : models) {
			assignmentRepo.deleteEntryFromAccountById(model, id);
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
	 * account given by accountId
	 * 
	 * @param countryId
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void deleteFromAccountById(long accountId, String name, List<Long> countries) throws DataAccessException, SQLException {
		deleteById(accountId, atrFactory.getRepository(name), countries);
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
	 * investor
	 * 
	 * @param id account id
	 * @param name name of table
	 * @param models list of models you want to delete from assignment
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void deleteFromInvestorById(long id, String name, List<Long> models) throws DataAccessException, SQLException {
		deleteById(id, atrFactory.getRepositoryForInvestor(name), models);
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

	/**
	 * remove entry in assignmenttable specified by those ids and the name from
	 * startup
	 * 
	 * @param id
	 * @return Responsenetitiy with a statuscode and an optional body
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void deleteFromStartupById(long accountId, String name, List<Long> models) throws DataAccessException, SQLException {
		deleteById(accountId, atrFactory.getRepositoryForStartup(name), models);
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

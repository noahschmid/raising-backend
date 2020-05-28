package ch.raising.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IAssignmentTableRepository{
	/**
	 * find the entry by id
	 * @param id
	 * @return {@link IAssignmentTableModel}
	 */
	public IAssignmentTableModel find(long id);
	/**
	 * 
	 * @return a list of all {@link IAssignmentTableModels}
	 */
    public List<IAssignmentTableModel> findAll();
    /**
     * 
     * @param id of the account that has any values referenced through an assignment table
     * @return a list of all {@link IAssignmentTableModels} that belong to the account
     */
	public List<IAssignmentTableModel> findByAccountId(long id);
	/**
	 * 
	 * @param id of the value
	 * @param accid the account
	 */
	public void addEntryToAccountById(long id, long accid);
	/**
	 * 
	 * @param id to be added
	 * @param accid to be added
	 */
	public void deleteEntryFromAccountById(long id, long accid);
	/**
	 * maps the resultset to an {@link IAssignmentTableModel}
	 * @param rs
	 * @param row
	 * @return {@link IAssignmentTableModel}
	 * @throws SQLException
	 */
	public IAssignmentTableModel mapRowToModel(ResultSet rs, int row) throws SQLException;
}

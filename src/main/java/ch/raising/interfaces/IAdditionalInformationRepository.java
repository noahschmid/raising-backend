package ch.raising.interfaces;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;

import ch.raising.data.BoardmemberRepository;
import ch.raising.data.CorporateShareholderRepository;
import ch.raising.data.FounderRepository;
import ch.raising.data.PrivateShareholderRepository;

/**
 * An additionalInformationRepository represents a table that holds additional information for the startup table. 
 * @see BoardmemberRepository
 * @see CorporateShareholderRepository
 * @see FounderRepository
 * @see PrivateShareholderRepository
 * @author manus
 *
 */
public interface IAdditionalInformationRepository<Model> extends IRepository<Model>{
	/**
	 * 
	 * @param id of the memeber
	 * @return the id of the startup he belongs to
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public long getStartupIdByMemberId(long id)throws SQLException, DataAccessException;
	/**
	 * 
	 * @param sumem startup member to be added
	 * @param startupId
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addMemberByStartupId(Model sumem, long startupId)throws SQLException, DataAccessException;
	/**
	 * 
	 * @param id of the member to be deleted
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void deleteMemberById(long id) throws SQLException, DataAccessException;
	/**
	 * 
	 * @param startupId 
	 * @return a list of startup members that belong to that startup
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public List<Model> findByStartupId(long startupId)throws SQLException, DataAccessException;
	/**
	 * Maps the results in the resultset to a model
	 */
	public Model mapRowToModel(ResultSet rs, int row) throws SQLException;
	/**
	 * adds several models to a startup
	 * @param models
	 * @param startupId
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void addMemberListByStartupId(List<Model> models, long startupId)throws SQLException, DataAccessException;
	
	public PreparedStatementCallback<Boolean> getCallback(List<Model> model, long startupId);
	/**
	 * extracts the startupid of a row
	 * @param rs
	 * @param row
	 * @return
	 * @throws SQLException
	 */
	public default long mapRowToId(ResultSet rs, int row) throws SQLException {
		return rs.getInt("startupId");
	}
	
	public default PreparedStatementCallback<Boolean> deleteById(long id){
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, id);
				return ps.execute();
			}
		};
	}
}

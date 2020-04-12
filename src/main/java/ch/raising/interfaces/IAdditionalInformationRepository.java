package ch.raising.interfaces;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;

/**
 * An additionalInformationRepository represents a table that holds additional information for the startup table. 
 * @author Manuel Schüpbach
 * @version 1.0
 *
 */
public interface IAdditionalInformationRepository<Model> extends IRepository<Model>{
	
	public long getStartupIdByMemberId(long id)throws SQLException, DataAccessException;
	
	public void addMemberByStartupId(Model sumem, long startupId)throws SQLException, DataAccessException;
	
	public void deleteMemberById(long id) throws SQLException, DataAccessException;
	
	public List<Model> findByStartupId(long startupId)throws SQLException, DataAccessException;
	
	public Model mapRowToModel(ResultSet rs, int row) throws SQLException;
	
	public void addMemberListByStartupId(List<Model> models, long startupId)throws SQLException, DataAccessException;
	
	public PreparedStatementCallback<Boolean> getCallback(List<Model> model, long startupId);
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

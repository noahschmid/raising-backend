package ch.raising.interfaces;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;

import ch.raising.models.Boardmember;
import ch.raising.models.Contact;

/**
 * An additionalInformationRepository represents a table that holds additional information for the startup table. 
 * @author Manuel Schüpbach
 * @version 1.0
 *
 */
public interface IAdditionalInformationRepository<Model>{
	
	public long getStartupIdByMemberId(long id);
	
	public void addMemberByStartupId(Model sumem, long startupId);
	
	public void deleteMemberByStartupId(long id);
	
	public PreparedStatementCallback<Boolean> addByStartupId(Model model, long startupId);
	
	public List<Model> findByStartupId(long startupId);
	
	public default long mapRowToId(ResultSet rs, int row) throws SQLException {
		return rs.getInt("startupId");
	}
	
	public Model mapRowToModel(ResultSet rs, int row) throws SQLException;
	
	public Model find(long id);
	
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

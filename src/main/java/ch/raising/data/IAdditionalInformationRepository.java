package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCallback;

import ch.raising.models.Boardmember;
import ch.raising.models.Contact;

/**
 * An additionalInformationRepository represents a table that holds additional information for the startup table. 
 * @author Manuel Schüpbach
 * @version 1.0
 *
 */
public interface IAdditionalInformationRepository<Model, UpdateQueryBuilder> extends IRepository<Model, UpdateQueryBuilder> {
	
	public long getStartupIdByMemberId(long id);
	public void addMemberByStartupId(Model sumem, long startupId);
	public void addMemberByStartupId(Model sumem);
	public void deleteMemberByStartupId(long id);
	public PreparedStatementCallback<Boolean> deleteById(long id);
	public PreparedStatementCallback<Boolean> addByStartupId(Model model, long startupId);
	PreparedStatementCallback<Boolean> addByMember(Model bmem);
	public Object findByStartupId(long startupId);
	
	public default int mapRowToId(ResultSet rs, int row) throws SQLException {
		return rs.getInt("startupId");
	}
}

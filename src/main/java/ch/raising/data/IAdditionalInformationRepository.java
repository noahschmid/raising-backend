package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An additionalInformationRepository represents a table that holds additional information for the startup table. 
 * @author Manuel Schüpbach
 *
 */
public interface IAdditionalInformationRepository<Model, UpdateQueryBuilder> extends IRepository<Model, UpdateQueryBuilder> {
	
	public int getStartupIdOfTableById(int id);
	public void addEntry(Model sumem);
	public void deleteEntry(int id);
	
	public default int mapRowToId(ResultSet rs, int row) throws SQLException {
		return rs.getInt("startupId");
	}
}

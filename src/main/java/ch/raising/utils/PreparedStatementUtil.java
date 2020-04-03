package ch.raising.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;

import ch.raising.models.Media;
/**
 * Is used to store Prepared statements to be used in the {@see ch.raising.data} package.
 * @author Manuel Schüpbach
 *
 */
public class PreparedStatementUtil {
	
	/**
	 * Callback for: INSERT INTO tableName(media) VALUES (?);
	 * @param mediaBytes
	 * @return the resulting {@link org.springframework.jdbc.core.PreparedStatementCallback<Boolean>}
	 */
	public static PreparedStatementCallback<Boolean> addBytesCallback(byte[] mediaBytes){
		return new PreparedStatementCallback<Boolean>() {
			 @Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setBytes(c++, mediaBytes);
				return ps.execute();
			}
		};
	}

	/**
	 * Callback for: INSERT INTO tableName(media, accountid) VALUES (?,?);
	 * @param media
	 * @param id
	 * @return  the resulting {@link org.springframework.jdbc.core.PreparedStatementCallback<Boolean>}
	 */
	public static PreparedStatementCallback<Boolean> addMediaByIdCallback(Media media, long id){
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException {
				int c = 1;
				ps.setLong(c++, id);
				ps.setBytes(c++, media.getMedia());
				return ps.execute();
			}
		};
	}
	
	/**
	 *  Callback for: DELETE FROM tablename where id = ? and accountid = ?;
	 * @param imageId
	 * @param accountId
	 * @return the resulting {@link org.springframework.jdbc.core.PreparedStatementCallback<Boolean>}
	 */
	public static PreparedStatementCallback<Boolean> deleteMediaByIdAndAccountIdCallback(long imageId, long accountId){
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException {
				int c =1;
				ps.setLong(c++, imageId);
				ps.setLong(c++, accountId);
				return ps.execute();
			}
		};
	}
}

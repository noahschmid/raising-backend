package ch.raising.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;

import ch.raising.models.Image;

public class PreparedStatementUtil {

	public static PreparedStatementCallback<Boolean> addImageByIdCallback(Image img, long id){
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException {
				int c =1;
				byte[] image = img.getImage().getBytes();
				assert image != null;
				ps.setLong(c++, id);
				ps.setBytes(c++, img.getImage().getBytes());
				return ps.execute();
			}
		};
	}
	
	/**
	 * 
	 * @param imageId
	 * @param accountId
	 * @return the callback for DELETE FROM tablename where id = ? and accountid = ?;
	 */
	public static PreparedStatementCallback<Boolean> deleteByIdAndAccountIdCallback(long imageId, long accountId){
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

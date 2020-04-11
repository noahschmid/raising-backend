package ch.raising.interfaces;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import ch.raising.models.Media;

public interface IMediaRepository<Model> {

	void addMediaToAccount(byte[] img, long accountId) throws DataAccessException, SQLException;
	
	long addMedia(byte[] media) throws DataAccessException, SQLException;
	
	Model findMediaById(long mediaId) throws DataAccessException, SQLException;
	
	List<Long> findMediaIdByAccountId(long accountId) throws DataAccessException, SQLException;

	List<Model> findMediaByAccountId(long accountId) throws SQLException, DataAccessException;

	void deleteMediaFromAccount(long imageId, long accountId) throws DataAccessException, SQLException;

	long getMediaIdOf(long accountId);

	void addAccountIdToMedia(long videoId, long accountId) throws DataAccessException;

}
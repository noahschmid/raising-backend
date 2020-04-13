package ch.raising.interfaces;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import ch.raising.models.Media;
import ch.raising.utils.DatabaseOperationException;

public interface IMediaRepository<Model> {
	
	long addMedia(Media media) throws DataAccessException, SQLException, DatabaseOperationException;
	
	Media findMediaById(long mediaId) throws DataAccessException, SQLException;
	
	List<Long> findMediaIdByAccountId(long accountId) throws DataAccessException, SQLException;

	List<Media> findMediaByAccountId(long accountId) throws SQLException, DataAccessException;

	void deleteMediaFromAccount(long imageId, long accountId) throws DataAccessException, SQLException;

	long getMediaIdOf(long accountId);

	void addAccountIdToMedia(long videoId, long accountId) throws DataAccessException;

	long countMediaOfAccount(long accountId) throws SQLException, DataAccessException;

	void updateMedia(Media media) throws SQLException, DataAccessException;

}
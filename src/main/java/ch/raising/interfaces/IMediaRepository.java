package ch.raising.interfaces;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;

import ch.raising.models.Media;

public interface IMediaRepository<Model> {

	void addMediaToAccount(Model model, long accountId) throws DataAccessException, SQLException;
	
	long addMediaBytes(byte[] media) throws DataAccessException, SQLException;
	
	Model findMediaById(long mediaId) throws DataAccessException, SQLException;

	List<Model> findMediaByAccount(long id);

	void deleteMediaFromAccount(long imageId, long accountId) throws DataAccessException, SQLException;

	long getMediaIdOf(long accountId);

	void addAccountIdToMedia(long accountId, long videoId) throws DataAccessException;

}
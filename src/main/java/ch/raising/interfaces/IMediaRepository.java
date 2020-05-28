package ch.raising.interfaces;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.web.multipart.MultipartFile;

import ch.raising.models.Media;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MediaException;

/**
 * repository is responsible for storing and retrieving data in a database
 * 
 * @author manus
 *
 */
public interface IMediaRepository {
	/**
	 * 
	 * @param media {@link Media} to be added
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
	long addMedia(Media media) throws DataAccessException, SQLException, DatabaseOperationException;

	/**
	 * 
	 * @param mediaId the d of the media to be retrieved
	 * @return {@link Media} to be retrieved
	 * @throws DatabaseOperationException
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	Media findMediaById(long mediaId) throws DatabaseOperationException, DataAccessException, SQLException;

	/**
	 * 
	 * @param accountId
	 * @return List of Integers representing all the media from that account
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	List<Long> findMediaIdByAccountId(long accountId) throws DataAccessException, SQLException;

	/**
	 * 
	 * @param accountId
	 * @return List
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws DatabaseOperationException
	 */
	List<Media> findMediaByAccountId(long accountId)
			throws SQLException, DataAccessException, DatabaseOperationException;

	/**
	 * 
	 * @param imageId   that should be deleted
	 * @param accountId it should be delete from
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	void deleteMediaFromAccount(long imageId, long accountId) throws DataAccessException, SQLException;

	/**
	 * 
	 * @param mediaId to be deleted
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	void deleteMedia(long mediaId) throws DataAccessException, SQLException;

	/**
	 * 
	 * @param accountId
	 * @return the id of the media of this account
	 */
	long getMediaIdOf(long accountId) throws DataAccessException, SQLException;

	/**
	 * assigns a media entry to an account
	 * 
	 * @param mediaId
	 * @param accountId
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws MediaException
	 */
	void addAccountIdToMedia(long mediaId, long accountId) throws DataAccessException, SQLException, MediaException;

	/**
	 * find the number of media objects of the table of that account
	 * 
	 * @param accountId
	 * @return the number of entries belonging to that account
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	long countMediaOfAccount(long accountId) throws SQLException, DataAccessException;

	/**
	 * 
	 * @param media {@link Media} to be updated fields not to be updated are left in their initial value
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws MediaException
	 */
	void updateMedia(Media media) throws SQLException, DataAccessException, MediaException;
	/**
	 * checks if media is already assigned to an account
	 * @param mediaId of the media
	 * @param accountId
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	boolean mediaIsFree(long mediaId, long accountId) throws SQLException, DataAccessException;

}
package ch.raising.data;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;

import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.Media;

public class VideoRepository implements IMediaRepository<Media> {

	@Override
	public void addMediaToAccount(Media img, long accountId) throws DataAccessException, SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Media> findMediaByAccount(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteMediaFromAccount(long imageId, long accountId) throws DataAccessException, SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getMediaIdOf(long accountId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long addMediaBytes(byte[] media) throws DataAccessException, SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Media findMediaById(long mediaId) throws DataAccessException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}

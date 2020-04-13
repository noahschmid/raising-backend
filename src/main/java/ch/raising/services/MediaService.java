package ch.raising.services;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.raising.data.MediaRepository;
import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Media;
import ch.raising.utils.DatabaseOperationException;

@Service
public class MediaService{

	
	IMediaRepository<Media> mediaRepo;
	
	public MediaService() {}
	
	public MediaService(JdbcTemplate jdbc, String name) {
		this.mediaRepo = new MediaRepository(jdbc, name);
	}
	
	public long uploadMediaAndReturnId(Media video) throws DataAccessException, SQLException, DatabaseOperationException {
		return mediaRepo.addMedia(video);
	}
	
	public void uploadMediaToAccount(Media media) throws DataAccessException, SQLException, DatabaseOperationException {
		mediaRepo.addMediaToAccount(media, getAccountId());
	}
	
	public Media getMedia(long id) throws DataAccessException, SQLException {
		return mediaRepo.findMediaById(id);
	}
	
	public void deleteMedia(long videoId) throws DataAccessException, SQLException{
		mediaRepo.deleteMediaFromAccount(getAccountId(), videoId);
	}
	
	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities()).getId();
	}

	public List<Long> uploadMultipleAndReturnIds(MultipartFile[] gallery) throws DataAccessException, SQLException, IOException, DatabaseOperationException{
		List<Long> ids = new ArrayList<Long>();
		for(MultipartFile f: gallery) {
			Media insert = new Media(f.getBytes(), f.getContentType());
			ids.add(uploadMediaAndReturnId(insert));
		}
		return ids;
	}
}
 
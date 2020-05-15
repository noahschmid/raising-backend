package ch.raising.services;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.raising.data.AccountRepository;
import ch.raising.data.MediaRepository;
import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Media;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MediaException;

@Service
public class MediaService{
	private final IMediaRepository<Media> mediaRepo;
	private final int MAX_ALLOWED_ITEMS;
	
	public MediaService() {
		this.mediaRepo = null;
		this.MAX_ALLOWED_ITEMS = 0;
	}
	
	public MediaService(JdbcTemplate jdbc, String name, int maxAllowedNumbers) {
		this.mediaRepo = new MediaRepository(jdbc, name);
		this.MAX_ALLOWED_ITEMS = maxAllowedNumbers;
	}
	/**
	 * adds a image to the table if the maximum count is not exeeded
	 * @param video
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 * @throws MediaException 
	 */
	public long uploadMediaAndReturnId(Media media) throws DataAccessException, SQLException, DatabaseOperationException, MediaException {
		long accountId = getAccountId();
		if(mediaRepo.countMediaOfAccount(accountId) + 1 <= MAX_ALLOWED_ITEMS) {
			media.setAccountId(accountId);
			return mediaRepo.addMedia(media);
		}
		throw new MediaException("All media items for this account were added. Try updating instead.");
	}
	
	public void updateMediaOfAccount(MultipartFile file, long id) throws DataAccessException, SQLException, MediaException, IOException {
		Media media = new Media(id, getAccountId(), file.getContentType(), file.getBytes());
		if(id > 0) {
			media.setId(id);
			media.setAccountId(getAccountId());
			mediaRepo.updateMedia(media);
		}else {
			throw new MediaException("mediaId not specified");
		}
	}
	
	public Media getMedia(long id) throws DataAccessException, SQLException, DatabaseOperationException {
		return mediaRepo.findMediaById(id);
	}
	
	public void deleteMedia(long videoId) throws DataAccessException, SQLException{
		mediaRepo.deleteMediaFromAccount(videoId, getAccountId());
	}
	
	private long getAccountId() {
		try {
			return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal()).getId();
		}catch(Exception e) {
			return -1;
		}
	}

	/**
	 * should only be used in registration process
	 * @param gallery
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 * @throws MediaException 
	 */
	public List<Long> uploadMultipleAndReturnIds(MultipartFile[] gallery) throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException{
		long accountId = getAccountId();
		if(accountId >=0) {
			if(mediaRepo.countMediaOfAccount(accountId) + gallery.length > MAX_ALLOWED_ITEMS) {
				throw new MediaException("All media items for this account were added. Try updating instead.");
			}
		}
		List<Long> ids = new ArrayList<Long>();
		for(MultipartFile f: gallery) {
			Media insert = new Media(f.getBytes(), f.getContentType());
			ids.add(uploadMediaAndReturnId(insert));
		}
		return ids;
	}
}
 
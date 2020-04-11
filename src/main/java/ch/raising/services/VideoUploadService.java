package ch.raising.services;

import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.MediaRepository;
import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Media;

@Service
public class VideoUploadService{

	
	IMediaRepository<Media> videoRepo;
	
	public VideoUploadService(JdbcTemplate jdbc) {
		this.videoRepo = new MediaRepository(jdbc, "video");
	}
	
	public long uploadVideoAndReturnId(Media video) throws DataAccessException, SQLException {
		return videoRepo.addMedia(video.getMedia());
	}
	
	public Media getVideo(long accountId) throws DataAccessException, SQLException {
		return videoRepo.findMediaById(accountId);
	}
	
	public void deleteVideo(long videoId) throws DataAccessException, SQLException{
		videoRepo.deleteMediaFromAccount(getAccountId(), videoId);
	}
	
	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities()).getId();
	}
}

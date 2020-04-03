package ch.raising.services;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Media;

@Service
public class VideoUploadService{

	
	IMediaRepository<Media> videoRepo;
	
	@Autowired
	public VideoUploadService(IMediaRepository<Media> videoRepo) {
		this.videoRepo = videoRepo;
	}
	
	public long uploadVideoAndReturnId(Media video) throws DataAccessException, SQLException {
		long accountId = getAccountId();
		long mediaId = videoRepo.getMediaIdOf(accountId);
		if(videoRepo.getMediaIdOf(accountId) != -1) {
			videoRepo.deleteMediaFromAccount(mediaId, accountId);
		}
		return videoRepo.addMediaBytes(video.getMedia());
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

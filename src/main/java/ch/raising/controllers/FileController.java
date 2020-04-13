package ch.raising.controllers;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ch.raising.models.Media;
import ch.raising.models.MediaUploadResponse;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.models.responses.FileUploadResponse;
import ch.raising.services.MediaService;
import ch.raising.utils.DatabaseOperationException;

@RequestMapping("/upload")
@Controller
public class FileController {
	
	MediaService videoService;
	MediaService ppicService;
	MediaService galleryService;
	
	private final static int MAX_GALLERY_SIZE = 9;
	
	public FileController(JdbcTemplate jdbc) {
		this.videoService = new MediaService(jdbc, "video");
		this.ppicService = new MediaService(jdbc, "profilepicture");
		this.galleryService = new MediaService(jdbc, "gallery");
	}
	
	@PostMapping("/video")
	public ResponseEntity<?> uploadVideo(@RequestBody Media video) throws DataAccessException, SQLException, DatabaseOperationException{
		long videoid = videoService.uploadMediaAndReturnId(video);
		return ResponseEntity.ok().body(new MediaUploadResponse(videoid));
	}
	
	@DeleteMapping("/video/{videoId}")
	public ResponseEntity<?> deleteVideo(@PathVariable long videoId) throws DataAccessException, SQLException{
		videoService.deleteMedia(videoId);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/video/{accountId}")
	public ResponseEntity<?> getVideo(@PathVariable long accountId) throws DataAccessException, SQLException{
		return ResponseEntity.ok().body(videoService.getMedia(accountId));
	}
	
	/**
	 * 
	 * @param id of the profilepicture
	 * @return
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	@GetMapping("/profilepicture/{id}")
	public ResponseEntity<?> getProfilePicture(@PathVariable long id) throws DataAccessException, SQLException{
		Media ppic = ppicService.getMedia(id);
		MediaType returns = MediaType.parseMediaType(ppic.getContentType());
		return ResponseEntity.ok().contentType(returns).body(ppic.getMedia());
	}
	/**
	 * 
	 * @param file To be uploaded
	 * @return a response containing the id of the uploaded picture
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 */
	@PostMapping("/profilepicture")
	public ResponseEntity<?> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file) throws DataAccessException, SQLException, IOException, DatabaseOperationException{
		if(file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")) {
			long picId = ppicService.uploadMediaAndReturnId(new Media(file.getBytes(), file.getContentType()));
			return ResponseEntity.ok(new FileUploadResponse("Added new Profilepicture", picId));
		}
		return ResponseEntity.status(415).body(new ErrorResponse("Filetype not supported, try .png or .jpeg"));
	}
	@GetMapping("/gallery/{id}")
	public ResponseEntity<?> getGallery(@PathVariable long id) throws DataAccessException, SQLException{
		Media ppic = galleryService.getMedia(id);
		MediaType returns = MediaType.parseMediaType(ppic.getContentType());
		return ResponseEntity.ok().contentType(returns).body(ppic.getMedia());
	}
	/**
	 * Add multiple images to the gallery
	 * @param gallery
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 */
	@PostMapping("/gallery")
	public ResponseEntity<?> uploadGallery(@RequestParam("gallery") MultipartFile[] gallery) throws DataAccessException, SQLException, IOException, DatabaseOperationException{
		if(gallery.length > MAX_GALLERY_SIZE) {
			return ResponseEntity.status(413).body(new ErrorResponse("The account cannot have more than " + MAX_GALLERY_SIZE + "pictures"));
		}
		
		return ResponseEntity.ok().body(galleryService.uploadMultipleAndReturnIds(gallery));
	}

}

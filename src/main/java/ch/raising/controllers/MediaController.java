package ch.raising.controllers;


import java.io.IOException;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ch.raising.models.Media;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.models.responses.FileUploadResponse;
import ch.raising.services.MediaService;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MediaNotAddedException;

@RequestMapping("/media")
@Controller
public class MediaController {
	
	MediaService videoService;
	MediaService ppicService;
	MediaService galleryService;
	
	private final static int MAX_GALLERY_SIZE = 9;
	private final static int MAX_VIDEO_SIZE = 1;
	private final static int MAX_PPIC_SIZE = 1;
	
	@Autowired
	public MediaController(JdbcTemplate jdbc) {
		this.videoService = new MediaService(jdbc, "video", MAX_VIDEO_SIZE);
		this.ppicService = new MediaService(jdbc, "profilepicture", MAX_PPIC_SIZE);
		this.galleryService = new MediaService(jdbc, "gallery", MAX_GALLERY_SIZE);
	}
	
	@PostMapping("/video")
	public ResponseEntity<?> uploadVideo(@RequestBody Media video) throws DataAccessException, SQLException, DatabaseOperationException, MediaNotAddedException{
		long videoid = videoService.uploadMediaAndReturnId(video);
		return ResponseEntity.ok().body(new FileUploadResponse("added video", videoid));
	}
	
	@DeleteMapping("/video/{videoId}")
	public ResponseEntity<?> deleteVideo(@PathVariable long videoId) throws DataAccessException, SQLException{
		videoService.deleteMedia(videoId);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/video/{accountId}")
	public ResponseEntity<?> getVideoOfAccount(@PathVariable long accountId) throws DataAccessException, SQLException{
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
	 * @throws MediaNotAddedException 
	 */
	@PostMapping("/profilepicture")
	public ResponseEntity<?> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file) throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaNotAddedException{
		if(file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg") ||  file.getContentType().equals("image/jpeg")) {
			long picId = ppicService.uploadMediaAndReturnId(new Media(file.getBytes(), file.getContentType()));
			return ResponseEntity.ok(new FileUploadResponse("Added new Profilepicture", picId));
		}
		return ResponseEntity.status(415).body(new ErrorResponse("Filetype not supported, try .png or .jpeg"));
	}
	/**
	 * updates the profilepicture
	 * @param file
	 * @param id
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws MediaNotAddedException
	 * @throws IOException
	 */
	@PatchMapping("/profilepicture/{id}")
	public ResponseEntity<?> updateProfilePicture(@RequestParam("profilePicture") MultipartFile file, @PathVariable("id") long id) throws DataAccessException, SQLException, MediaNotAddedException, IOException{
		System.out.println("============================= " + id );
		if(file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")) {
			ppicService.updateMediaOfAccount(file, id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(415).body(new ErrorResponse("Filetype not supported, try .png or .jpeg"));
	}
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	@DeleteMapping("/profilepicture/{id}")
	public ResponseEntity<?> deleteProfilePicture(@PathVariable long id) throws DataAccessException, SQLException{
		ppicService.deleteMedia(id);
		return ResponseEntity.ok().build();
	}
	/**
	 * returns an image from gallery specified by the id of said image
	 * @param id
	 * @return a byte[] and the contenttype in the header
	 * @throws DataAccessException
	 * @throws SQLException
	 */
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
	 * @throws MediaNotAddedException 
	 */
	@PostMapping("/gallery")	
	public ResponseEntity<?> uploadGallery(@RequestParam("gallery") MultipartFile[] gallery) throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaNotAddedException{
		if(gallery.length > MAX_GALLERY_SIZE) {
			return ResponseEntity.status(413).body(new ErrorResponse("The account cannot have more than " + MAX_GALLERY_SIZE + "pictures"));
		}
		return ResponseEntity.ok().body(galleryService.uploadMultipleAndReturnIds(gallery));
	}
	@PatchMapping("/gallery/{id}")
	public ResponseEntity<?> updateGalleryImage(@RequestParam("gallery") MultipartFile file, @PathVariable long id) throws DataAccessException, SQLException, MediaNotAddedException, IOException{
		if(file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")) {
			galleryService.updateMediaOfAccount(file, id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(415).body(new ErrorResponse("Filetype not supported, try .png or .jpeg"));
	}
	@DeleteMapping("/gallery/{id}")
	public ResponseEntity<?> deleteGalleryImage(@PathVariable long id) throws DataAccessException, SQLException{
		galleryService.deleteMedia(id);
		return ResponseEntity.ok().build();
	}

}

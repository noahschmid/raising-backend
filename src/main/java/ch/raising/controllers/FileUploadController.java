package ch.raising.controllers;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import ch.raising.models.ErrorResponse;
import ch.raising.models.Media;
import ch.raising.models.MediaUploadResponse;
import ch.raising.services.VideoUploadService;

@RequestMapping("/upload")
@Controller
public class FileUploadController {
	
	VideoUploadService videoService;
	
	@Autowired
	public FileUploadController(VideoUploadService fileUpload) {
		this.videoService = fileUpload;
	}
	
	@PostMapping("/video")
	public ResponseEntity<?> uploadVideo(@RequestBody Media video) throws DataAccessException, SQLException{
		long videoid = videoService.uploadVideoAndReturnId(video);
		return ResponseEntity.ok().body(new MediaUploadResponse(videoid));
	}
	
	@DeleteMapping("/video/{videoId}")
	public ResponseEntity<?> deleteVideo(@PathVariable long videoId) throws DataAccessException, SQLException{
		videoService.deleteVideo(videoId);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/video/{accountId}")
	public ResponseEntity<?> getVideo(@PathVariable long accountId) throws DataAccessException, SQLException{
		return ResponseEntity.ok().body(videoService.getVideo(accountId));
	}
	
	@ExceptionHandler(IOException.class)
	public ResponseEntity<?> handleIOException(){
		return ResponseEntity.status(500).body(new ErrorResponse("File malformed."));
	}
	
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<?> handleDBAccessExceptions(){
		return ResponseEntity.status(500).body(new ErrorResponse("Database action not preformed,"));
	}
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<?> handleSQLExceptionExceptions(){
		return ResponseEntity.status(500).body(new ErrorResponse("Database action not preformed. SQL statement malformed or no result found."));
	}
}

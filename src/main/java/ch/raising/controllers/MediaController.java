package ch.raising.controllers;

import java.io.IOException;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
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

import ch.raising.data.AccountRepository;
import ch.raising.models.Icon;
import ch.raising.models.Media;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.models.responses.ReturnIdResponse;
import ch.raising.services.IconService;
import ch.raising.services.MediaService;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MediaException;

@RequestMapping("/media")
@Controller
public class MediaController {

	private final MediaService videoService;
	private final MediaService ppicService;
	private final MediaService galleryService;
	private final MediaService documentService;
	private final IconService iconService;
	
	private final static int RAISING_ICON = 49;

	private final static int MAX_GALLERY_SIZE = 9;
	private final static int MAX_VIDEO_SIZE = 1;
	private final static int MAX_PPIC_SIZE = 1;
	private final static int MAX_PDF_SIZE = 1;


	private AccountRepository accountRepository;

	@Autowired
	public MediaController(JdbcTemplate jdbc, IconService iconService, AccountRepository accountRepository) {
		this.videoService = new MediaService(jdbc, "video", MAX_VIDEO_SIZE);
		this.ppicService = new MediaService(jdbc, "profilepicture", MAX_PPIC_SIZE);
		this.galleryService = new MediaService(jdbc, "gallery", MAX_GALLERY_SIZE);
		this.documentService = new MediaService(jdbc, "document", MAX_PDF_SIZE);
		this.iconService = iconService;
	}

	@PostMapping("/video")
	public ResponseEntity<?> uploadVideo(@RequestBody Media video)
			throws DataAccessException, SQLException, DatabaseOperationException, MediaException {
		long videoid = videoService.uploadMediaAndReturnId(video);
		return ResponseEntity.ok().body(new ReturnIdResponse("added video", videoid));
	}

	@DeleteMapping("/video/{videoId}")
	public ResponseEntity<?> deleteVideo(@PathVariable long videoId) throws DataAccessException, SQLException {
		videoService.deleteMedia(videoId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/video/{id}")
	public ResponseEntity<?> getVideoOfAccount(@PathVariable long id)
			throws DataAccessException, SQLException, DatabaseOperationException {
		return ResponseEntity.ok().body(videoService.getMedia(id));
	}

	/**
	 * 
	 * @param id of the profilepicture
	 * @return
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	@GetMapping("/profilepicture/{id}")
	public ResponseEntity<?> getProfilePicture(@PathVariable long id)
			throws DatabaseOperationException, DataAccessException, SQLException {
		Media ppic = ppicService.getMedia(id);
		MediaType returns = getMediaType(ppic.getContentType());
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
	 * @throws MediaException
	 */
	@PostMapping("/profilepicture")
	public ResponseEntity<?> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		if (file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")
				|| file.getContentType().equals("image/jpeg")) {
			long picId = ppicService.uploadMediaAndReturnId(new Media(file.getBytes(), file.getContentType()));
			return ResponseEntity.ok(new ReturnIdResponse("Added new Profilepicture", picId));
		}
		return ResponseEntity.status(415).body(new ErrorResponse("Filetype not supported, try .png or .jpeg"));
	}

	/**
	 * updates the profilepicture
	 * 
	 * @param file
	 * @param id
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws MediaException
	 * @throws IOException
	 */
	@PatchMapping("/profilepicture/{id}")
	public ResponseEntity<?> updateProfilePicture(@RequestParam("profilePicture") MultipartFile file,
			@PathVariable("id") long id) throws DataAccessException, SQLException, MediaException, IOException {
		if (file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")) {
			ppicService.updateMediaOfAccount(file, id);
			accountRepository.updateLastChanged();
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
	public ResponseEntity<?> deleteProfilePicture(@PathVariable long id) throws DataAccessException, SQLException {
		ppicService.deleteMedia(id);
		return ResponseEntity.ok().build();
	}

	/**
	 * returns an image from gallery specified by the id of said image
	 * 
	 * @param id
	 * @return a byte[] and the contenttype in the header
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
	@GetMapping("/gallery/{id}")
	public ResponseEntity<?> getGallery(@PathVariable long id)
			throws DataAccessException, SQLException, DatabaseOperationException {
		Media galleryImage = galleryService.getMedia(id);
		MediaType returns = getMediaType(galleryImage.getContentType());
		return ResponseEntity.ok().contentType(returns).body(galleryImage.getMedia());
	}

	/**
	 * Add multiple images to the gallery
	 * 
	 * @param gallery
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 * @throws MediaException
	 */
	@PostMapping("/gallery")
	public ResponseEntity<?> uploadGallery(@RequestParam("gallery") MultipartFile[] gallery)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		if (gallery.length > MAX_GALLERY_SIZE) {
			return ResponseEntity.status(413)
					.body(new ErrorResponse("The account cannot have more than " + MAX_GALLERY_SIZE + "pictures"));
		}
		return ResponseEntity.ok().body(galleryService.uploadMultipleAndReturnIds(gallery));
	}

	@PatchMapping("/gallery/{id}")
	public ResponseEntity<?> updateGalleryImage(@RequestParam("gallery") MultipartFile file, @PathVariable long id)
			throws DataAccessException, SQLException, MediaException, IOException {
		if (file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")) {
			galleryService.updateMediaOfAccount(file, id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(415).body(new ErrorResponse("Filetype not supported, try .png or .jpeg"));
	}

	@DeleteMapping("/gallery/{id}")
	public ResponseEntity<?> deleteGalleryImage(@PathVariable long id) throws DataAccessException, SQLException {
		galleryService.deleteMedia(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/document/{id}")
	public ResponseEntity<?> getBusinessPlan(@PathVariable long id)
			throws DataAccessException, SQLException, DatabaseOperationException {
		Media document = documentService.getMedia(id);
		MediaType returns = MediaType.parseMediaType(document.getContentType());
		return ResponseEntity.ok().contentType(returns).body(document.getMedia());
	}

	@PostMapping("/document")
	public ResponseEntity<?> uploadBusinessplan(@RequestParam("document") MultipartFile[] docs)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		if (docs.length > MAX_GALLERY_SIZE) {
			return ResponseEntity.status(413)
					.body(new ErrorResponse("The account cannot have more than " + MAX_PDF_SIZE + "pdf"));
		}
		return ResponseEntity.ok().body(documentService.uploadMultipleAndReturnIds(docs));
	}

	@PatchMapping("/document/{id}")
	public ResponseEntity<?> updateDocument(@RequestParam("document") MultipartFile file, @PathVariable long id)
			throws DataAccessException, SQLException, MediaException, IOException {
		if (!file.getContentType().equals("application/pdf"))
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
					.body(new ErrorResponse("Content-type must be application/pdf"));
		documentService.updateMediaOfAccount(file, id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/document/{id}")
	public ResponseEntity<?> deleteDocument(@PathVariable long id) throws DataAccessException, SQLException {
		documentService.deleteMedia(id);
		return ResponseEntity.ok().build();
	}

	private MediaType getMediaType(String type) throws InvalidMediaTypeException {
		return type.equals("none") ? null : MediaType.parseMediaType(type);
	}

	@PostMapping("/icon/investortype/{tableEntryId}")
	public ResponseEntity<?> uploadInvTypeIcon(@PathVariable long tableEntryId,
			@RequestParam("icon") MultipartFile icon)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		if (!icon.getContentType().equals("image/png"))
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
					.body(new ErrorResponse("Content-type must be image/png"));
		iconService.addToInvestortype(icon, tableEntryId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/icon/investmentphase/{tableEntryId}")
	public ResponseEntity<?> uploadInvPhaseIcon(@PathVariable long tableEntryId,
			@RequestParam("icon") MultipartFile icon)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		if (!icon.getContentType().equals("image/png"))
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
					.body(new ErrorResponse("Content-type must be image/png"));
		iconService.addToInvestmentPhase(icon, tableEntryId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/icon/support/{tableEntryId}")
	public ResponseEntity<?> uploadSupIcon(@PathVariable long tableEntryId, @RequestParam("icon") MultipartFile icon)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		if (!icon.getContentType().equals("image/png"))
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
					.body(new ErrorResponse("Content-type must be image/png"));
		iconService.addToSupport(icon, tableEntryId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/icon/industry/{tableEntryId}")
	public ResponseEntity<?> uploadIndustryIcon(@PathVariable long tableEntryId,
			@RequestParam("icon") MultipartFile icon)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		if (!icon.getContentType().equals("image/png"))
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
					.body(new ErrorResponse("Content-type must be image/png"));
		iconService.addToIndustry(icon, tableEntryId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/icon")
	public ResponseEntity<?> uploadLabelIcon(@RequestParam("icon") MultipartFile icon)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		if (!icon.getContentType().equals("image/png"))
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
					.body(new ErrorResponse("Content-type must be image/png"));
		return ResponseEntity.ok(iconService.addIconAndReturnId(icon));
	}

	@PatchMapping("/icon/{id}")
	public ResponseEntity<?> updateIcon(@PathVariable int id, @RequestParam("icon") MultipartFile icon)
			throws DataAccessException, IOException {
		iconService.update(id, icon);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/icon/{id}")
	public ResponseEntity<?> getIcon(@PathVariable long id) {
		Icon icon = iconService.getIcon(id);
		MediaType returns = getMediaType(icon.getContentType());
		return ResponseEntity.ok().contentType(returns).body(icon.getIcon());
	}
	@GetMapping("/icon")
	public ResponseEntity<?> getIcon() throws DataAccessException, SQLException {
		return ResponseEntity.ok().body(iconService.getAllIcons());
	}
	
	@GetMapping("/icon/raising")
	public ResponseEntity<?> getRaisingIcon() {
		Icon icon = iconService.getIcon(RAISING_ICON);
		MediaType returns = getMediaType(icon.getContentType());
		return ResponseEntity.ok().contentType(returns).body(icon.getIcon());
	}
}

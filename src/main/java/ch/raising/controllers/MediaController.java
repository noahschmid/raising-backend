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
import org.springframework.security.core.context.SecurityContextHolder;
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
import ch.raising.models.Account;
import ch.raising.models.Icon;
import ch.raising.models.Media;
import ch.raising.models.AccountDetails;
import ch.raising.models.responses.ErrorResponse;
import ch.raising.models.responses.ReturnIdResponse;
import ch.raising.services.IconService;
import ch.raising.services.MediaService;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MediaException;

/**
 * This end point is used for storing, updating and retreiving media.
 * 
 * @author manus
 *
 */
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
		this.accountRepository = accountRepository;
	}

	/**
	 * Fetches the profile picture having this id.
	 * 
	 * @param id of the profilepicture
	 * @return ResponseEntity with a byte[] in the body represented a or a response
	 *         according to {@link ControllerExceptionHandler}
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
	 * @return ResponseEntity with {@link ReturnIdResponse} in the body represented
	 *         a or a response according to {@link ControllerExceptionHandler} if
	 *         the image has the wrong content type code 415 will be returned
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
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
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
			accountRepository.updateLastChanged(getAccountId());
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(415).body(new ErrorResponse("Filetype not supported, try .png or .jpeg"));
	}

	/**
	 * Get id of account that's sending the request
	 * 
	 * @return id of the user that makes the request.
	 */
	private long getAccountId() {
		try {
			return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * deletes the image from the id
	 * 
	 * @param id
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	@DeleteMapping("/profilepicture/{id}")
	public ResponseEntity<?> deleteProfilePicture(@PathVariable long id) throws DataAccessException, SQLException {
		accountRepository.deleteProfilePicture(getAccountId());
		ppicService.deleteMedia(id);
		return ResponseEntity.ok().build();
	}

	/**
	 * returns an image from gallery specified by the id of said image
	 * 
	 * @param id
	 * @return a byte[] and the content type in the header
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

	/**
	 * 
	 * @param MultipartFile
	 * @param id
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws MediaException
	 * @throws IOException
	 */
	@PatchMapping("/gallery/{id}")
	public ResponseEntity<?> updateGalleryImage(@RequestParam("gallery") MultipartFile file, @PathVariable long id)
			throws DataAccessException, SQLException, MediaException, IOException {
		if (file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")) {
			galleryService.updateMediaOfAccount(file, id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.status(415).body(new ErrorResponse("Filetype not supported, try .png or .jpeg"));
	}

	/**
	 * 
	 * @param id of the media to be deleted
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	@DeleteMapping("/gallery/{id}")
	public ResponseEntity<?> deleteGalleryImage(@PathVariable long id) throws DataAccessException, SQLException {
		galleryService.deleteMedia(id);
		return ResponseEntity.ok().build();
	}

	/**
	 * 
	 * @param id of the document to be fetched
	 * @return ResponseEntity with a byte[] as body or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
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

	/**
	 * 
	 * @param id of the media to be deleted
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	@DeleteMapping("/document/{id}")
	public ResponseEntity<?> deleteDocument(@PathVariable long id) throws DataAccessException, SQLException {
		documentService.deleteMedia(id);
		return ResponseEntity.ok().build();
	}

	private MediaType getMediaType(String type) throws InvalidMediaTypeException {
		return type.equals("none") ? null : MediaType.parseMediaType(type);
	}

	/**
	 * 
	 * @param tableEntryId the id of the investortype the icon should be mapped to
	 * @param icon         as MultiPartFile the filetype must be png
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 * @throws MediaException
	 */
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

	/**
	 * 
	 * @param tableEntryId the id of the investmentphase the icon should be mapped
	 *                     to
	 * @param icon         as MultiPartFile the filetype must be png
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 * @throws MediaException
	 */
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
	/**
	 * 
	 * @param tableEntryId the id of the support the icon should be mapped to
	 * @param icon as MultiPartFile the filetype must be png
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 * @throws MediaException
	 */
	@PostMapping("/icon/support/{tableEntryId}")
	public ResponseEntity<?> uploadSupIcon(@PathVariable long tableEntryId, @RequestParam("icon") MultipartFile icon)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		if (!icon.getContentType().equals("image/png"))
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
					.body(new ErrorResponse("Content-type must be image/png"));
		iconService.addToSupport(icon, tableEntryId);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 
	 * @param tableEntryId the id of the intdustry the icon should be mapped to
	 * @param icon         as MultiPartFile the filetype must be png
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 * @throws MediaException
	 */
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

	/**
	 * 
	 * 
	 * @param icon as MultiPartFile the filetype must be png
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 * @throws MediaException
	 */
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

	/**
	 * 
	 * @param id of the document to be fetched
	 * @return ResponseEntity with a byte[] as body or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
	@GetMapping("/icon/{id}")
	public ResponseEntity<?> getIcon(@PathVariable long id) {
		Icon icon = iconService.getIcon(id);
		MediaType returns = getMediaType(icon.getContentType());
		return ResponseEntity.ok().contentType(returns).body(icon.getIcon());
	}

	/**
	 * 
	 * @param id of the document to be fetched
	 * @return ResponseEntity with a List of {@link Icon} as body or a response
	 *         according to {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
	@GetMapping("/icon")
	public ResponseEntity<?> getIcon() throws DataAccessException, SQLException {
		return ResponseEntity.ok().body(iconService.getAllIcons());
	}

	/**
	 * returns the raising icon
	 * 
	 * @param id of the document to be fetched
	 * @return ResponseEntity with a byte[] as body or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
	@GetMapping("/icon/raising")
	public ResponseEntity<?> getRaisingIcon() {
		Icon icon = iconService.getIcon(RAISING_ICON);
		MediaType returns = getMediaType(icon.getContentType());
		return ResponseEntity.ok().contentType(returns).body(icon.getIcon());
	}
}

package ch.raising.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;

/**
 * universal service class that is used to manage any media
 * 
 * @author noahs, manus
 *
 */
@Service
public class MediaService {
	private final IMediaRepository mediaRepo;
	private final int MAX_ALLOWED_ITEMS;

	private final double MAX_FILE_SIZE = 150 * 1024;

	public MediaService() {
		this.mediaRepo = null;
		this.MAX_ALLOWED_ITEMS = 0;
	}

	public MediaService(JdbcTemplate jdbc, String name, int maxAllowedNumbers) {
		this.mediaRepo = new MediaRepository(jdbc, name);
		this.MAX_ALLOWED_ITEMS = maxAllowedNumbers;
	}

	/**
	 * adds a image to the table if the maximum count is not exceeded
	 * 
	 * @param media
	 * @return the id of the newly inserted media.
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 * @throws MediaException
	 */
	public long uploadMediaAndReturnId(Media media)
			throws DataAccessException, SQLException, DatabaseOperationException, MediaException {
		long accountId = getAccountId();
		if (mediaRepo.countMediaOfAccount(accountId) + 1 <= MAX_ALLOWED_ITEMS) {
			media.setAccountId(accountId);
			return mediaRepo.addMedia(compress(media));
		}
		throw new MediaException("All media items for this account were added. Try updating instead.");
	}

	/**
	 * updates any media that belonging to a specific account
	 * 
	 * @param file
	 * @param id
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws MediaException
	 * @throws IOException
	 */
	public void updateMediaOfAccount(MultipartFile file, long id)
			throws DataAccessException, SQLException, MediaException, IOException {
		Media media = new Media(id, getAccountId(), file.getContentType(), file.getBytes());
		if (id > 0) {
			media.setId(id);
			media.setAccountId(getAccountId());
			mediaRepo.updateMedia(compress(media));
		} else {
			throw new MediaException("mediaId not specified");
		}
	}

	/**
	 * 
	 * @param id
	 * @return an {@link Media} object containing metadata and the media as byte[]
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws DatabaseOperationException
	 */
	public Media getMedia(long id) throws DataAccessException, SQLException, DatabaseOperationException {
		return mediaRepo.findMediaById(id);
	}

	/**
	 * 
	 * @param mediaId to delete any media by id
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public void deleteMedia(long mediaId) throws DataAccessException, SQLException {
		mediaRepo.deleteMedia(mediaId);
	}

	private long getAccountId() {
		try {
			return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * should only be used in registration process
	 * 
	 * @param gallery
	 * @return
	 * @throws DataAccessException
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseOperationException
	 * @throws MediaException
	 */
	public List<Long> uploadMultipleAndReturnIds(MultipartFile[] gallery)
			throws DataAccessException, SQLException, IOException, DatabaseOperationException, MediaException {
		long accountId = getAccountId();
		if (accountId >= 0) {
			if (mediaRepo.countMediaOfAccount(accountId) + gallery.length > MAX_ALLOWED_ITEMS) {
				throw new MediaException("All media items for this account were added. Try updating instead.");
			}
		}
		List<Long> ids = new ArrayList<Long>();
		for (MultipartFile f : gallery) {
			Media insert = new Media(f.getBytes(), f.getContentType());
			ids.add(uploadMediaAndReturnId(insert));
		}
		return ids;
	}

	/**
	 * compresses all the media that is inserted
	 * 
	 * @param media
	 * @return
	 */
	private Media compress(Media media) {
		if (media.getContentType().contains("image/")) {
			String type = media.getContentType().substring(6);
			ByteArrayInputStream bis = new ByteArrayInputStream(media.getMedia());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			try {
				BufferedImage bufferedImage = ImageIO.read(bis);
				Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(type);

				if (!imageWriters.hasNext())
					throw new IllegalStateException("Writers Not Found!!");

				long size = media.getMedia().length;
				double factor = MAX_FILE_SIZE / size;

				if (factor >= 1)
					return media;

				factor = Math.max(factor, 0.2);

				ImageWriter imageWriter = (ImageWriter) imageWriters.next();
				ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(bos);
				imageWriter.setOutput(imageOutputStream);

				ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

				// Set the compress quality metrics
				imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				imageWriteParam.setCompressionQuality((float) factor);

				// Created image
				imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);

				media.setMedia(bos.toByteArray());
				System.out.println("Compression successful. Original size: " + size + "KB new size: "
						+ (media.getMedia().length / 1024) + "KB");

			} catch (Exception e) {
				System.out.println("Exception while compressing: " + e.getMessage());
			}

		}

		return media;
	}
}

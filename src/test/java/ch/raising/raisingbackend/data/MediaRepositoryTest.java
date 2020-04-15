package ch.raising.raisingbackend.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;

import javax.management.Query;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import ch.raising.data.MediaRepository;
import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.Media;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MapUtil;
import ch.raising.utils.PreparedStatementUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { TestConfig.class })
@SpringBootTest
@ActiveProfiles("RepositoryTest")
@TestInstance(Lifecycle.PER_CLASS)
public class MediaRepositoryTest {

	JdbcTemplate jdbc;

	byte[] imageBytes;
	Media image;
	IMediaRepository imgrepo;

	@Autowired
	public MediaRepositoryTest(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.imageBytes = "DEADBEEF".getBytes();
		imgrepo = new MediaRepository(jdbc, "gallery");
		image = Media.builder().media(imageBytes).build();
	}

	@BeforeEach
	private void setup() {
		createTable();
		addImage();
	}

	private void createTable() {
		String sql = QueryBuilder.getInstance().tableName("gallery").pair("id", Type.SERIAL).pair("type", Type.VARCHAR)
				.pair("accountid", Type.BIGINT).pair("media", Type.BYTEA).createTable();
		jdbc.execute(sql);
	}

	@AfterEach
	private void cleanup() {
		JdbcTestUtils.dropTables(jdbc, "gallery");
	}

	private void addImage() {
		String sql = QueryBuilder.getInstance().tableName("gallery").attribute("accountid, media, type").qMark().qMark()
				.qMark().insert();
		jdbc.execute(sql, PreparedStatementUtil.addMediaByIdCallback(image, 2));
	}

	@Test
	public void addImageToAccountTest() throws SQLException, DataAccessException, DatabaseOperationException {
		Media img = Media.builder().accountId(2).media(imageBytes).build();
		imgrepo.addMedia(img);

		String sql = QueryBuilder.getInstance().tableName("gallery").whereEquals("accountid", "2").select();
		List<Media> found = jdbc.query(sql, MapUtil::mapRowToMedia);
		assertNotNull(found);
		assertEquals(2, found.size());
		Media foundImage = found.get(1);
		assertNotNull(foundImage);
		assertEquals(imageBytes.length, foundImage.getMedia().length);
		for (int i = 0; i < imageBytes.length; i++) {
			assertEquals(imageBytes[i], foundImage.getMedia()[i]);
		}
		assertEquals(2, foundImage.getAccountId());
		assertEquals(2, foundImage.getId());

	}

	@Test
	public void findImagesByAccountId() throws DataAccessException, SQLException, DatabaseOperationException {
		List<Media> found = imgrepo.findMediaByAccountId(2);
		assertNotNull(found);
		assertEquals(1, found.size());
		Media foundImage = found.get(0);
		assertNotNull(foundImage);
		assertEquals(image.getMedia().length, foundImage.getMedia().length);
		for (int i = 0; i < image.getMedia().length; i++) {
			assertEquals(image.getMedia()[i], foundImage.getMedia()[i]);
		}

		assertEquals(2, foundImage.getAccountId());
		assertEquals(1, foundImage.getId());
	}

	@Test
	public void deleteImageFromAccount() throws DataAccessException, SQLException {
		imgrepo.deleteMediaFromAccount(1, 2);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, "gallery"));
	}

}

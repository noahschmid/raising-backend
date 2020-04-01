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

import ch.raising.data.ImageRepository;
import ch.raising.models.Image;
import ch.raising.utils.MapUtil;
import ch.raising.utils.PreparedStatementUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.Type;

@ContextConfiguration(classes = { RepositoryTestConfig.class })
@SpringBootTest
@ActiveProfiles("RepositoryTest")
@TestInstance(Lifecycle.PER_CLASS)
public class ImageRepositoryTest {

	
	JdbcTemplate jdbc;
	
	String imageString;
	Image image;
	ImageRepository imgrepo;
	
	@Autowired
	public ImageRepositoryTest(JdbcTemplate jdbc){
		this.jdbc = jdbc;
		this.imageString = "DEADBEEF";
		imgrepo = new ImageRepository(jdbc, "gallery");
		image = Image.builder().image(imageString).build();
	}
	
	@BeforeEach
	private void setup() {
		createTable();
		addImage();
	}

	private void createTable() {
		String sql = QueryBuilder.getInstance().tableName("gallery").pair("id", Type.SERIAL)
				.pair("accountid", Type.BIGINT).pair("image", Type.BYTEA).createTable();
		jdbc.execute(sql);
	}

	@AfterEach
	private void cleanup() {
		JdbcTestUtils.dropTables(jdbc, "gallery");
	}

	private void addImage() {
		String sql = QueryBuilder.getInstance().tableName("gallery").attribute("accountid, image").qMark().qMark().insert();
		jdbc.execute(sql, PreparedStatementUtil.addImageByIdCallback(image, 1));
	}
	
	@Test
	public void addImageToAccountTest() throws SQLException {
		Image img = Image.builder().accountId(1).image(imageString).build();
		imgrepo.addImageToAccount(img, 1);
		
		String sql = QueryBuilder.getInstance().tableName("gallery").whereEquals("accountid", ""+1).select();
		List<Image> found = jdbc.query(sql, MapUtil::mapRowToImage);
		assertNotNull(found);
		assertEquals(2, found.size());
		Image foundImage = found.get(1);
		assertNotNull(foundImage);
		assertEquals(imageString, foundImage.getImage());
		assertEquals(1, foundImage.getAccountId());
		assertEquals(2, foundImage.getId());
		
	}
	@Test 
	public void findImagesByAccountId() {
		List<Image> found = imgrepo.findImagesByAccountId(1);
		assertNotNull(found);
		assertEquals(1, found.size());
		Image foundImage = found.get(0);
		assertNotNull(foundImage);
		assertEquals(imageString, foundImage.getImage());
		assertEquals(1, foundImage.getAccountId());
		assertEquals(1, foundImage.getId());
	}
	@Test
	public void deleteImageFromAccount() throws DataAccessException, SQLException {
		imgrepo.deleteImageFromAccount(1, 1);
		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbc, "gallery"));
	}

}

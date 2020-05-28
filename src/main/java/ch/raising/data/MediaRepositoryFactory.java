package ch.raising.data;

import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.stereotype.Repository;
/**
 * factory for the different media repositories
 * @author manus
 *
 */
@Repository
public class MediaRepositoryFactory {

	private final JdbcTemplate jdbc;

	public MediaRepositoryFactory(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	public MediaRepository getMediaRepository(String type) throws SQLException {
		switch (type) {
		case "profilepicture":
			return new MediaRepository(jdbc, type);
		case "gallery":
			return new MediaRepository(jdbc, type);
		case "video":
			return new MediaRepository(jdbc, type);
		case "document":
			return new MediaRepository(jdbc, type);
		default:
			throw new SQLException("no table with name " + type + " exists");
		}
		
	}
}

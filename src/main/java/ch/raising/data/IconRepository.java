package ch.raising.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import ch.raising.models.Icon;


@Repository
public class IconRepository {
	
	JdbcTemplate jdbc;
	String INSERT_ICON = "INSERT INTO icon(media, type, lastchanged) VALUES (?,?,now())";

	public IconRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	public long addMedia(Icon icon) throws SQLException, DataAccessException{
		return jdbc.execute(new AddAndReturnIdPreparedStatement(), addIconCallback(icon));
	}    
	
	
	
	private PreparedStatementCallback<Long> addIconCallback(Icon icon) {
		return new PreparedStatementCallback<Long>() {

			@Override
			public Long doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setBytes(c++, icon.getIcon());
				ps.setString(c++, icon.getContentType());
				if(ps.executeUpdate() > 0) {
					if(ps.getGeneratedKeys().next()) {
						return ps.getGeneratedKeys().getLong("id");
					}
				}
				return -1l;
			}
		};
	}
	private class AddAndReturnIdPreparedStatement implements PreparedStatementCreator{

		@Override
		public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
			return con.prepareStatement(INSERT_ICON, Statement.RETURN_GENERATED_KEYS);
		}
		
	}
}

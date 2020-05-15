package ch.raising.data;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import ch.raising.models.Icon;
import ch.raising.utils.MapUtil;


@Repository
public class IconRepository {
	
	private final JdbcTemplate jdbc;
	private final String INSERT_ICON = "INSERT INTO icon(media, type, lastchanged) VALUES (?,?,now())";
	private final String FIND_BY_ID = "SELECT * FROM icon where id = ?";
	private final String UPDATE = "UPDATE icon set type = ?, media = ? where id = ?";
	private final String FIND_ALL = "SELECT id, type, media, lastchanged FROM icon";

	public IconRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	public long addMedia(Icon icon) throws SQLException, DataAccessException{
		return jdbc.execute(new AddAndReturnIdPreparedStatement(), addIconCallback(icon));
	}    
	
	public Icon find(long id) {
		return jdbc.queryForObject(FIND_BY_ID, new Object[] {id}, new IconMapper());
	}

	public void update(Icon build) throws DataAccessException{
		jdbc.update(UPDATE, new Object[] {build.getContentType(), build.getIcon(), build.getId()}, new int[] {Types.VARCHAR, Types.BLOB, Types.BIGINT});
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
	
	private class IconMapper implements RowMapper<Icon>{

		@Override
		public Icon mapRow(ResultSet rs, int rowNum) throws SQLException {
			return Icon.builder().id(rs.getLong("id")).contentType(rs.getString("type")).icon(rs.getBytes("media")).build();
		}
		
	}

	public List<Long> findAllIds() throws SQLException, DataAccessException {
		return jdbc.query(FIND_ALL, MapUtil::mapRowToId);
	}

	public List<Icon> findAllIcons() {
		return jdbc.query(FIND_ALL, new IconMapper());
	}
}

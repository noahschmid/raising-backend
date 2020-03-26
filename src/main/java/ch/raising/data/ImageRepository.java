 package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.management.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.models.Image;
import ch.raising.utils.MapUtil;
import ch.raising.utils.PreparedStatementGetter;
import ch.raising.utils.PreparedStatementUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.RowMapper;

@Repository
public class ImageRepository {
	
	JdbcTemplate jdbc;
	private String tableName;
	private final PreparedStatementGetter<PreparedStatementCallback<Boolean>,Image, Long> preps;
	private final PreparedStatementGetter<PreparedStatementCallback<Boolean>, Long, Long> psDelete;
	private final RowMapper<ResultSet, Integer, Image> rowMapper;
	
	@Autowired
	public ImageRepository(JdbcTemplate jdbc, String tableName) {
		this.jdbc = jdbc;
		this.tableName = tableName;
		this.preps = PreparedStatementUtil::addImageByIdCallback;
		this.psDelete = PreparedStatementUtil::deleteByIdAndAccountIdCallback;
		this.rowMapper = MapUtil::mapRowToImage;
	}
	
	public void addImageToAccount(Image img, long accountId) throws DataAccessException, SQLException {
		String sql = QueryBuilder.getInstance().tableName(tableName).attribute("accountid, image").qMark().qMark().insert();
		jdbc.execute(sql, preps.getCallback(img, accountId));
	}
	
	public List<Image> findImagesByAccountId(long id) {
		String sql = QueryBuilder.getInstance().tableName(tableName).whereEqualsQmark("accountid").select();
		return jdbc.query(sql, new Object[] { id }, rowMapper::mapRowToModel);
	}
	
	public void deleteImageFromAccount(long imageId, long accountId) throws DataAccessException, SQLException {
		String sql = QueryBuilder.getInstance().tableName(tableName).whereEqualsQmark("id").whereEqualsQmark("accountid").delete();
		jdbc.execute(sql, psDelete.getCallback(imageId, accountId));
	}
	
	public long containsPictureOf(long accountId) {
		String sql = QueryBuilder.getInstance().tableName(tableName).whereEqualsQmark("accountid").select();
		return jdbc.queryForObject(sql, new Object[] {accountId}, MapUtil::mapRowToId);
		
	}
}

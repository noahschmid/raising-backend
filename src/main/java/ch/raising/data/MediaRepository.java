 package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.Media;
import ch.raising.utils.MapUtil;
import ch.raising.utils.PreparedStatementUtil;
import ch.raising.utils.QueryBuilder;
import ch.raising.utils.functionalInterface.PSOneParameter;
import ch.raising.utils.functionalInterface.PSTwoParameters;
import ch.raising.utils.functionalInterface.RowMapper;


public class MediaRepository implements IMediaRepository<Media> {
	
	JdbcTemplate jdbc;
	private final PSTwoParameters<PreparedStatementCallback<Boolean>, Media, Long> PS_ADD;
	private final PSOneParameter<PreparedStatementCallback<Boolean>, byte[]> PS_ADD_BYTES;
	private final PSTwoParameters<PreparedStatementCallback<Boolean>, Long, Long> PS_DELETE;
	private final RowMapper<ResultSet, Integer, Media> rowMapper;
	
	private final String INSERT_WITHOUT_ACCOUNTID;
	private final String INSERT_WITH_ACCOUNTID;
	private final String FIND_BY_ACCOUNTID;
	private final String DELETE_ENTRY_FROM_ACCOUNT;
	private final String GET_MEDIA_ID_OF;
	private final String FIND_BY_ID;
	private final String ADD_ACCOUNTID_TO_MEDIA;
	
	public MediaRepository(JdbcTemplate jdbc, String tableName) {
		this.jdbc = jdbc;
		this.PS_ADD = PreparedStatementUtil::addMediaByIdCallback;
		this.PS_ADD_BYTES = PreparedStatementUtil::addBytesCallback;
		this.PS_DELETE = PreparedStatementUtil::deleteMediaByIdAndAccountIdCallback;
		this.rowMapper = MapUtil::mapRowToMedia;
		this.INSERT_WITHOUT_ACCOUNTID = "INSERT INTO " + tableName + "(media) VALUES (?);" ;
		this.INSERT_WITH_ACCOUNTID = "INSERT INTO " + tableName + "(accountid, media) VALUES (?, ?)";
		this.FIND_BY_ACCOUNTID = "SELECT * FROM " + tableName + " WHERE accountid = ?";
		this.DELETE_ENTRY_FROM_ACCOUNT = "DELETE FROM " + tableName + " WHERE id = ? AND accountId = ?";
		this.GET_MEDIA_ID_OF = "SELECT id FROM " + tableName + " WHERE accountId = ?";
		this.FIND_BY_ID = "SELECT * FROM " + tableName + " WHERE id = ?";
		this.ADD_ACCOUNTID_TO_MEDIA = "UPDATE " + tableName + " SET accountid = ? WHERE id = ?";
	}
	
	@Override
	public void addMediaToAccount(Media img, long accountId) throws DataAccessException, SQLException {
		jdbc.execute(INSERT_WITH_ACCOUNTID, PS_ADD.getCallback(img, accountId));
	}
	
	@Override
	public long addMediaBytes(byte[] media) throws DataAccessException, SQLException {
		KeyHolder kh = new GeneratedKeyHolder();
		jdbc.update(INSERT_WITHOUT_ACCOUNTID, PS_ADD_BYTES.getCallback(media), kh);
		return kh.getKey().longValue();
	}
	
	@Override
	public List<Media> findMediaByAccount(long id) {
		return jdbc.query(FIND_BY_ACCOUNTID, new Object[] { id }, rowMapper::mapRowToModel);
	}
	
	@Override
	public void deleteMediaFromAccount(long imageId, long accountId) throws DataAccessException, SQLException {
		jdbc.execute(DELETE_ENTRY_FROM_ACCOUNT, PS_DELETE.getCallback(imageId, accountId));
	}
	
	@Override
	public long getMediaIdOf(long accountId) {
		return jdbc.queryForObject(GET_MEDIA_ID_OF, new Object[] {accountId}, MapUtil::mapRowToId);
		
	}

	@Override
	public Media findMediaById(long mediaId) throws DataAccessException, SQLException {
		return jdbc.queryForObject(FIND_BY_ID, rowMapper::mapRowToModel);
	}
	
	@Override
	public void addAccountIdToMedia(long accountId, long videoId) throws DataAccessException{
		jdbc.update(ADD_ACCOUNTID_TO_MEDIA, new Object[] {accountId, videoId});
	}

	
}

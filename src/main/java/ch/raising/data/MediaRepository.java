 package ch.raising.data;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IMediaRepository;
import ch.raising.models.Media;
import ch.raising.utils.DatabaseOperationException;
import ch.raising.utils.MapUtil;
import ch.raising.utils.MediaNotAddedException;
import ch.raising.utils.PreparedStatementUtil;
import ch.raising.utils.functionalInterface.PSTwoParameters;
import ch.raising.utils.functionalInterface.RowMapper;

public class MediaRepository implements IMediaRepository<Media> {
	
	
	private final JdbcTemplate jdbc;
	private final PSTwoParameters<PreparedStatementCallback<Boolean>, Media, Long> PS_ADD;
	private final PSTwoParameters<PreparedStatementCallback<Boolean>, Long, Long> PS_DELETE;
	private final RowMapper<ResultSet, Integer, Media> rowMapper;
	
	private final String INSERT_Media;
	private final String FIND_BY_ACCOUNTID;
	private final String DELETE_ENTRY_FROM_ACCOUNT;
	private final String GET_MEDIA_ID_OF;
	private final String FIND_BY_ID;
	private final String ADD_ACCOUNTID_TO_MEDIA;
	private final String FIND_ID_BY_ACCOUNT_ID;
	private final String COUNT_MEDIA_OF_ACCOUNT;
	private final String UPDATE_MEDIA;
	
	public MediaRepository(JdbcTemplate jdbc, String tableName) {
		this.PS_ADD = PreparedStatementUtil::addMediaByIdCallback;
		this.PS_DELETE = PreparedStatementUtil::setIdAccountIdCallback;
		this.rowMapper = MapUtil::mapRowToMedia;
		this.INSERT_Media = "INSERT INTO " + tableName + "(type, media, accountid) VALUES (?,?, ?)" ;
		this.FIND_BY_ACCOUNTID = "SELECT * FROM " + tableName + " WHERE accountid = ?";
		this.DELETE_ENTRY_FROM_ACCOUNT = "DELETE FROM " + tableName + " WHERE id = ? AND accountid = ?";
		this.GET_MEDIA_ID_OF = "SELECT id FROM " + tableName + " WHERE accountId = ?";
		this.FIND_BY_ID = "SELECT * FROM " + tableName + " WHERE id = ?";
		this.FIND_ID_BY_ACCOUNT_ID = "SELECT id FROM " + tableName + " WHERE accountid = ?";
		this.ADD_ACCOUNTID_TO_MEDIA = "UPDATE " + tableName + " SET accountid = ? WHERE id = ?";
		this.COUNT_MEDIA_OF_ACCOUNT = "SELECT COUNT(id) FROM "+ tableName+" WHERE accountid = ?";
		this.UPDATE_MEDIA = "UPDATE " + tableName + " SET media = ?, type = ? where id = ? AND accountid = ?";
		this.jdbc = jdbc;
	}
	
	@Override
	public long addMedia(Media media) throws DataAccessException, SQLException, DatabaseOperationException {
		
		PreparedStatement ps = jdbc.getDataSource().getConnection().prepareStatement(INSERT_Media, Statement.RETURN_GENERATED_KEYS);
		int c = 1;
		ps.setString(c++, media.getContentType());
		ps.setBytes(c++, media.getMedia());
		if(media.getAccountId() > 0)
			ps.setLong(c++, media.getAccountId());
		else
			ps.setNull(c++, java.sql.Types.BIGINT);
		if(ps.executeUpdate() >0) {
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				long insertedId = rs.getLong(1);
				ps.getConnection().close();
				return insertedId;
			}
		}
		throw new DatabaseOperationException("Generated Key could not be retreived");
	}
	
	@Override
	public List<Media> findMediaByAccountId(long id) {
		return jdbc.query(FIND_BY_ACCOUNTID, new Object[] { id }, rowMapper::mapRowToModel);
	}
	
	@Override
	public void deleteMediaFromAccount(long mediaId, long accountId) throws DataAccessException, SQLException {
		jdbc.execute(DELETE_ENTRY_FROM_ACCOUNT, PS_DELETE.getCallback(mediaId, accountId));
	}
	
	@Override
	public long getMediaIdOf(long accountId) {
		return jdbc.queryForObject(GET_MEDIA_ID_OF, new Object[] {accountId}, MapUtil::mapRowToId);
		
	}

	@Override
	public Media findMediaById(long mediaId) throws DataAccessException, SQLException {
		return jdbc.queryForObject(FIND_BY_ID,new Object[] {mediaId}, rowMapper::mapRowToModel);
	}
	
	@Override
	public void addAccountIdToMedia(long videoId, long accountId) throws DataAccessException{
		if(videoId != -1 && videoId != 0) {
			jdbc.update(ADD_ACCOUNTID_TO_MEDIA, new Object[] {accountId, videoId});
		}
	}

	@Override
	public List<Long> findMediaIdByAccountId(long accountId) throws DataAccessException, SQLException {
		return jdbc.query(FIND_ID_BY_ACCOUNT_ID, new Object[] { accountId}, MapUtil::mapRowToId);
	}

	@Override
	public long countMediaOfAccount(long accountId) throws SQLException, DataAccessException{
		return jdbc.queryForObject(COUNT_MEDIA_OF_ACCOUNT, new Object[] {accountId}, MapUtil::mapRowToFirstEntry);
	}

	@Override
	public void updateMedia(Media media) throws SQLException, DataAccessException, MediaNotAddedException{
		if(media.getAccountId() < 0 || media.getId() < 0)
			throw new MediaNotAddedException("No id or accountid found");
		jdbc.execute(UPDATE_MEDIA, new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				ps.setBytes(c++, media.getMedia());
				ps.setString(c++, media.getContentType());
				ps.setLong(c++, media.getId());
				ps.setLong(c++, media.getAccountId());
				return ps.execute();
			}
				
		});
	}

	
}

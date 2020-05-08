package ch.raising.data;

import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ch.raising.models.Settings;
import ch.raising.models.enums.Device;
import ch.raising.models.enums.NotificationType;
import ch.raising.utils.MapUtil;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class SettingRepository {

	private final JdbcTemplate jdbc;
	private final static String INSERT_DEVICE_TOKEN = "INSERT INTO settings(accountid, token, device, notificationtypes, language, numberofmatches) VALUES (?,?,?,?,?,?)";
	private final static String SELECT_BY_ACCOUNTID = "SELECT * FROM settings WHERE accountid = ?;";
	private final static String FIND_NOTIFICATIONSETTINGS_BY_ACCOUNTID = "SELECT token, notificationtypes FROM settings WHERE accountid = ?";

	@Autowired
	public SettingRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void addSettings(Settings settings) throws DataAccessException, SQLException {
		String notificationTypes = parseToString(settings.getNotificationTypes());
		String device = settings.getDevice() == null ? "" : settings.getDevice().name();
		jdbc.update(INSERT_DEVICE_TOKEN,
				new Object[] { settings.getAccountId(), settings.getToken(), device, notificationTypes,
						settings.getLanguage(), settings.getNumberOfMatches() },
				new int[] { Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER });
	}

	public Settings findByAccountId(long accountId) throws SQLException, DataAccessException {
		return jdbc.queryForObject(SELECT_BY_ACCOUNTID, new Object[] { accountId }, new SettingMapper());
	}

	public void update(Settings token, long accountId) throws SQLException, DataAccessException {
		UpdateQueryBuilder update = new UpdateQueryBuilder(jdbc, "settings", accountId, "accountid");
		update.addField(token.getToken(), "token");
		update.addField(token.getDevice(), "device");
		update.addField(parseToString(token.getNotificationTypes()), "notificationtypes");
		update.addField(token.getLanguage(), "language");
		update.addField(token.getNumberOfMatches(), "numberofmatches");
		update.execute();
	}

	private String parseToString(List<NotificationType> types) {
		if (types == null)
			return NotificationType.NEVER.name();
		String result = "";
		for (NotificationType t : types) {
			result += t.name() + ", ";
		}
		return result.substring(0, result.length() - 2);
	}

	private List<NotificationType> parseToEnum(String strings) {
		List<NotificationType> types = new ArrayList<NotificationType>();
		for (String s : strings.split(",")) {
			types.add(NotificationType.valueOf(s.trim()));
		}
		return types;
	}

	public boolean hasSettings(long accountId) {
		long id = -1;
		try {
			id = jdbc.queryForObject(SELECT_BY_ACCOUNTID, new Object[] { accountId }, MapUtil::mapRowToAccountId);
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
		return id != -1;
	}

	public Settings findInfoByAccountId(long accountId) throws SQLException, DataAccessException {
		return jdbc.queryForObject(FIND_NOTIFICATIONSETTINGS_BY_ACCOUNTID, new Object[] { accountId },
				new NotificationSettingMapper());
	}

	private class NotificationSettingMapper implements RowMapper<Settings> {
		
		@Override
		public Settings mapRow(ResultSet rs, int rowNum) throws SQLException {
			return Settings.builder().token(rs.getString("token"))
					.notificationTypes(parseToEnum(rs.getString("notificationtypes"))).build();
		}

	}

	private class SettingMapper implements RowMapper<Settings> {

		@Override
		public Settings mapRow(ResultSet rs, int rowNum) throws SQLException {
			String numberOfMatches = rs.getString("numberofmatches") == null ? "1" : rs.getString("numberofmatches");
			return Settings.builder().accountId(rs.getLong("accountid")).device(Device.valueOf(rs.getString("device")))
					.notificationTypes(parseToEnum(rs.getString("notificationtypes"))).token(rs.getString("token"))
					.language(rs.getString("language")).numberOfMatches(Integer.parseInt(numberOfMatches)).build();
		}

	}

}

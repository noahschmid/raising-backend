package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ch.raising.models.DeviceToken;
import ch.raising.models.enums.Device;
import ch.raising.models.enums.NotificationType;
import ch.raising.utils.UpdateQueryBuilder;

@Repository	
public class NotificationRepository {

	
	private final JdbcTemplate jdbc;
	private final String INSERT_DEVICE_TOKEN;
	private final String SELECT_BY_ACCOUNTID;
	
	@Autowired
	public NotificationRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.INSERT_DEVICE_TOKEN = "INSERT INTO notification(accountid, token, device, notificationtypes) VALUES (?,?,?,?)";
		this.SELECT_BY_ACCOUNTID = "SELECT * FROM notification WHERE accountid = ?;";
	}
	
	public void addNewDeviceToken(DeviceToken token) throws DataAccessException, SQLException{
		String notificationTypes = parseToString(token.getNotificationTypes());
		jdbc.update(INSERT_DEVICE_TOKEN, new Object[] {token.getAccountId(), token.getToken(), token.getDevice().name(), notificationTypes }, new int[] {Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});
	}
	
	public DeviceToken findDeviceTokenByAccountId(long accountId) {
		
		return jdbc.queryForObject(SELECT_BY_ACCOUNTID, new DeviceTokenMapper());
	}
	
	public void update(DeviceToken token, long accountId) throws SQLException, DataAccessException{
		UpdateQueryBuilder update = new UpdateQueryBuilder(jdbc, "notification", accountId, "accountid");
		update.addField(token.getToken(), "token");
		update.addField(token.getDevice(), "device");
		update.addField(parseToString(token.getNotificationTypes()), "notificationtypes");
		update.execute();
	}
	private String parseToString(List<NotificationType> types) {
		String result = "";
		for(NotificationType t: types) {
			result += t.name() +", ";
		}
		return result.substring(0, result.length()-2);
	}
	private List<NotificationType> parseToEnum(String strings) {
		List<NotificationType> types = new ArrayList<NotificationType>();
		for(String s: strings.split(",")) {
			types.add(NotificationType.valueOf(s.trim()));
		}
		return types;
	}
	
	private class DeviceTokenMapper implements RowMapper<DeviceToken>{

		@Override
		public DeviceToken mapRow(ResultSet rs, int rowNum) throws SQLException {
			return DeviceToken.builder()
					.id(rs.getLong("id"))
					.accountId(rs.getLong("accountid"))
					.device(Device.valueOf(rs.getString("device")))
					.notificationTypes(parseToEnum(rs.getString("notificationtypes")))
					.build();
		}
		
	}
}

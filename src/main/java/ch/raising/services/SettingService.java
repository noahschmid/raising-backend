package ch.raising.services;

import java.sql.SQLException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.SettingRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.Settings;
/**
 * service for managing settings
 * @see SettingRepository
 * @author manus
 *
 */
@Service
public class SettingService {

	private final SettingRepository settingRepo;

	@Autowired
	public SettingService(SettingRepository settingRepo) {
		this.settingRepo = settingRepo;
	}
	/**
	 * 
	 * @return {@link Settings} of that account
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public Settings getSettings() throws DataAccessException, SQLException {
		return settingRepo.findByAccountId(getAccountId());
	}
	/**
	 * 
	 * @param token {@link Settings} that are updated
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	public void update(Settings token) throws SQLException, DataAccessException {
		if(settingRepo.hasSettings(getAccountId()))
			settingRepo.update(token, getAccountId());
		else 
			settingRepo.addSettings(getAccountId(), token);
	}

	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}
	/**
	 * if a user logs out he does not want any notifications
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	public void deleteNotificationToken() throws DataAccessException, SQLException {
		settingRepo.setDevicetokenNull(getAccountId());
	}

}

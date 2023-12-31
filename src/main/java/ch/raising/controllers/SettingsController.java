package ch.raising.controllers;

import java.sql.SQLException;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.PushNotification;
import ch.raising.models.Settings;
import ch.raising.models.enums.NotificationType;
import ch.raising.services.NotificationService;
import ch.raising.services.SettingService;

/**
 * Class that is responsible to provide endpoints for getting and saving
 * settings. The devicetoken for PushNotifiactions is saved here
 * 
 * @author manus
 *
 */
@Controller
@RequestMapping("/settings")
public class SettingsController {

	private final NotificationService notificationService;
	private final SettingService settingService;

	public SettingsController(NotificationService notificationService, SettingService settingService) {
		this.settingService = settingService;
		this.notificationService = notificationService;
	}
	/**
	 * 
	 * @param dtr {@link Settings} 
	 * * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	@PatchMapping
	public ResponseEntity<?> addOrUpdateDevice(@RequestBody Settings dtr) throws DataAccessException, SQLException {
		settingService.update(dtr);
		return ResponseEntity.ok().build();
	}
	/**
	 * 
	 * * @return ResponseEntity with status code 200 and an {@link Settings} object for the user or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	@GetMapping
	public ResponseEntity<?> getSettings() throws DataAccessException, SQLException {
		return ResponseEntity.ok(settingService.getSettings());
	}
	/**
	 * 
	 * @return ResponseEntity with status code 200 or a response according to
	 *         {@link ControllerExceptionHandler}
	 * @throws DataAccessException
	 * @throws SQLException
	 */
	@PatchMapping("/deletetoken")
	public ResponseEntity<?> deleteDeviceToken() throws DataAccessException, SQLException {
		settingService.deleteNotificationToken();
		return ResponseEntity.ok().build();
	}

}

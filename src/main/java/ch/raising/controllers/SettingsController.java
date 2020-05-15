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

@Controller
@RequestMapping("/settings")
public class SettingsController {
	
	private final NotificationService notificationService;
	private final SettingService settingService;

	public SettingsController(NotificationService notificationService, SettingService settingService) {
		this.settingService = settingService;
		this.notificationService = notificationService;
	}
	
	@PatchMapping
	public ResponseEntity<?> addOrUpdateDevice(@RequestBody Settings dtr) throws DataAccessException, SQLException{
		settingService.update(dtr);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping
	public ResponseEntity<?> getSettings() throws DataAccessException, SQLException{
		return ResponseEntity.ok(settingService.getSettings());
	}
	
	@PatchMapping("/deletetoken")
	public ResponseEntity<?> deleteDeviceToken() throws DataAccessException, SQLException{
		settingService.deleteNotificationToken();
		return ResponseEntity.ok().build();
	}

}

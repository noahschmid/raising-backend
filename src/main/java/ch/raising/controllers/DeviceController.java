package ch.raising.controllers;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.raising.models.DeviceToken;
import ch.raising.models.PushNotification;
import ch.raising.models.enums.NotificationType;
import ch.raising.services.NotificationService;

@Controller
@RequestMapping("/device")
public class DeviceController {
	
	private final NotificationService notificationService;

	public DeviceController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
	
	@PostMapping
	public ResponseEntity<?> registerDevice(@RequestBody DeviceToken dtr) throws DataAccessException, SQLException{
		notificationService.registerDeviceToken(dtr);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/send")
	public ResponseEntity<?> registerDevice(@RequestBody PushNotification dtr) throws InterruptedException, ExecutionException{
		notificationService.sendTestNotificationToSelf(dtr.getAccountid(), dtr.getMessage(), dtr.getTitle());
		return ResponseEntity.ok().build();
	}
	
	@PatchMapping
	public ResponseEntity<?> updateDevice(@RequestBody DeviceToken dtr) throws DataAccessException, SQLException{
		notificationService.update(dtr);
		return ResponseEntity.ok().build();
	}

}

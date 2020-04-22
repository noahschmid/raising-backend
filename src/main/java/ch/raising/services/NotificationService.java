package ch.raising.services;

import java.security.acl.NotOwnerException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ch.raising.data.NotificationRepository;
import ch.raising.models.AccountDetails;
import ch.raising.models.DeviceToken;
import ch.raising.models.enums.NotificationType;

@Service
public class NotificationService {

	private final NotificationRepository notificationRepo;
	
	@Autowired
	public NotificationService(NotificationRepository notificationRepo) {
		this.notificationRepo = notificationRepo;
	}

	public void registerDeviceToken(DeviceToken token) throws SQLException, DataAccessException{
		token.setAccountId(getAccountId());
		notificationRepo.addNewDeviceToken(token);
	}
	
	public void update(DeviceToken token) throws SQLException, DataAccessException{
		if(token.getNotificationTypes().contains(NotificationType.NEVER))
			token.setNotificationTypes(Arrays.asList(NotificationType.NEVER));
		notificationRepo.update(token,getAccountId());
	}
	
	
	private long getAccountId() {
		return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
	}
}

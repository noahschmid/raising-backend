package ch.raising.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IOSSubscriptionRepository {

	private final JdbcTemplate jdbc;
	
	@Autowired
	public IOSSubscriptionRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
}

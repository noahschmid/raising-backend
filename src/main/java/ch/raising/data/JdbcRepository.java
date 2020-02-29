package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.raising.models.User;

@Repository
public class JdbcRepository implements IJdbcRepository{
	
	private JdbcTemplate jdbc;
	
	@Autowired
	public JdbcRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public List<User> findALL() {
		String getAll = "SELECT id, username, password FROM account";
		List<User> users = jdbc.query(getAll, this::mapRowToUser);
		return users;
	}
	
	private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
		return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
	}

}

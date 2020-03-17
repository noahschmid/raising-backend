package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceRepository {

	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	public FinanceRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	/**
	 * 
	 * @return a list of all possible corporate bodies
	 */
	public List<String> getAll(){
		return jdbc.query("SELECT * FROM finance", this::mapRowToModel);
	}
	
	private String mapRowToModel(ResultSet rs, int row) throws SQLException {
		return rs.getString("type");
	}
}

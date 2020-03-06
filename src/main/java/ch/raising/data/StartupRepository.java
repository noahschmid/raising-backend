package ch.raising.data;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ch.raising.models.Startup;

@Repository
public class StartupRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public StartupRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

	public Startup getStartupById(int id) {
		String sql = "SELECT * FROM account WHERE id = ?";
		Object[] ps = new Object[] {id};
		return jdbc.queryForObject(sql,ps,this::mapRowToStartup);
	}
	
	
	private Startup mapRowToStartup(ResultSet rs, int row) {
		assert 0 == 1 ;
		return null;
		
	}
}
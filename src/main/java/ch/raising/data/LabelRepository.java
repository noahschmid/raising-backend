package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import ch.raising.models.Label;

public class LabelRepository implements IRepository{
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	public LabelRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Label find(int id) {
		String sql = "SELECT * FROM label WHERE id=?";
		return jdbc.queryForObject(sql ,new Object[] {id}, this::mapRowToLabel);
	}

	@Override
	public void update(int id, Object updateRequest) throws Exception {
		throw new Exception("Not updateable");
	}
	
	private Label mapRowToLabel(ResultSet rs, int row) throws SQLException {
		return new Label(rs.getInt("id"), rs.getString("name"), rs.getString("descritpion"));
	}
	
	public void addLabelToStartup(int labelId, int suId) {
		String sql = "INSERT INTO labelassignment(startupid, labelid) VALUES (?,?)";
		jdbc.execute(sql,new PreparedStatementCallback<Boolean>(){  
			@Override  
			public Boolean doInPreparedStatement(PreparedStatement ps)  
					throws SQLException, DataAccessException {  
					
				ps.setInt(1, suId);  
				ps.setInt(2, labelId);
					
				return ps.execute();  
			}  
		});  
	}
	

}

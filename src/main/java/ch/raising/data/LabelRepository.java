package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.models.Contact;
import ch.raising.models.Label;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class LabelRepository implements IRepository<Label, UpdateQueryBuilder>{
	
	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	public LabelRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	/**
	 * 
	 * @return a List of all labels
	 */
	public List<Label> getAllLabels() {
		String sql = "SELECT * FROM label";
		return jdbc.query(sql, this::mapRowToLabel);
	}

	@Override
	public Label find(long id) {
		String sql = "SELECT * FROM label WHERE id=?";
		return jdbc.queryForObject(sql ,new Object[] {id}, this::mapRowToLabel);
	}
	
	private Label mapRowToLabel(ResultSet rs, int row) throws SQLException {
		return new Label(rs.getInt("id"), rs.getString("name"), rs.getString("descritpion"));
	}
	
	/**
	 * creates an entry in the labelassignmenttable with the specified ids
	 * @param labelId
	 * @param suId
	 */
	public void addLabelToStartup(long labelId, long suId) {
		String sql = "INSERT INTO labelassignment(startupid, labelid) VALUES (?,?)";
		jdbc.execute(sql,new PreparedStatementCallback<Boolean>(){  
			@Override  
			public Boolean doInPreparedStatement(PreparedStatement ps)  
					throws SQLException, DataAccessException {  
					
				ps.setLong(1, suId);  
				ps.setLong(2, labelId);
					
				return ps.execute();  
			}  
		});  
	}

	@Override
	public void update(long id, UpdateQueryBuilder updateRequest) throws Exception {
		throw new Exception("not implemented");
	}

	/**
	 * deletes the entries of labelassignment table containing both ids
	 * @param labelId
	 * @param suId
	 */
	public void deleteLabelOfStartup(long labelId, long suId) {
		String sql = "DELETE FROM labelassignment WHERE label.id = ? label.startupid = ?";
		jdbc.execute(sql, new PreparedStatementCallback<Boolean>(){
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException{
				ps.setLong(1, labelId);
				ps.setLong(2, suId);
				
				return ps.execute();
			}
		
		});
	}
}

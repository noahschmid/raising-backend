package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import ch.raising.models.Founder;
import ch.raising.utils.UpdateQueryBuilder;

public class FounderRepository implements IAdditionalInformationRepository<Founder, UpdateQueryBuilder> {
	
	@Autowired
	JdbcTemplate jdbc;
	@Autowired
	public FounderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void deleteFounder(int id) {
		jdbc.execute("DELETE FROM founder WHERE founder.id = ?", new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setInt(1, id);
				return ps.execute();
			}
		});
	}

	public void addFounder(Founder founder) {
		jdbc.execute("INSERT INTO founder(id ,startupid, name, role, education) VALUES (?,?,?,?,?,?,?)", 
				new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ps.setInt(1, founder.getId());
					ps.setInt(2, founder.getStartupId());
					ps.setString(3, founder.getName());
					ps.setString(4, founder.getRole());
					ps.setString(5, founder.getEducation());
					return ps.execute();
				}
		});

	}

	@Override
	public int getStartupIdOfTableById(int founderId) {
		return jdbc.queryForObject("SELECT startupid FROM founder WHERE id = ?", new Object[] {founderId}, this::mapRowToId);
	}

	@Override
	public Founder find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(int id, UpdateQueryBuilder updateRequest) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEntry(Founder sumem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteEntry(int id) {
		// TODO Auto-generated method stub
		
	}
}

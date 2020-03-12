package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.models.Founder;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class FounderRepository implements IAdditionalInformationRepository<Founder, UpdateQueryBuilder> {
	
	@Autowired
	JdbcTemplate jdbc;
	@Autowired
	public FounderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void deleteFounderByStartupId(long id) {
		jdbc.execute("DELETE FROM founder WHERE founder.id = ?", new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, id);
				return ps.execute();
			}
		});
	}

	public void addFounderByStartupId(Founder founder) {
		jdbc.execute("INSERT INTO founder(id ,startupid, name, role, education) VALUES (?,?,?,?,?,?,?)", 
				new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ps.setLong(1, founder.getId());
					ps.setLong(2, founder.getStartupId());
					ps.setString(3, founder.getName());
					ps.setString(4, founder.getRole());
					ps.setString(5, founder.getEducation());
					return ps.execute();
				}
		});

	}

	@Override
	public long getStartupIdOfTableById(long founderId) {
		return jdbc.queryForObject("SELECT startupid FROM founder WHERE id = ?", new Object[] {founderId}, this::mapRowToId);
	}

	@Override
	public Founder find(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(long id, UpdateQueryBuilder updateRequest) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEntry(Founder sumem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteEntry(long id) {
		// TODO Auto-generated method stub
		
	}
}

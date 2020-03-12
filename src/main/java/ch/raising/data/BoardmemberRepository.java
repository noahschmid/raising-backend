package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import ch.raising.models.Boardmember;
import ch.raising.utils.UpdateQueryBuilder;

public class BoardmemberRepository implements IAdditionalInformationRepository<Boardmember, UpdateQueryBuilder>{

	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	public BoardmemberRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void deleteBoardMember(int id) {
		jdbc.execute("DELETE FROM boardmemeber WHERE boardmember.id = ?", new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setInt(1, id);
				return ps.execute();
			}
		});
	}

	public void addBoardMember(Boardmember bmem) {
		jdbc.execute("INSERT INTO boardmember(id ,startupid, name, education, profession, pulldowntype, pulldownduration) VALUES (?,?,?,?,?,?,?)", 
				new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ps.setInt(1, bmem.getId());
					ps.setInt(2,bmem.getStartupId());
					ps.setString(3, bmem.getName());
					ps.setString(4, bmem.getEducation());
					ps.setString(5, bmem.getProfession());
					ps.setString(6, bmem.getPullDownType());
					ps.setInt(7, bmem.getPullDownDuration());
					return ps.execute();
				}
		});

	}

	@Override
	public int getStartupIdOfTableById(int bmemId) {
		return jdbc.queryForObject("SELECT startupid FROM boardmember WHERE id = ?", new Object[] {bmemId}, this::mapRowToId);
	}

	@Override
	public Boardmember find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(int id, UpdateQueryBuilder updateRequest) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEntry(Boardmember sumem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteEntry(int id) {
		// TODO Auto-generated method stub
		
	}

}

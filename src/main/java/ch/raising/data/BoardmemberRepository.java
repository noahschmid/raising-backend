package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.models.Boardmember;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class BoardmemberRepository implements IAdditionalInformationRepository<Boardmember, UpdateQueryBuilder>{

	@Autowired
	JdbcTemplate jdbc;
	
	@Autowired
	public BoardmemberRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public void deleteBoardMemberByStartupId(long id) {
		jdbc.execute("DELETE FROM boardmemeber WHERE boardmember.id = ?", new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setLong(1, id);
				return ps.execute();
			}
		});
	}

	public void addBoardMemberByStartupId(Boardmember bmem) {
		jdbc.execute("INSERT INTO boardmember(id ,startupid, name, education, profession, pulldowntype, pulldownduration) VALUES (?,?,?,?,?,?,?)", 
				new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(PreparedStatement ps)
						throws SQLException, DataAccessException {
					ps.setLong(1, bmem.getId());
					ps.setLong(2,bmem.getStartupId());
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
	public long getStartupIdOfTableById(long bmemId) {
		return jdbc.queryForObject("SELECT startupid FROM boardmember WHERE id = ?", new Object[] {bmemId}, this::mapRowToId);
	}

	@Override
	public Boardmember find(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(long id, UpdateQueryBuilder updateRequest) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEntry(Boardmember sumem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteEntry(long id) {
		// TODO Auto-generated method stub
		
	}

}

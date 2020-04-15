package ch.raising.data;

import java.sql.PreparedStatement;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IAdditionalInformationRepository;
import ch.raising.models.Boardmember;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class BoardmemberRepository implements IAdditionalInformationRepository<Boardmember> {

	@Autowired
	private final JdbcTemplate jdbc;

	private final String ADD_MEMBER;
	private final String FIND_BY_STARTUP_ID;
	private final String FIND_BY_ID;
	private final String DELETE_MEMBER;

	@Autowired
	public BoardmemberRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.ADD_MEMBER = "INSERT INTO boardmember(startupid, firstname, lastname, education, profession, position, membersince, countryid) VALUES (?,?,?,?,?,?,?,?)";
		this.FIND_BY_STARTUP_ID = "SELECT * FROM boardmember WHERE startupid = ?";
		this.FIND_BY_ID = "SELECT * FROM boardmember WHERE id = ?";;
		this.DELETE_MEMBER = "DELETE FROM boardmember WHERE id = ?";
	}

	@Override
	public void addMemberByStartupId(Boardmember bmem, long startupid) throws SQLException, DataAccessException {
		jdbc.execute(ADD_MEMBER, getCallback(Arrays.asList(bmem), startupid));
	}

	@Override
	public long getStartupIdByMemberId(long bmemId) throws SQLException, DataAccessException {
		return jdbc.queryForObject(FIND_BY_ID, new Object[] { bmemId }, this::mapRowToId);
	}

	@Override
	public Boardmember find(long id) throws DataAccessException, SQLException {
		return jdbc.queryForObject(FIND_BY_ID, new Object[] { id }, this::mapRowToModel);
	}

	@Override
	public void deleteMemberById(long id) throws SQLException, DataAccessException {
		jdbc.execute(DELETE_MEMBER, deleteById(id));
	}

	@Override
	public void addMemberListByStartupId(List<Boardmember> models, long startupId)
			throws SQLException, DataAccessException {
		if(models == null || models.size() ==0)
			return;
		
		String sql = ADD_MEMBER;
		for (int i = 1; i < models.size(); i++) {
			sql += ",(?,?,?,?,?,?,?,?)";
		}
		jdbc.execute(sql, getCallback(models, startupId));

	}

	@Override
	public Boardmember mapRowToModel(ResultSet rs, int row) throws SQLException {
		return Boardmember.builder().id(rs.getLong("id")).startupId(rs.getLong("startupid"))
				.lastName(rs.getString("lastname")).firstName(rs.getString("firstname"))
				.position(rs.getString("position")).education(rs.getString("education"))
				.profession(rs.getString("profession")).memberSince(rs.getInt("membersince"))
				.coutryId(rs.getLong("countryId")).build();
	}
	
	@Override
	public PreparedStatementCallback<Boolean> getCallback(List<Boardmember> bmem, long accountId){
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c =1;
				
				for(Boardmember b: bmem) {
					ps.setLong(c++, accountId);
					ps.setString(c++, b.getFirstName());
					ps.setString(c++, b.getLastName());
					ps.setString(c++, b.getEducation());
					ps.setString(c++, b.getProfession());
					ps.setString(c++, b.getPosition());
					ps.setInt(c++, b.getMemberSince());
					ps.setLong(c++, b.getCountryId());
				}
				
				
				return ps.execute();
			}
		};
	}

	@Override
	public List<Boardmember> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM boardmember WHERE startupid = ?", new Object[] { startupId },
				this::mapRowToModel);
	}

	@Override
	public void update(long id, Boardmember req) throws DataAccessException, SQLException {
		UpdateQueryBuilder update = new UpdateQueryBuilder(jdbc, "boardmember", id);
		update.addField(req.getFirstName(), "firstname");
		update.addField(req.getLastName(), "lastname");
		update.addField(req.getEducation(), "education");
		update.addField(req.getProfession(), "profession");
		update.addField(req.getPosition(), "position");
		update.addField(req.getMemberSince(), "membersince");
		update.addField(req.getCountryId(), "countryId");
		update.execute();

	}

}

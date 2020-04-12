package ch.raising.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.interfaces.IAdditionalInformationRepository;
import ch.raising.models.CorporateShareholder;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class CorporateShareholderRepository implements IAdditionalInformationRepository<CorporateShareholder> {

	@Autowired
	private final JdbcTemplate jdbc;
	private final String ADD_MEMBER;
	private final String DELETE_MEMBER;
	private final String FIND_BY_STARTUP_ID;
	private final String FIND_BY_ID;

	@Autowired
	public CorporateShareholderRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
		this.ADD_MEMBER = "INSERT INTO corporateshareholder(startupid, name, website, equityshare, corporatebodyid, countryid) VALUES (?,?,?,?,?,?)";
		this.FIND_BY_STARTUP_ID = "SELECT * FROM corporateshareholder WHERE startupid = ?";
		this.DELETE_MEMBER = "DELETE FROM corporateshareholder WHERE startupid = ?";
		this.FIND_BY_ID = "SELECT * FROM corporateshareholder WHERE id = ?";
	}

	@Override
	public CorporateShareholder find(long id) throws SQLException, DataAccessException {
		return jdbc.queryForObject(FIND_BY_ID, new Object[] { id }, this::mapRowToModel);
	}

	@Override
	public CorporateShareholder mapRowToModel(ResultSet rs, int row) throws SQLException {
		return CorporateShareholder.builder().id(rs.getLong("id")).startupId(rs.getLong("startupid"))
				.corpName(rs.getString("name")).website(rs.getString("website")).equityShare(rs.getInt("equityshare"))
				.corporateBodyId(rs.getLong("corporatebodyid")).countryId(rs.getInt("countryid")).build();
	}

	@Override
	public long getStartupIdByMemberId(long id) throws SQLException, DataAccessException {
		return jdbc.queryForObject(FIND_BY_ID, new Object[] { id }, this::mapRowToId);
	}

	@Override
	public void addMemberByStartupId(CorporateShareholder sumem, long startupId)
			throws SQLException, DataAccessException {
		jdbc.execute(ADD_MEMBER, getCallback(Arrays.asList(sumem), startupId));
	}

	@Override
	public void deleteMemberById(long id) throws SQLException, DataAccessException {
		jdbc.execute(DELETE_MEMBER, deleteById(id));
	}

	@Override
	public void addMemberListByStartupId(List<CorporateShareholder> models, long startupId)
			throws SQLException, DataAccessException {
		String sql = ADD_MEMBER;
		for(int i = 1; i < models.size(); i++) {
			sql += ", (?,?,?,?,?,?)";
		}
		jdbc.execute(sql, getCallback(models, startupId));
	}

	@Override
	public PreparedStatementCallback<Boolean> getCallback(List<CorporateShareholder> cshList, long startupId) {
		return new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				int c = 1;
				for (CorporateShareholder csh : cshList) {
					ps.setLong(c++, startupId);
					ps.setString(c++, csh.getCorpName());
					ps.setString(c++, csh.getWebsite());
					ps.setInt(c++, csh.getEquityShare());
					ps.setLong(c++, csh.getCorporateBodyId());
					ps.setLong(c++, csh.getCountryId());
				}
				return ps.execute();
			}
		};
	}

	@Override
	public List<CorporateShareholder> findByStartupId(long startupId) {
		return jdbc.query("SELECT * FROM corporateshareholder WHERE startupid = ?", new Object[] { startupId },
				this::mapRowToModel);
	}

	@Override
	public void update(long id, CorporateShareholder req) throws SQLException, DataAccessException {
		UpdateQueryBuilder update = new UpdateQueryBuilder("corporateshareholder", id, jdbc);
		update.addField(req.getFirstName(), "firstname");
		update.addField(req.getLastName(), "lastname");
		update.addField(req.getCorpName(), "name");
		update.addField(req.getWebsite(), "website");
		update.addField(req.getEquityShare(), "equityshare");
		update.addField(req.getCorporateBodyId(), "corporatebodyid");
		update.addField(req.getCountryId(), "countryid");
		update.execute();
	}

}

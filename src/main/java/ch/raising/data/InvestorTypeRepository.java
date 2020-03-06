package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ch.raising.models.InvestorType;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class InvestorTypeRepository implements IRepository<InvestorType, InvestorType> {
    private JdbcTemplate jdbc;

    @Autowired
    public InvestorTypeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
	 * Find investor type by id
	 * @param id id of the desired investor type
	 * @return instance of the found investor type
	 */
	public InvestorType find(int id) {
		return jdbc.queryForObject("SELECT * FROM investorType WHERE id = ?", new Object[] { id }, this::mapRowToInvestorType);
	}

    /**
	 * Map a row of a result set to an InvestorType instance
	 * @param rs result set of an sql query
	 * @param rowNum row number in the result set
	 * @return InvestorType instance of the result set
	 * @throws SQLException
	 */
	private InvestorType mapRowToInvestorType(ResultSet rs, int rowNum) throws SQLException {
		return new InvestorType(rs.getInt("id"), 
            rs.getString("name"),
            rs.getString("description"));
	}

	/**
	 * Update industry
	 * @param id the id of the industry to update
	 * @param req request containing fields to update
	 */
	public void update(int id, InvestorType req) throws Exception {
		try {
			UpdateQueryBuilder updateQuery = new UpdateQueryBuilder("investorType", id, this);
			updateQuery.setJdbc(jdbc);
			updateQuery.addField(req.getName(), "name");
			updateQuery.addField(req.getDescription(), "description");
			updateQuery.execute();
		} catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
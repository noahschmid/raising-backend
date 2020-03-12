package ch.raising.data;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import ch.raising.models.ResetCode;
import ch.raising.utils.UpdateQueryBuilder;

@Repository
public class ResetCodeRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public ResetCodeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Find reset code entry by code
     * 
     * @param code the reset code
     * @return list of matching reset code entries
     */
    public ResetCode findByCode(String code) {
        try {
            String sql = "SELECT * FROM resetCode WHERE code = ?";
            Object[] ps = new Object[] { code };
            return jdbc.queryForObject(sql, ps, this::mapRowToResetCode);
        } catch (DataAccessException e) {
            return null;
        } 
    }

    /**
     * Find reset code entry by accountId
     * 
     * @param accountId id of the account to search for
     * @return list of matching reset code entries
     */
    public List<ResetCode> findByAccountId(long accountId) {
        try {
            String sql = "SELECT * FROM resetCode WHERE accountId = ?";
            Object[] ps = new Object[] { accountId };
            return jdbc.query(sql, ps, this::mapRowToResetCode);
        } catch (DataAccessException e) {
            return null;
        }
    }


    /**
     * Add new reset code to database
     * @param resetCode the code to add
     * @throws Exception
     */
    public void add(ResetCode resetCode) throws Exception {
        try {
            String query = "INSERT INTO resetCode(accountId, code, expiresAt) VALUES (?, ?, ?);";
            jdbc.execute(query, new PreparedStatementCallback<Boolean>() {
                @Override
                public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

                    ps.setLong(1, resetCode.getAccountId());
                    ps.setString(2, resetCode.getCode());
                    ps.setTimestamp(3, new Timestamp(resetCode.getExpiresAt().getTime()));
						
					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
    }

    /**
     * Map result set to ResetCode instance
     * @param rs the result set 
     * @param row the current row
     * @return ResetCode instance
     * @throws SQLException
     */
    private ResetCode mapRowToResetCode(ResultSet rs, int row) throws SQLException {
        return new ResetCode( 
            rs.getString("code"), 
            rs.getLong("accountId"), 
            rs.getTimestamp("expiresAt"));
    }

    /**
     * Delete reset code entries by given code
     * @param code the code to delete
     * @throws Exception
     */
    public void deleteByCode(String code) throws Exception {
		try {
			String query = "DELETE FROM resetCode WHERE code = ?;"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
					ps.setString(1, code); 	
					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}

    /**
     * Delete reset code by account id
     * @param accountId the account id 
     */
	public void deleteByAccountId(long accountId) {
        try {
			String query = "DELETE FROM resetCode WHERE accountId = ?;"; 
			jdbc.execute(query, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
						
					ps.setLong(1, accountId); 	
					return ps.execute();  
				}  
			});  
		} catch (Exception e) {
			System.out.println(e.toString());
			throw e;
		}
	}
}
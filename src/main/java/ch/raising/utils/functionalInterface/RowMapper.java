package ch.raising.utils.functionalInterface;

import java.sql.SQLException;

public interface RowMapper <ResultSet, Int, Model>{
	public Model mapRowToModel(ResultSet rs, Int n) throws SQLException;
}

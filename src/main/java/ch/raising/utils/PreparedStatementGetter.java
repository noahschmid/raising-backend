package ch.raising.utils;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;

public interface PreparedStatementGetter< PreparedStatementCallback, Model, Long> {
	public PreparedStatementCallback getCallback(Model m, Long id) throws SQLException, DataAccessException;
}

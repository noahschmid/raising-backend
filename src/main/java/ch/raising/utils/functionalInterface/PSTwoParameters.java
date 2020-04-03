package ch.raising.utils.functionalInterface;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;

public interface PSTwoParameters< PreparedStatementCallback, Model, Id> {
	public PreparedStatementCallback getCallback(Model m, Id id) throws SQLException, DataAccessException;
}

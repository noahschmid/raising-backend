package ch.raising.utils.functionalInterface;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;

public interface PSOneParameter<PreparedStatement, Data> {

	public PreparedStatementCallback<Boolean> getCallback(Data o) throws SQLException, DataAccessException;

}

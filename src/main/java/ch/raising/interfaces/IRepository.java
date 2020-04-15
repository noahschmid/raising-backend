package ch.raising.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;

import ch.raising.utils.DatabaseOperationException;

public interface IRepository<Model> {
    public Model find(long id) throws SQLException, DataAccessException, DatabaseOperationException;
    public void update(long id, Model req) throws SQLException, DataAccessException;
    public Model mapRowToModel(ResultSet rs, int row) throws SQLException;

}
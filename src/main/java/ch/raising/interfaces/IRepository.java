package ch.raising.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IRepository<Model, UpdateQueryBuilder> {
    public Model find(long id);
    public void update(long id, UpdateQueryBuilder updateRequest) throws Exception;
    public Model mapRowToModel(ResultSet rs, int row) throws SQLException;

}
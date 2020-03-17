package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IRepository<Model, UpdateRequest> {
    public Model find(long id);
    
    public void update(long id, UpdateRequest updateRequest) throws Exception;
    public Model mapRowToModel(ResultSet rs, int row) throws SQLException;

}
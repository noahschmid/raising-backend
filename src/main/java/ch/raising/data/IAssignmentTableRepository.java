package ch.raising.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IAssignmentTableRepository<Model>{
	public Model find(long id);
    public List<Model> getAll();
	public List<Model> findByAccountId(long id);
	public void addEntryToAccountById(long id, long accid);
	public void deleteEntryFromAccountById(long id, long accid);
	public Model mapRowToModel(ResultSet rs, int row) throws SQLException;
}

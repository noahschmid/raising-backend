package ch.raising.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IAssignmentTableRepository{
	public IAssignmentTableModel find(long id);
    public List<IAssignmentTableModel> findAll();
	public List<IAssignmentTableModel> findByAccountId(long id);
	public void addEntryToAccountById(long id, long accid);
	public void deleteEntryFromAccountById(long id, long accid);
	public IAssignmentTableModel mapRowToModel(ResultSet rs, int row) throws SQLException;
}

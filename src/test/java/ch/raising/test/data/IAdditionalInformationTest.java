package ch.raising.test.data;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;

public interface IAdditionalInformationTest {

	public void setup();
	public void addMember();
	public void cleanup();
	public void testGetStartupIdByMemberId()throws DataAccessException, SQLException;
	public void testAddMemberByStartupId()throws DataAccessException, SQLException;
	public void testDeleteMemberById() throws DataAccessException, SQLException;
	public void testFindByStartupId() throws DataAccessException, SQLException;
	public void testFind()throws DataAccessException, SQLException;
	public void testupdate() throws Exception;
}

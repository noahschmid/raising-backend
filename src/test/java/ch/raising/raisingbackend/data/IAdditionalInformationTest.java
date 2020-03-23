package ch.raising.raisingbackend.data;

public interface IAdditionalInformationTest {

	public void setup();
	public void addMember();
	public void cleanup();
	public void deleteMember();
	public void testGetStartupByMemberId();
	public void testAddMemberByStartupId();
	public void testDeleteMemberByStartupId();
	public void testFindByStartupId();
	public void testFind();
}

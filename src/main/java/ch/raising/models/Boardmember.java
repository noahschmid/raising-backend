package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Boardmember extends StartupMember {
	private String education;
	private String profession;
	private String position;
	private int membersince;
	private long countryId;
	
	@Builder
	public Boardmember(long id, long startupid, String firstName, String lastName, int membersince, String position, String profession, String education, long coutryId) {
		super(id, startupid, firstName, lastName);
		this.education = education;
		this.profession =profession;
		this.position = position;
		this.membersince = membersince;
		this.countryId = coutryId;
	}
}

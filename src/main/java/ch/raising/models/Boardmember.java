package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Boardmember extends StartupMember {
	private String education = "";
	private String profession = "";
	private String position = "";
	private int memberSince = -1;
	private long countryId = -1;

	@Builder
	public Boardmember(long id, long startupId, String firstName, String lastName, int memberSince, String position,
			String profession, String education, long coutryId) {
		super(id, startupId, firstName, lastName);
		this.education = education;
		this.profession = profession;
		this.position = position;
		this.memberSince = memberSince;
		this.countryId = coutryId;
	}
}

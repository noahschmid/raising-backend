package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class Boardmember extends StartupMember {
	private String education;
	private String profession;
	private String pullDownType;
	private int pullDownDuration;
	
	@Builder
	public Boardmember(long id, long startupid, String name, int pullDownDuration, String pullDownType, String profession, String education) {
		super(id, startupid, name);
		this.education = education;
		this.profession =profession;
		this.pullDownType = pullDownType;
		this.pullDownDuration = pullDownDuration;
	}
}

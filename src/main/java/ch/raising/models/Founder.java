package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Founder extends StartupMember{
	private String education;
	private String position;
	
	@Builder 
	public Founder(long startupid, long id, String firstName, String lastName, String education, String position) {
		super(id, startupid, firstName, lastName);
		this.education = education;
		this.position = position;
	}
}

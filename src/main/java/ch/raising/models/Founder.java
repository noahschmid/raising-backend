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
	private long countryId;
	
	@Builder 
	public Founder(long startupid, long id, String firstName, String lastName, String education, String position, long countryId) {
		super(id, startupid, firstName, lastName);
		this.education = education;
		this.position = position;
		this.countryId = countryId;
	}
}

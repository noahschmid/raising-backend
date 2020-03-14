package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class Founder extends StartupMember{
	private String education;
	private String role;
	@Builder 
	public Founder(long startupid, long id, String name, String education, String role) {
		super(id, startupid, name);
		this.education = education;
		this.role = role;
	}
}

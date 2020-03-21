package ch.raising.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartupMember {
	
	private long id = -1l;
	private long startupId;
	private String firstName;
	private String lastName;
}

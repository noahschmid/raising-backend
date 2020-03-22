package ch.raising.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartupMember {
	
	private long id = -1l;
	private long startupId;
	private String firstName;
	private String lastName;
}

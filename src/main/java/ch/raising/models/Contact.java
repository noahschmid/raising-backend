package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)

public class Contact extends StartupMember {
	private String position;
	private String email;
	private String phone;

	@Builder
	public Contact(long id, long startupid, String firstName, String lastName, String position, String email,
			String phone) {
		super(id, startupid, firstName, lastName);
		this.position = position;
		this.email = email;
		this.phone = phone;
	}
}

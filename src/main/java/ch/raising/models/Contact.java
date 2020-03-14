package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class Contact extends StartupMember{
	private String role;
	private String email;
	private String phone;
	
	@Builder
	public Contact(long id, long startupid, String name, String role, String email, String phone) {
		super(id, startupid, name);
		this.role = role;
		this.email =email;
		this.phone = phone;
	}
}

package ch.raising.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class Contact extends StartupMember{
	private String role;
	private String email;
	private String phone;
}

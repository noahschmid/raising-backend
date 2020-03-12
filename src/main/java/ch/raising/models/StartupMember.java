package ch.raising.models;

import lombok.Data;

@Data
public class StartupMember {
	private long id;
	private long startupId;
	private String name;
}

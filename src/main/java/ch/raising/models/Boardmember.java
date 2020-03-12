package ch.raising.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)

public class Boardmember extends StartupMember {
	private String education;
	private String profession;
	private String pullDownType;
	private int pullDownDuration;
}

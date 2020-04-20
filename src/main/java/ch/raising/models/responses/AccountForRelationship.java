package ch.raising.models.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountForRelationship {
	private long accountId;
	private String firstName;
	private String lastName;
	private String companyName;
	private long profilePictureId;
}

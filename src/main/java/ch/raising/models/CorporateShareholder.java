package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CorporateShareholder extends StartupMember {

	private String website;
	private int equityShare;
	private long corporateBodyId;
	private long countryId;

	@Builder
	private CorporateShareholder(long id, long startupId, long corporateBodyId, int equityShare, String website,
			String firstName, String lastName, long countryId) {
		super(id, startupId, firstName, lastName);
		this.website = website;
		this.equityShare = equityShare;
		this.corporateBodyId = corporateBodyId;
		this.countryId = countryId;
	}

}

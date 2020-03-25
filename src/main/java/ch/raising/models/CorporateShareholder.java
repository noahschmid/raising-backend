package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CorporateShareholder extends StartupMember {

	private String corpName;
	private String website;
	private int equityShare;
	private long corporateBodyId;
	private long countryId;

	@Builder
	private CorporateShareholder(long id, long startupId, long corporateBodyId, int equityShare, String corpName,
			String website, long countryId) {
		super(id, startupId, "", "");
		this.corpName = corpName;
		this.website = website;
		this.equityShare = equityShare;
		this.corporateBodyId = corporateBodyId;
		this.countryId = countryId;
	}

}

package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CorporateShareholder extends StartupMember{
	
	private  String name;
	private String website;
	private int equityShare;
	private long corporateBodyId;
	private long startupId;
	private long countryId;
	
	@Builder
	private CorporateShareholder(long id, long startupId, long corporateBodyId, int equityShare, String website, String name, long countryId) {
		super(id, startupId, null, null);
		this.name = name;
		this.website = website;
		this.equityShare = equityShare;
		this.corporateBodyId =corporateBodyId;
		this.startupId = startupId;
		this.countryId = countryId;
	}

}

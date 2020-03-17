package ch.raising.models;

import lombok.Builder;
import lombok.Data;

@Data
public class CorporateShareholder {
	
	private  String name;
	private String website;
	private int equityShare;
	private long corporateBodyId;
	private long startupId;
	
	@Builder
	private CorporateShareholder(long startupId, long corporateBodyId, int equityShare, String website, String name) {
		this.name = name;
		this.website = website;
		this.equityShare = equityShare;
		this.corporateBodyId =corporateBodyId;
		this.startupId = startupId;
	}

}

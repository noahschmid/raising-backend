package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PrivateShareholder extends StartupMember{

	private String city;
	private int equityShare;
	private long investortypeId;
	private long startupId;
	private long countryId;
	
	@Builder
	public PrivateShareholder(long id, String firstName, String lastName, String city, int equityShare, long startupId, long investortypeId, long countryId) {
		super(id, startupId, firstName, lastName);
		this.city = city;
		this.equityShare = equityShare;
		this.investortypeId = investortypeId;
		this.startupId = startupId;
		this.countryId = countryId;
	}
	
	
}

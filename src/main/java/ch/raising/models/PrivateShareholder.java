package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class PrivateShareholder extends StartupMember{

	private String city = "";
	private int equityShare = -1;
	private long investorTypeId = -1;
	private long countryId = -1;
	
	@Builder
	public PrivateShareholder(long id, String firstName, String lastName, String city, int equityShare, long startupid, long investorTypeId, long countryId) {
		super(id, startupid, firstName, lastName);
		this.city = city;
		this.equityShare = equityShare;
		this.investorTypeId = investorTypeId;
		this.countryId = countryId;
	}
	
	
}

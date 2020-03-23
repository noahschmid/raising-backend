package ch.raising.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class PrivateShareholder extends StartupMember{

	private String city;
	private int equityShare;
	private long investortypeId;
	private long countryId;
	
	@Builder
	public PrivateShareholder(long id, String firstName, String lastName, String city, int equityShare, long startupid, long investortypeId, long countryId) {
		super(id, startupid, firstName, lastName);
		this.city = city;
		this.equityShare = equityShare;
		this.investortypeId = investortypeId;
		this.countryId = countryId;
	}
	
	
}

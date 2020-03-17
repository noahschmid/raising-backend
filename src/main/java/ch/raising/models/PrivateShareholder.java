package ch.raising.models;

import lombok.Builder;
import lombok.Data;

@Data
public class PrivateShareholder {
	
	private String prename;
	private String name;
	private String city;
	private int equityShare;
	private long investortypeId;
	private long startupId;
	
	@Builder
	public PrivateShareholder(String prename, String name, String city, int equityShare, long startupId, long investortypeId) {
		this.prename = prename;
		this.name = name;
		this.city = city;
		this.equityShare = equityShare;
		this.investortypeId = investortypeId;
		this.startupId = startupId;
	}
	
	
}

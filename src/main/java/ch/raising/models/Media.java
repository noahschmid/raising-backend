package ch.raising.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Media {
	private long id;
	private long accountId;
	private String media;
	
	public Media(long id, long accountId, String media) {
		this.id = id;
		this.accountId= accountId;
		this.media = media;
	}
	
	public Media() {}
	public Media(String media) {
		this.media = media;
	}
}

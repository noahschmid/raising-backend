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
	private byte[] media;
	
	public Media(long id, long accountId, byte[] media) {
		this.id = id;
		this.accountId= accountId;
		this.media = media;
	}
	
	public Media() {}
	public Media(byte[] media) {
		this.media = media;
	}
}

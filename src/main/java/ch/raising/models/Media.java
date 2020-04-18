package ch.raising.models;

import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Media {
	private long id;
	private long accountId;
	private String contentType;
	private byte[] media;
	/**
	 * 
	 * @param id long
	 * @param accountId long
	 * @param contentType String
	 * @param media byte[]
	 */
	public Media(long id, long accountId, String contentType, byte[] media) {
		this.id = id;
		this.accountId= accountId;
		this.media = media;
		this.contentType = contentType;
	}
	
	public Media() {}
	public Media(byte[] media, String contentType ) {
		this.media = media;
		this.contentType = contentType;
	}
}

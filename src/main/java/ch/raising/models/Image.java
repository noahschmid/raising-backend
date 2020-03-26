package ch.raising.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Image {
	private long id;
	private long accountId;
	private String image;
}

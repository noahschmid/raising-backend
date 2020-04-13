package ch.raising.models.responses;

import lombok.Data;

@Data
public class FileUploadResponse {
	private final String message;
	private final long id;
}

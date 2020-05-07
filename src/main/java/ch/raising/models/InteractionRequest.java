package ch.raising.models;

import ch.raising.models.enums.InteractionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InteractionRequest {
	private long accountId;
	private InteractionType interaction;
	private SharedData data;
}

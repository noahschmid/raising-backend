package ch.raising.models;

import ch.raising.models.enums.InteractionTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InteractionRequest {
	private long accountId;
	private InteractionTypes interaction;
	private SharedData data;
}

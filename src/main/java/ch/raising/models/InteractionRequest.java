package ch.raising.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InteractionRequest {
	private long interactionId;
	private long accountId;
	private InteractionTypes interaction;
	private SharedData data;
}

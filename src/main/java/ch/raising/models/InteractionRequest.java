package ch.raising.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InteractionRequest {
	private long id;
	private InteractionState interaction;
	private Share data;
}

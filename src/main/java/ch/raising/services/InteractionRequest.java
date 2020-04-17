package ch.raising.services;

import ch.raising.models.InteractionState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class InteractionRequest {
	private long id;
	private InteractionState interaction;
}

package ch.raising.models.responses;

import java.util.List;

import ch.raising.models.Interaction;
import ch.raising.models.Relationship;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompleteInteractions {
	private List<Interaction> interactions;
	private List<Relationship> relationships;
}

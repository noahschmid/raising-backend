package ch.raising.models;

import java.sql.Timestamp;

import ch.raising.models.enums.InteractionType;
import ch.raising.models.enums.State;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Interaction {
	@Builder.Default private long id = -1l;
	@Builder.Default private int relationshipId = -1;
	@Builder.Default private long startupId = -1l;
	@Builder.Default private long investorId = -1l;
	private InteractionType interaction;
	private State startupState;
	private State investorState;
	private Timestamp createdAt;
	private Timestamp acceptedAt;
	private SharedData data;

	public Interaction() {}
	public Interaction(long id, int relationshipId, long startupId, long investorId, InteractionType interaction,
			State startupState, State investorState, Timestamp createdAt, Timestamp acceptedAt, SharedData data) {
		this.id = id;
		this.relationshipId = relationshipId;
		this.startupId = startupId;
		this.investorId = investorId;
		this.interaction = interaction;
		this.startupState = startupState;
		this.investorState = investorState;
		this.createdAt = createdAt;
		this.acceptedAt = acceptedAt;
		this.data = data;
	}
}

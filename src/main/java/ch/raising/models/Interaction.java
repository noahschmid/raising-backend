package ch.raising.models;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Interaction {
	@Builder.Default
	private long id = -1l;
	@Builder.Default
	private long startupId = -1l;
	@Builder.Default
	private long investorId = -1l;
	private InteractionTypes interaction;
	private State startupState;
	private State investorState;
	private Timestamp createdAt;
	private Timestamp acceptedAt;
	private SharedData data;
}

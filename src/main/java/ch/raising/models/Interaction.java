package ch.raising.models;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.raising.models.enums.InteractionType;
import ch.raising.models.enums.State;
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
	@JsonIgnore
	private long startupId = -1l;
	@Builder.Default
	@JsonIgnore
	private long investorId = -1l;
	private InteractionType interaction;
	private State startupState;
	private State investorState;
	private Timestamp createdAt;
	private Timestamp acceptedAt;
	private SharedData data;
}

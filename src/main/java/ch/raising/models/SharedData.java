package ch.raising.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharedData {
	@Builder.Default
	private long id = -1l;
	@Builder.Default
	private long accountId = -1l;
	@Builder.Default
	private long interactionId = -1l;
	@Builder.Default
	private String email  ="";
	@Builder.Default
	private String phone = "";
	@Builder.Default
	private long businessPlanId = -1;
}

package ch.raising.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AssignmentTableModelWithDescription extends AssignmentTableModel {
	private String description;
	
	public AssignmentTableModelWithDescription(long id, String name, String description) {
		super(name, id);
		this.description = description;
	}
}

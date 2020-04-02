package ch.raising.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AssignmentTableModelWithDescription extends AssignmentTableModel {
	private String description;
	
	public AssignmentTableModelWithDescription(String name, long id,String description) {
		super(name, id);
		this.description = description;
	}
}
